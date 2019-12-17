package scheduler

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import genetic_algorithm.IndividualPath
import genetic_algorithm.Population
import genetic_algorithm.Product
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.request.receiveStream
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import json_structure.MeshNode
import json_structure.PathResponse
import json_structure.UnityProducts
import json_structure.WorkerRespond
import java.time.LocalDateTime
import java.util.*

/**
 * The [RestService] provides the server and the API for the workers
 */
class RestService {

    private val server: NettyApplicationEngine
    private val scheduler = Scheduler()
    private val gson = Gson()

    init {
        server = embeddedServer(Netty, port = 8080) {
            routing {
                // enable REST-Calls
                get("/ping") {
                    logRequest(call)
                    call.respondText("pong", ContentType.Text.Plain, HttpStatusCode.OK)
                }

                // worker
                post("/worker") {
                    logRequest(call)
                    addWorker(call)
                }
                put("/worker") {
                    logRequest(call)
                    updateWorker(call)
                }
                get("/worker/uuid") {
                    // TODO delete post /worker and get /map and implement
                }
                get("/worker") {
                    logRequest(call)
                    respondPopulation(call)
                }

                // map
                post("/map") {
                    logRequest(call)
                    saveMap(call)
                }

                // path
                get("/path") {
                    logRequest(call)
                    respondPath(call)
                }
            }
        }
    }

    private suspend fun respondPopulation(call: ApplicationCall) {
        val workerId = call.parameters["uuid"]

        if (workerId == null) {
            call.respondText("parameter uuid missing", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            println("worker id missing")
            return
        }

        scheduler.subPopulations.forEach {
            if (it.worker?.uuid.toString() == workerId) {
                val json = gson.toJson(it)
                call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
                return
            }
        }

        call.respondText("uuid is not registered", ContentType.Text.Plain, HttpStatusCode.BadRequest)
    }

    fun start() = server.start(wait = true)

    /**
     * responds:
     *
     *  200 -> found best path
     *
     *  204 -> the path is not yet available
     *
     *  503 -> the path is not yet available and no worker is registered
     */
    private suspend fun respondPath(call: ApplicationCall) {
        when {
            scheduler.demoIndividual.isNotEmpty() -> {
                println("Send best Individual")

                val pathResponse = PathResponse(
                    scheduler.bestIndividual?.getIndividualPathWithoutNulls(),
                    scheduler.demoIndividual.last().getIndividualPathWithoutNulls(),
                    scheduler.bestDistance
                )
                val json = gson.toJson(pathResponse)
                call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
            }
            scheduler.workers.isEmpty() -> call.respondText(
                "Currently no worker is registered, try later",
                    status = HttpStatusCode.NoContent
            )
            else -> call.respondText("The Path is not set yet, try later", ContentType.Text.Plain, HttpStatusCode.NoContent) // TODO change back to ServiceUnavailable
        }
    }

    /**
     * save sent json to map
     */
    private suspend fun saveMap(call: ApplicationCall) {
        try {
//            val string = call.receive<String>()
            val string = call.receiveTextWithCorrectEncoding()
            val items = gson.fromJson(string, UnityProducts::class.java)

            scheduler.products = items.Items
            scheduler.map = items.NavMesh
        } catch (e: Exception) {
            println("Could not read map")
            e.printStackTrace()
        }


        ensureMapAndProducts { products, navMesh ->
            scheduler.createPopulation(products)
            call.respondText(gson.toJson(UnityProducts(products, navMesh)), ContentType.Application.Json, HttpStatusCode.OK)
        } ?: respondJsonError(call)
    }

    private suspend fun ensureMapAndProducts(f: suspend (List<Product>, List<MeshNode>) -> Unit): Unit? {
        scheduler.products?.let { products ->
            scheduler.map?.let { navMesh ->
                f(products, navMesh)
            } ?: return null
        } ?: return null

        return Unit
    }

    private suspend fun respondJsonError(call: ApplicationCall) {
        call.respondText("Could not read json", ContentType.Text.Plain, HttpStatusCode.BadRequest)
        println("Could not read json")
    }

    /**
     * responds map data (200) or error (204) if map is not yet available
     */
    private suspend fun respondMap(call: ApplicationCall) {
        ensureMapAndProducts { products, navMesh ->
            val json = gson.toJson(UnityProducts(products, navMesh))
            println("send map: $json")
            call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
        } ?: call.respondText("Map is not set yet, try later", ContentType.Text.Plain, HttpStatusCode.NoContent)
    }

    /**
     * update [IndividualPath] of the calling worker
     */
    private suspend fun updateWorker(call: ApplicationCall) {
        // check if worker is in list
        val parsedPopulation = parsePopulation(call) ?: return
        val workerId = call.parameters["uuid"]

        if (workerId == null) {
            call.respondText("parameter uuid missing", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            println("worker id missing")
            return
        }

        println("update worker")

        // update worker individual
        var alreadyInList = false
        var respondPopulation: Population? = null

        scheduler.workers.forEach {
            if (it.uuid == UUID.fromString(workerId)) {

                println("Update worker ${it.uuid}")

                // get a new population for the worker
                val newPopulation = scheduler.getSubPopulation() ?: throw NoSuchElementException()

                if (scheduler.subPopulations.any { subPop -> subPop.worker == it }) {
                    it.subPopulation.updateIndividuals(parsedPopulation)
                    scheduler.updateBestIndividual(it.subPopulation)

                    it.changePopulation(newPopulation)
                    scheduler.evolvePopulation()
                } else {
                    it.changePopulation(newPopulation)
                }

                it.timestamp = LocalDateTime.now()
                respondPopulation = it.subPopulation
                alreadyInList = true

                scheduler.printPopulations("Update Worker")
                return@forEach
            }
        }

        scheduler.deleteOldWorkers()

        if (!alreadyInList) {
            call.respondText("Worker is not registered. Use POST instead", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            println("Worker is not registered. Use POST instead - 400")
        }
        else {
            // TODO fix that ugly thingy... (WorkerRespond everything is nullable)
            val json = Gson().toJson(WorkerRespond(population = respondPopulation, navMesh = scheduler.map))
            call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
        }
    }

    /**
     * parse json from [call] and try to add a worker to the list
     */
    private suspend fun addWorker(call: ApplicationCall) {
        val workerAddress = call.request.origin.remoteHost

        // if no map is available no population can be created
        ensureMapAndProducts { _, navMesh ->
            val subPopulation = scheduler.getSubPopulation()

            if (subPopulation == null) {
                call.respondText(
                    "Max worker count is reached: ${Scheduler.WORKER_COUNT} Workers",
                    status = HttpStatusCode.ServiceUnavailable
                )
                return@ensureMapAndProducts
            }

            val worker = Worker(UUID.randomUUID(), workerAddress, subPopulation, LocalDateTime.now())

            // save worker
            scheduler.workers.add(worker)

            val gson = GsonBuilder().serializeNulls().create()
            val json = gson.toJson(WorkerRespond(worker.uuid, worker.subPopulation, navMesh))

            call.respondText(json, ContentType.Application.Json, HttpStatusCode.Created)
        } ?: call.respondText(
            "No map available, therefore no population could be created",
            status = HttpStatusCode.ServiceUnavailable
        )
    }

    /**
     * Try to parse an [Population] from send json in [call].
     * If no [Population] could be parsed the call responds with [HttpStatusCode.BadRequest]
     * @return send [Population] or null
     */
    private suspend fun parsePopulation(call: ApplicationCall): Population? = try {
//        val string = call.receive<String>()
        val string = call.receiveTextWithCorrectEncoding()

        println("popupaltion from worker $string")
        val population = Gson().fromJson<Population>(string, Population::class.java)
        population.paths.forEach { print("$it |") }

        if (population.paths.contains(null) || population.paths.isNullOrEmpty()) {
            call.respondText("invalid population", status = HttpStatusCode.BadRequest)
            println("invalid population - 400")
            null
        } else population
    } catch (e: ContentTransformationException) {
        respondJsonError(call)
        null
    }

    private fun logRequest(call: ApplicationCall) {
        println("${call.request.httpMethod.value} ${call.request.path()} from ${call.request.origin.remoteHost}")
    }
}

/**
 * Receive the request as String.
 * If there is no Content-Type in the HTTP header specified use ISO_8859_1 as default charset, see https://www.w3.org/International/articles/http-charset/index#charset.
 * But use UTF-8 as default charset for application/json, see https://tools.ietf.org/html/rfc4627#section-3
 * from https://github.com/ktorio/ktor/issues/384
 */
private suspend fun ApplicationCall.receiveTextWithCorrectEncoding(): String =
    receiveStream().bufferedReader(charset = Charsets.UTF_8).readText()
