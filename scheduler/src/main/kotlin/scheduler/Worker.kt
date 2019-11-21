package scheduler

import genetic_algorithm.Population
import java.time.LocalDateTime
import java.util.*

data class Worker(
    val uuid: UUID,
    val ipAddress: String,
    var subPopulation: Population,
    var timestamp: LocalDateTime
) {
    fun changePopulation(newPopulation: Population) {
        subPopulation.worker = null
        subPopulation = newPopulation
        newPopulation.worker = this
    }

    init {
        subPopulation.worker = this
    }
}