package scheduler

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import genetic_algorithm.Population
import genetic_algorithm.Product
import io.ktor.application.Application
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
import io.ktor.routing.*
import json_structure.MeshNode
import json_structure.PathResponse
import json_structure.UnityMapStructure
import json_structure.WorkerRespond
import utility.get
import java.time.LocalDateTime
import java.util.*
import kotlin.NoSuchElementException

/**
 * The [RestService] provides the server and the API for the workers
 */
object RestService {

    private val scheduler = Scheduler()
    private val gson = Gson()

    fun initRouting(application: Application) {
        application.routing {
            // enable REST-Calls
            get("/ping") {
                call.logRequest()
                call.respondText("pong", ContentType.Text.Plain, HttpStatusCode.OK)
            }

            // worker
            get("/worker") {
                call.logRequest()
                call.parameters["uuid"]?.let { respondPopulation(call, it) } ?: addWorker(call)
            }
            put("/worker") {
                call.logRequest()
                updateWorker(call)
            }

            // map
            post("/map") {
                call.logRequest()
                saveMap(call)
            }
            delete("/map") {
                call.logRequest()
                deleteMap(call)
            }

            // path
            get("/path") {
                call.logRequest()
                respondPath(call)
            }
        }
    }

    /**
     * parse json from [call] and try to add a worker to the list
     */
    private suspend fun addWorker(call: ApplicationCall) {
        val workerAddress = call.request.origin.remoteHost

        if(!calculationIsRunning(call) ) return

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
     * respond [Population] to [Worker] with uuid == [workerId]
     */
    private suspend fun respondPopulation(call: ApplicationCall, workerId: String) {
        val worker = scheduler.workers.get { it.uuid == UUID.fromString(workerId) }
        if (worker == null) {
            call.respondText("uuid is not registered", ContentType.Text.Plain, HttpStatusCode.BadRequest)
            return
        }

        // return population if set
        scheduler.subPopulations.forEach {
            if (it.worker?.uuid.toString() == workerId) {
                val json = gson.toJson(it)
                call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
                return
            }
        }

        // try to utility.get new population
        val population = scheduler.getSubPopulation()

        if (population == null) {
            call.respondText("No population available", ContentType.Text.Plain, HttpStatusCode.NoContent)
            return
        }

        worker.changePopulation(population)

        val json = gson.toJson(population, Population::class.java)
        call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
    }

    /**
     * update [Population] of the calling worker and respond new one
     */
    private suspend fun updateWorker(call: ApplicationCall) {
        // check if worker is in list
        val parsedPopulation = parsePopulation(call) ?: return
        print("ParsePopulation: Distances: ")
        parsedPopulation.paths.forEach { print("${it?.distance} |") }
        println("")
        val workerId = call.parameters["uuid"]

        if (workerId == null) {
            call.respondText("parameter uuid missing", ContentType.Text.Plain, HttpStatusCode.NotFound)
            println("worker id missing")
            return
        }

        // update worker individual
        var alreadyInList = false
        var respondPopulation: Population? = null

        scheduler.workers.forEach { updatingWorker ->
            if (updatingWorker.uuid == UUID.fromString(workerId)) {

                println("Update worker ${updatingWorker.uuid}")
                if (!calculationIsRunning(call)) {
                    updatingWorker.timestamp = LocalDateTime.now()
                    return
                }

                // utility.get a new population for the worker
                val newPopulation = scheduler.getSubPopulation() ?: throw NoSuchElementException()

                if (scheduler.subPopulations.any { subPop -> subPop.worker == updatingWorker }) {
                    updatingWorker.subPopulation.updateIndividuals(parsedPopulation)
                    scheduler.updateBestIndividual(updatingWorker.subPopulation)

                    updatingWorker.changePopulation(newPopulation)
                    scheduler.evolvePopulation()
                } else {
                    updatingWorker.changePopulation(newPopulation)
                }

                updatingWorker.timestamp = LocalDateTime.now()
                respondPopulation = updatingWorker.subPopulation
                alreadyInList = true

                scheduler.printPopulations("Worker update")
                return@forEach
            }
        }

        scheduler.deleteOldWorkers()

        if (!alreadyInList) {
            call.respondText("Worker is not registered. Use POST instead", ContentType.Text.Plain, HttpStatusCode.Forbidden)
            println("Worker is not registered. Use POST instead - 400")
        }
        else {
            // TODO fix that ugly thingy... (WorkerRespond everything is nullable)
            val json = Gson().toJson(WorkerRespond(population = respondPopulation, navMesh = scheduler.map))
            call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
        }
    }

    /**
     * save sent json to map
     */
    private suspend fun saveMap(call: ApplicationCall) {
        try {
            val string = call.receiveTextWithCorrectEncoding()
            val unityData = gson.fromJson(string, UnityMapStructure::class.java)

            scheduler.products = unityData.products
            scheduler.map = unityData.navMesh
            scheduler.calculationRunning = true
        } catch (e: Exception) {
            println("Could not read map")
            e.printStackTrace()
        }


        ensureMapAndProducts { products, navMesh ->
            scheduler.createPopulation(products)
            call.respondText(gson.toJson(UnityMapStructure(products, navMesh)), ContentType.Application.Json, HttpStatusCode.OK)
        } ?: respondJsonError(call)
    }

    /**
     * delete map and set calculationRunning flag to false
     */
    private suspend fun deleteMap(call: ApplicationCall) {
        scheduler.bestDistance = Int.MAX_VALUE
        scheduler.bestIndividual = null
        scheduler.subPopulations.clear()
        scheduler.demoIndividual.clear()

        scheduler.calculationRunning = false

        println("Map has been deleted")
        call.respondText("Current map has been deleted", ContentType.Text.Plain, HttpStatusCode.OK)
    }

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
                    scheduler.demoIndividual.first().getIndividualPathWithoutNulls(),
                    scheduler.bestDistance
                )
                val json = gson.toJson(pathResponse)
                call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
            }
            scheduler.workers.isEmpty() -> call.respondText(
                "Currently no worker is registered, try later",
                status = HttpStatusCode.NoContent
            )
            else -> call.respondText("The Path is not set yet, try later", ContentType.Text.Plain, HttpStatusCode.ServiceUnavailable)
        }
    }

    /**
     * Check if a calculation is running, if not respond [HttpStatusCode.NoContent] to [call]
     * @return true if a calculation is running
     */
    private suspend fun calculationIsRunning(call: ApplicationCall): Boolean {
        if (!scheduler.calculationRunning)
            call.respondText("Map not available", ContentType.Text.Plain, HttpStatusCode.NoContent)

        return scheduler.calculationRunning
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
     * Try to parse an [Population] from send json in [call].
     * If no [Population] could be parsed the call responds with [HttpStatusCode.BadRequest]
     * @return send [Population] or null
     */
    private suspend fun parsePopulation(call: ApplicationCall): Population? = try {
        val string = call.receiveTextWithCorrectEncoding()

        val population = Gson().fromJson(string, Population::class.java)

        if (population.paths.contains(null) || population.paths.isNullOrEmpty()) {
            call.respondText("invalid population", status = HttpStatusCode.BadRequest)
            println("invalid population - 400")
            null
        } else population
    } catch (e: ContentTransformationException) {
        respondJsonError(call)
        null
    }

    private fun ApplicationCall.logRequest() {
        println("${request.httpMethod.value} ${request.path()} from ${request.origin.remoteHost}")
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
