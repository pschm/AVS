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
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
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

    init {
        server = embeddedServer(Netty, port = 8080) {
            routing {
                // enable REST-Calls
                get("/ping") {
                    val host = call.request.origin.remoteHost
                    println("Ping-Request from $host")
                    call.respondText("pong")
                }

                // worker
                post("/worker") {
                    println("POST /worker from: ${call.request.origin.remoteHost}")
                    addWorker(call)
                }
                put("/worker") {
                    println("PUT /worker from: ${call.request.origin.remoteHost}")
                    updateWorker(call)
                }

                // map
                get("/map") {
                    println("GET /map from: ${call.request.origin.remoteHost}")
                    respondMap(call)
                }
                post("/map") {
                    println("POST /map from: ${call.request.origin.remoteHost}")
                    saveMap(call)
                }

                // path
                get("/path") {
                    println("GET /path from: ${call.request.origin.remoteHost}")
                    respondPath(call)
                }
            }
        }
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
            scheduler.bestIndividual != null -> {
                println("Send best Individual")

                val mList = mutableListOf<Product>()
                scheduler.bestIndividual?.IndividualPath?.forEach {p ->
                    p?.let { mList.add(it) }
                }
                val unityProducts = UnityProducts(mList)
                val json = Gson().toJson(unityProducts)
                call.respondText(json, status = HttpStatusCode.OK, contentType = ContentType.Application.Json)
            }
            scheduler.workers.isEmpty() -> call.respondText(
                "Currently no worker is registered, try later",
                    status = HttpStatusCode.NoContent
            )
            else -> call.respondText("The Path is not set yet, try later", status = HttpStatusCode.NoContent)
        }
    }

    /**
     * save sent json to map
     */
    private suspend fun saveMap(call: ApplicationCall) {
        scheduler.map = try {
            val string = call.receive<String>()
            val items = Gson().fromJson<UnityProducts>(string, UnityProducts::class.java)

            items.Items
        } catch (e: Exception) {
            println("Could not read map")
            e.printStackTrace()
            null
        }

        scheduler.map?.let {
            scheduler.createPopulation(it)
            call.respondText(Gson().toJson(UnityProducts(it)), status = HttpStatusCode.OK)
        } ?: call.respondText("Could not read json", status = HttpStatusCode.BadRequest)
    }

    /**
     * responds map data (200) or error (204) if map is not yet available
     */
    private suspend fun respondMap(call: ApplicationCall) {
        scheduler.map?.let {
            val json = Gson().toJson(UnityProducts(it))
            println("send map: $json")
            call.respondText(json, status = HttpStatusCode.OK)
        } ?: call.respondText("Map is not set yet, try later", status = HttpStatusCode.NoContent)
    }

    /**
     * update [IndividualPath] of the calling worker
     */
    private suspend fun updateWorker(call: ApplicationCall) {
        // check if worker is in list
        val parsedPopulation = parsePopulation(call) ?: return
        val workerId = call.parameters["uuid"]

        if (workerId == null) {
            call.respondText("parameter uuid missing", status = HttpStatusCode.BadRequest)
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

        if (!alreadyInList) {
            call.respondText("Worker is not registered. Use POST instead", status = HttpStatusCode.BadRequest)
            println("Worker is not registered. Use POST instead - 400")
        }
        else {
            val json = Gson().toJson(respondPopulation)
            call.respondText(json, status = HttpStatusCode.OK)
        }
    }

    /**
     * parse json from [call] and try to add a worker to the list
     */
    private suspend fun addWorker(call: ApplicationCall) {
        val workerAddress = call.request.origin.remoteHost

        // if no map is available no population can becreated
        if  (scheduler.map == null) {
            call.respondText(
                "No map available, therefore no population could be created",
                status = HttpStatusCode.ServiceUnavailable
            )
            return
        }

        val subPopulation = scheduler.getSubPopulation()

        if (subPopulation == null) {
            call.respondText(
                "Max worker count is reached: ${Scheduler.WORKER_COUNT} Workers",
                status = HttpStatusCode.ServiceUnavailable
            )
            return
        }

        val worker = Worker(UUID.randomUUID(), workerAddress, subPopulation, LocalDateTime.now())

        // save worker
        scheduler.workers.add(worker)


        val gson = GsonBuilder().serializeNulls().create()
        val json = gson.toJson(WorkerRespond(worker.uuid, worker.subPopulation))

        call.respondText(json, status = HttpStatusCode.OK)
    }

    /**
     * Try to parse an [Population] from send json in [call].
     * If no [Population] could be parsed the call responds with [HttpStatusCode.BadRequest]
     * @return send [Population] or null
     */
    private suspend fun parsePopulation(call: ApplicationCall): Population? = try {
        val string = call.receive<String>()

        println("popupaltion from worker $string")
        val population = Gson().fromJson<Population>(string, Population::class.java)
        population.paths.forEach { print("$it |") }

        if (population.paths.contains(null) || population.paths.isNullOrEmpty()) {
            call.respondText("invalid population", status = HttpStatusCode.BadRequest)
            println("invalid population - 400")
            null
        } else population
    } catch (e: ContentTransformationException) {
        call.respondText("couldn't read json", status = HttpStatusCode.BadRequest)
        println("could read json - 400")
        null
    }
}