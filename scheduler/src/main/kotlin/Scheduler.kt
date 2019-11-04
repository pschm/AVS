import genetic_algorithm.Individual
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

/**
 * The [Scheduler] provides the server and the API for the workers
 */
class Scheduler(private val unityUrl: String) {

    private val server : NettyApplicationEngine
    private val workers = mutableListOf<Worker>()
    private var bestIndividual: Individual? = null

    init {
        server = embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    val host = call.request.origin.host
                    println("Request from $host")
                    call.respondText("Hello World")
                }

                post("/worker") {
                    // parse Individual from json
                    val individual = try {
                        call.receive<Individual>()
                    } catch (e: ContentTransformationException) {
                        call.respondText("couldn't update worker, check json", status = HttpStatusCode.BadRequest)
                        return@post
                    }

                    val workerAddress = call.request.origin.host
                    val worker = updateOrAddWorker(workerAddress, individual)

                    // TODO check, if fitness is always greater than 0
                    if (worker.individual.fitness > bestIndividual?.fitness ?: -1) {
                        bestIndividual = worker.individual
                        sendIndividualToUnity(bestIndividual)
                    }

                    call.respondText("updated worker individual", status = HttpStatusCode.OK)
                }

                get("/map") {
                    call.respondText(getMapJsonFromUnity())
                }
            }
        }
    }

    /**
     * send new individual to the server and print server feedback
     *
     * TODO should the scheduler transform the Individual to a route?
     */
    private suspend fun sendIndividualToUnity(individual: Individual?) {
        val bodyContent = individual ?: return

        val client = HttpClient()
        val urlString = "$unityUrl/route" // TODO use correct jsonPath

        val message = client.post<Individual> {
            url(urlString)
            contentType(ContentType.Application.Json)
            body = bodyContent
        }

        // comment for debug
        println("CLIENT: Message from the server: $message")

        client.close()
    }

    fun start() = server.start(wait = true)

    private suspend fun getMapJsonFromUnity(): String {
        val client = HttpClient()

        // TODO use correct jsonPath
        return client.get("$unityUrl/map")
    }

    /**
     * update [individual] of the worker with given [ipAddress] or add a new worker to [workers]
     *
     * @return updated or newly created [Worker]
     */
    private fun updateOrAddWorker(ipAddress: String, individual: Individual): Worker {
        workers.any {
            if (it.ipAddress == ipAddress) {
                it.individual = individual
                return it
            }
            else false
        }

        val worker = Worker(ipAddress, individual)
        workers.add(worker)

        return worker
    }

}