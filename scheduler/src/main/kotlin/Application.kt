import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import scheduler.RestService

fun main() {
    embeddedServer(Netty, port = 8080) {
        RestService.initRouting(this)
    }.start(wait = true)
}
