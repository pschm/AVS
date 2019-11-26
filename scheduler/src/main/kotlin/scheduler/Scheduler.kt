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

    /**
     * Create [WORKER_COUNT]+1 sub populations and save them in [subPopulations]
     */
    fun createPopulation(products: List<Product>) {
        subPopulations.clear()
        products.forEach { PathManager.addProduct(it) }

        // calc populationSize (should max be product.size²)
        var populationSize = POPULATION_SIZE
        val maxSize = products.size.toDouble().pow(products.size).toInt()
        if (populationSize > maxSize)
            populationSize = maxSize

        val masterPopulation = Population(populationSize, true)

        val subPopSize = populationSize / (WORKER_COUNT+1)
        repeat(WORKER_COUNT+1) {
            val subPopulation = Population(subPopSize, false)
            val individuals = arrayListOf<IndividualPath?>()

            val paths = masterPopulation.getPaths()
            repeat(subPopSize) {
                val individual = paths.removeAt(0)
                individual?.let { individuals.add(it) }
            }
            subPopulation.setPaths(individuals)
            masterPopulation.setPaths(paths)

            subPopulations.add(subPopulation)
        }
        subPopulations.last().getPaths().addAll(masterPopulation.paths)

        printPopulations("Created following subPopulations")
    }

    /**
     * @return [Population] from subPopulations which has no worker attached to it.
     * Returns if the there are more workers than subPopulations or if no free population was found
     */
    fun getSubPopulation(): Population? {
        if (workers.size + 1 > subPopulations.size) {
            println("Worker size to large")
            return null
        }

        val freePopulations = mutableListOf<Population>()
        for (it in subPopulations)
            if (it.worker == null) freePopulations.add(it)

        if (freePopulations.isNotEmpty())
            return freePopulations[Random.nextInt(0, freePopulations.size)]

        println("could not find free population :/")
        return null
    }

    /**
     * Evolves all [subPopulations] based on nearest neighbor immigrant principle
     * and changes the [Population] of the worker
     */
    fun evolvePopulation(worker: Worker) {
        // get a new population for the worker
        val newPopulation = getSubPopulation() ?: throw NoSuchElementException()
        worker.changePopulation(newPopulation)

        // evolve
        var lastPopulation = subPopulations.last()
        subPopulations.forEach {
            val paths = it.getPaths()
            // sort all individuals according to the best path
            paths.sortBy { individualPath -> individualPath?.distance }
            // remove the worst individual
            paths.removeAt(paths.size - 1)
            // add random new individual from neighbor population
            paths.add(lastPopulation.getPaths()[Random.nextInt(0, lastPopulation.populationSize())])

            it.setPaths(paths)
            lastPopulation = it
        }
    }

    private fun printPopulations(header: String) {
        println(header)
        subPopulations.forEach { println(it.getPaths()) }
        println("---------------")
    }
}