package scheduler

import genetic_algorithm.IndividualPath
import genetic_algorithm.PathManager
import genetic_algorithm.Population
import genetic_algorithm.Product
import kotlin.math.pow
import kotlin.random.Random

class Scheduler {

    companion object {
        const val WORKER_COUNT = 12
        const val POPULATION_SIZE = 200 // min (WORKER_COUNT+1)²
    }

    val workers = mutableListOf<Worker>()
    var bestIndividual: IndividualPath? = null
    var map: List<Product>? = null
    val subPopulations = mutableListOf<Population>()

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

        val subPopSize = populationSize / (WORKER_COUNT+1)
        repeat(WORKER_COUNT+1) { _ ->
            val subPopulation = Population(subPopSize, false)
            val individuals = arrayListOf<IndividualPath?>()

            val paths = masterPopulation.getPaths()
            repeat(subPopSize) { _ ->
                val individual = paths.removeAt(0)
                individual?.let { individuals.add(it) }
            }
            subPopulation.setPaths(individuals)
            masterPopulation.setPaths(paths)

            subPopulations.add(subPopulation)
        }
        subPopulations.last().getPaths().addAll(masterPopulation.paths)
    }

    fun getSubPopulation(): Population? {

        if (workers.size + 1 > subPopulations.size) {
            println("Worker size to large")
            return null
        }

        for (it in subPopulations)
            if (it.worker == null) return it

//        subPopulations.forEach {
//            if (it.worker == null) {
//                return it
//            }
//        }

        println("could not find free population :/")
        return null
    }

    fun evolvePopulation(worker: Worker) {
        // get a new population for the worker
        val newPopulation = getSubPopulation() ?: throw NoSuchElementException()
        worker.changePopulation(newPopulation)

        // evolve
        var lastPopulation = subPopulations.last()
        subPopulations.forEach {
            val paths = it.getPaths()
            paths.sortBy { individualPath -> individualPath?.distance }
            paths.removeAt(paths.size - 1)
            paths.add(lastPopulation.getPaths().get(Random.nextInt(0, lastPopulation.populationSize()-1)))
            it.setPaths(paths)
            lastPopulation = it
        }
    }

}