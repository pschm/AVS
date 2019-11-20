import scheduler.RestService

fun main() {
    println("Scheduler")

    val scheduler = RestService()
    scheduler.start()
}