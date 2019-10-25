import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

/**
 * The [Scheduler] provides the server and the API for the workers
 */
class Scheduler {

    private val server : NettyApplicationEngine

    init {
        server = embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    call.respondText("Hello World.")
                }

                get("/map") {
                    call.respondText(getMapJsonFromUnity())
                }
            }
        }
    }

    fun start() = server.start(wait = true)

    private fun getMapJsonFromUnity(): String {
        // TODO call unity-api and return map json
        return "Map JSON"
    }
}