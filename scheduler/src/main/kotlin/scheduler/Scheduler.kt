package scheduler

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import genetic_algorithm.IndividualPath
import genetic_algorithm.PathManager
import genetic_algorithm.Population
import genetic_algorithm.Product
import json_structure.MeshNode
import mu.KotlinLogging
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.util.*
import kotlin.math.pow
import kotlin.random.Random


class Scheduler {

    companion object {
        val config = SchedulerConfig.getInstance()
    }

    private val logger = KotlinLogging.logger {}

    @get:Synchronized
    val workers =  mutableListOf<Worker>()
    var bestIndividual: IndividualPath? = null
    val demoIndividual = mutableListOf<IndividualPath>()
    var bestDistance = 0.0

    var calculationRunning = false

    var products: List<Product>? = null
    var map: List<MeshNode>? = null
    val subPopulations = mutableListOf<Population>()

    //Testing variables
    var start: Long = 0
    var finish: Long = 0
    var distanceJsonObject:DistancesOverTime? = null

    /**
     * update [bestIndividual] if [population] has better fitness
     */
    fun updateBestIndividual(population: Population) {
        // best individual of the given population
        val individual = population.getBestIndividual()

        individual?.let { addDemoIndividual(it) } ?: return
        createJsonForConfiguration();
        if (demoIndividual.size >= config.DEMO_INDIVIDUAL_SIZE) {

            logger.debug { "Demo worst: ${demoIndividual.last().distance}; Demo best: ${demoIndividual.first().distance}" }
            val delta = demoIndividual.last().distance - demoIndividual.first().distance
            if (delta <= config.MIN_DELTA) {
                bestIndividual = demoIndividual.first()
            }
        }

        logger.info { "Currently shortest Distance: ${demoIndividual.firstOrNull()?.distance} (DemoResult=${bestIndividual == null})" }
    }

    private fun addDemoIndividual(individual: IndividualPath) {
        if (demoIndividual.size <= config.DEMO_INDIVIDUAL_SIZE) demoIndividual.add(individual)
        else if (individual.distance < demoIndividual.last().distance) {
            demoIndividual.removeAt(demoIndividual.size - 1)
            demoIndividual.add(individual)
        }

        demoIndividual.sortBy { it.distance }
        bestDistance = demoIndividual.first().distance

        finish = System.currentTimeMillis()
        var timeDelta = (finish - start)/1000 //sekunden
        distanceJsonObject = DistancesOverTime(bestDistance,timeDelta)
        start = finish;
    }

    private fun createJsonForConfiguration() {
        val file = File("src/main/resources/distancesOverTime.json")
        var distances:MutableList<DistancesOverTime> = mutableListOf()
        val gson = Gson()
        if(file.exists()) {
            val fr = FileReader(file.absolutePath)
            val jsonReader = JsonReader(fr)
            val parser = JsonParser();
            val jsonarray: JsonArray = parser.parse(jsonReader).asJsonArray

            jsonarray.forEach{
                distances.add(gson.fromJson(it, DistancesOverTime::class.java))
            }
            println(distances.size)
        }
        distances.add(distanceJsonObject!!)
        println(file.exists())
       // val json = gson.toJson(distances)
        val json = gson.toJson(distances)
        val writer = FileWriter(file)
        writer.write(json)
        writer.close()
    }

    /**
     * Create [SchedulerConfig.WORKER_COUNT]+1 sub populations and save them in [subPopulations]
     */
    fun createPopulation(products: List<Product>) {
        subPopulations.clear()
        demoIndividual.clear()
        bestIndividual = null
        PathManager.clear()
        products.forEach { PathManager.addProduct(it) }

        // calc populationSize (should max be product.size²)
        var populationSize = config.POPULATION_SIZE
        val maxSize = products.size.toDouble().pow(products.size).toInt()
        if (populationSize > maxSize)
            populationSize = maxSize

        val masterPopulation = Population(populationSize, true)

        val subPopSize = populationSize / (config.WORKER_COUNT+1)
        repeat(config.WORKER_COUNT+1) {
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

        displaySchedulerStatus("Created following subPopulations")
    }

    /**
     * @return [Population] from subPopulations which has no worker attached to it.
     * Returns null if the there are more workers than subPopulations or if no free population was found
     */
    fun getSubPopulation(): Population? {
        if (workers.size + 1 > subPopulations.size && calculationRunning) {
            logger.warn { "Worker size to large." }
            return null
        }

        val freePopulations = mutableListOf<Population>()
        for (it in subPopulations)
            if (it.worker == null) freePopulations.add(it)

        if (freePopulations.isNotEmpty())
            return freePopulations[Random.nextInt(0, freePopulations.size)]

        logger.warn { "Could not find free population." }
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

            repeat(config.INDIVIDUAL_EXCHANGE_COUNT) {
                // remove the worst individual
                paths.removeAt(paths.size - 1)
                // add random new individual from neighbor population
                paths.add(0, lastPopulation.getPaths()[Random.nextInt(0, lastPopulation.populationSize())])
            }

            it.setPaths(paths)
            lastPopulation = it
        }
    }

    fun displaySchedulerStatus(header: String) {
        val schedulerStatus = StringBuilder()
        schedulerStatus.append("Scheduler Status (Event: $header) \n")
        schedulerStatus.append("Current Worker:\n")
        workers.forEach { schedulerStatus.append("${it.uuid} (${it.subPopulation.getBestIndividual()?.distance})\n") }
        schedulerStatus.append("---\n")
        schedulerStatus.append("Current subPopulations:\n")
        subPopulations.forEach { population ->
            val individual = population.getBestIndividual()
            schedulerStatus.append("Distance: ${individual?.distance ?: "null"} - Individual: $individual\n")
        }

        logger.debug { schedulerStatus }
    }

    fun deleteOldWorkers() {
        val now = LocalDateTime.now()
        val workerId = mutableListOf<UUID>()
        for (worker in workers) {
            if(worker.timestamp.isBefore(now.minusMinutes(config.WORKER_RESPONSE_TIME.toLong()))){
                workerId.add(worker.uuid)
            }
        }
        subPopulations.forEach{
            if(workerId.contains(it.worker?.uuid)){
                it.worker = null
            }
        }
        workers.removeAll{workerId.contains(it.uuid)}
    }
}