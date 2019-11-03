fun main() {
    println("Scheduler")

    val unityAddress = "192.168.0.101"

    val scheduler = Scheduler(unityAddress)
    scheduler.start()
}