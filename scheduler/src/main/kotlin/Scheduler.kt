import com.google.gson.Gson
import genetic_algorithm.IndividualPath
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.origin
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
import java.time.LocalDateTime
import java.util.*

/**
 * The [Scheduler] provides the server and the API for the workers
 */
class Scheduler {

    private val server: NettyApplicationEngine
    private val workers = mutableListOf<Worker>()
    private var bestIndividual: IndividualPath? = null
    private var map: String? = null
        get() = if (field.isNullOrBlank()) null else field


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
            bestIndividual != null -> {
                val json = Gson().toJson(bestIndividual)
                call.respondText(json, status = HttpStatusCode.OK)
            }
            workers.isEmpty() -> call.respondText(
                "Currently no worker is registered, try later",
                    status = HttpStatusCode.ServiceUnavailable
            )
            else -> call.respondText("The Path is not set yet, try later", status = HttpStatusCode.NoContent)
        }
    }

    /**
     * save sent json to map
     */
    private suspend fun saveMap(call: ApplicationCall) {
        map = try {
            call.receive()
        } catch (e: Exception) {
            println("Could not read map")
            e.printStackTrace()
            null
        }

        map?.let {
            call.respondText(it, status = HttpStatusCode.OK)
        } ?: call.respondText("Could not read json", status = HttpStatusCode.BadRequest)
    }

    /**
     * responds map data (200) or error (204) if map is not yet available
     */
    private suspend fun respondMap(call: ApplicationCall) {
        map?.let {
            println("send map: $map")
            call.respondText(it, status = HttpStatusCode.OK)
        } ?: call.respondText("Map is not set yet, try later", status = HttpStatusCode.NoContent)
    }

    /**
     * update [IndividualPath] of the calling worker
     */
    private suspend fun updateWorker(call: ApplicationCall) {
        // check if worker is in list
        val individual = parseIndividual(call) ?: return
        val workerId = call.parameters["uuid"]

        if (workerId == null) {
            call.respondText("parameter uuid missing", status = HttpStatusCode.BadRequest)
            return
        }

        // update worker individual
        var alreadyInList = false
        workers.forEach {
            if (it.uuid == UUID.fromString(workerId)) {
                it.individual = individual
                it.timestamp = LocalDateTime.now()
                updateBestIndividual(individual)
                alreadyInList = true
                return@forEach
            }
        }

        if (alreadyInList)
            call.respondText("Worker is not registered. Use POST instead", status = HttpStatusCode.BadRequest)
        else
            call.respondText("updated individual successfully", status = HttpStatusCode.OK)
    }

    /**
     * parse json from [call] and try to add a worker to the list
     */
    private suspend fun addWorker(call: ApplicationCall) {
        val individual = parseIndividual(call) ?: return
        val workerAddress = call.request.origin.remoteHost
        val worker = Worker(UUID.randomUUID(), workerAddress, individual, LocalDateTime.now())

        // check if the worker is already registered
        if (workers.any { it.uuid == worker.uuid }) {
            call.respondText("Worker is already registered. Use PUT instead", status = HttpStatusCode.BadRequest)
            return
        }

        // save worker
        workers.add(worker)

        // update best individual
        updateBestIndividual(worker.individual)

        call.respondText(worker.uuid.toString(), status = HttpStatusCode.OK)
    }

    /**
     * Try to parse an [IndividualPath] from send json in [call].
     * If no [IndividualPath] could be parsed the call responds with [HttpStatusCode.BadRequest]
     * @return send [IndividualPath] or null
     */
    private suspend fun parseIndividual(call: ApplicationCall): IndividualPath? = try {
        call.receive<IndividualPath>()
    } catch (e: ContentTransformationException) {
        call.respondText("couldn't read json", status = HttpStatusCode.BadRequest)
        null
    }

    /**
     * update [bestIndividual] if [individual] has better fitness
     */
    private fun updateBestIndividual(individual: IndividualPath) {
        bestIndividual = bestIndividual?.let {
            if (individual.distance < it.distance) individual
            else it
        } ?: individual
    }
}