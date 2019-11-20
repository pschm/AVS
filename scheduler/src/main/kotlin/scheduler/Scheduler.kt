package scheduler

import genetic_algorithm.IndividualPath
import genetic_algorithm.PathManager
import genetic_algorithm.Population
import genetic_algorithm.Product
import kotlin.math.pow

class Scheduler {

    companion object {
        const val WORKER_COUNT = 12
        const val POPULATION_SIZE = 500
    }

    val workers = mutableListOf<Worker>()
    var bestIndividual: IndividualPath? = null
    var map: List<Product>? = null
    private val subPopulations = mutableListOf<Population>()

    /**
     * update [bestIndividual] if [population] has better fitness
     */
    fun updateBestIndividual(population: Population) {

        // best individual of the given population
        val individual = population.getFittest()

        bestIndividual = individual?.let {
            if (individual.distance < it.distance) individual
            else bestIndividual
        } ?: bestIndividual
    }

    fun createPopulation(products: List<Product>) {
        subPopulations.clear()
        products.forEach { PathManager.addProduct(it) }

        var populationSize = POPULATION_SIZE
        val maxSize = products.size.toDouble().pow(products.size).toInt()
        if (populationSize > maxSize)
            populationSize = maxSize

        val masterPopulation = Population(populationSize, true)

        val subPopSize = populationSize / WORKER_COUNT
        repeat(WORKER_COUNT) { _ ->
            val subPopulation = Population(subPopSize, false)
            val individuals = arrayListOf<IndividualPath?>()

            repeat(subPopSize) { index ->
                val individual = masterPopulation.getPaths()?.removeAt(index)
                individual?.let { individuals.add(it) }
            }
            subPopulation.setPaths(individuals)

            subPopulations.add(subPopulation)
        }
        subPopulations.last().getPaths()?.addAll(masterPopulation.paths)
    }

    fun getSubPopulation(): Population? {

        if (workers.size + 1 > subPopulations.size)
            return null

        subPopulations.forEach {
            if (it.worker == null) {
                return it
            }
        }

        return null
    }


}