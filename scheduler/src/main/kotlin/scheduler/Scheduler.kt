package scheduler

import genetic_algorithm.IndividualPath
import genetic_algorithm.PathManager
import genetic_algorithm.Population
import genetic_algorithm.Product
import json_structure.MeshNode
import java.time.LocalDateTime
import java.util.*
import kotlin.math.pow
import kotlin.random.Random

class Scheduler {

    companion object {
        const val WORKER_COUNT = 12
        const val POPULATION_SIZE = 1200 // subpopsize = 200/12 min (WORKER_COUNT+1)²
        const val WORKER_RESPONSE_TIME = 2 // time in minutes
        const val DEMO_INDIVIDUAL_SIZE = 90 // TODO test sizes
        const val MIN_DELTA = 10
        const val INDIVIDUAL_EXCHANGE_COUNT = 10
    }

    @get:Synchronized
    val workers =  mutableListOf<Worker>()
    var bestIndividual: IndividualPath? = null
    val demoIndividual = mutableListOf<IndividualPath>()
    var bestDistance = 0.0

    var calculationRunning = false

    var products: List<Product>? = null
    var map: List<MeshNode>? = null
    val subPopulations = mutableListOf<Population>()

    /**
     * update [bestIndividual] if [population] has better fitness
     */
    fun updateBestIndividual(population: Population) {
        // best individual of the given population
        val individual = population.getBestIndividual()

        individual?.let { addDemoIndividual(it) } ?: return

        if (demoIndividual.size >= DEMO_INDIVIDUAL_SIZE) {
            println("Demo worst: ${demoIndividual.last().distance}; Demo best: ${demoIndividual.first().distance}")
            val delta = demoIndividual.last().distance - demoIndividual.first().distance
            if (delta < MIN_DELTA) bestIndividual = demoIndividual.first()
        }

        println("Currently shortest Distance: ${demoIndividual.firstOrNull()?.distance} (DemoResult=${bestIndividual == null})")
//        println("BEST distance: ${bestIndividual?.distance})")
//        println("DEMO distance: ${demoIndividual.firstOrNull()?.distance})")
//        println("new distance ${individual.distance})")
    }

    private fun addDemoIndividual(individual: IndividualPath) {
        if (demoIndividual.size <= DEMO_INDIVIDUAL_SIZE) demoIndividual.add(individual)
        else if (individual.distance < demoIndividual.last().distance) {
            demoIndividual.removeAt(demoIndividual.size - 1)
            demoIndividual.add(individual)
            println("new distance ${individual.distance})")
        }

        demoIndividual.sortBy { it.distance }
        bestDistance = demoIndividual.first().distance
    }

    /**
     * Create [WORKER_COUNT]+1 sub populations and save them in [subPopulations]
     */
    fun createPopulation(products: List<Product>) {
        subPopulations.clear()
        demoIndividual.clear()
        bestIndividual = null
        PathManager.clear()
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
     * Returns null if the there are more workers than subPopulations or if no free population was found
     */
    fun getSubPopulation(): Population? {
        if (workers.size + 1 > subPopulations.size && calculationRunning) {
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
    fun evolvePopulation() {
        // evolve
        var lastPopulation = subPopulations.last()
        subPopulations.forEach {
            val paths = it.getPaths()
            // sort all individuals according to the best path
            paths.sortBy { individualPath -> individualPath?.distance }

            repeat(INDIVIDUAL_EXCHANGE_COUNT) {
                // remove the worst individual
                paths.removeAt(paths.size - 1)
                // add random new individual from neighbor population
                paths.add(0, lastPopulation.getPaths()[Random.nextInt(0, lastPopulation.populationSize())])
            }

            it.setPaths(paths)
            lastPopulation = it
        }
    }

    fun printPopulations(header: String) {
        println("---------------")
        println("Event: $header")
        println("Current Worker:")
        workers.forEach { println("${it.uuid} (${it.subPopulation.getBestIndividual()?.distance})") }
        println("---")
        println("Current subPopulations:")
        subPopulations.forEach { population ->
            val individual = population.getBestIndividual()
            println("Distance: ${individual?.distance ?: "null"} - Individual: $individual")
        }
        println("---------------")
    }

    fun deleteOldWorkers() {
        val now = LocalDateTime.now()
        val workerId = mutableListOf<UUID>();
        for (worker in workers) {
            if(worker.timestamp.isBefore(now.minusMinutes(WORKER_RESPONSE_TIME.toLong()))){
                workerId.add(worker.uuid);
            }
        }
        subPopulations.forEach{
            if(workerId.contains(it.worker?.uuid)){
                it.worker = null;
            }
        }
        workers.removeAll{workerId.contains(it.uuid)}
    }
}