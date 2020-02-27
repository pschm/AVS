package genetic_algorithm

import scheduler.Worker

/**
 * Construct a population
 * @param populationSize
 * @param initialize
 */
class Population(populationSize: Int, initialize: Boolean) {
    // Holds population of paths
    var paths: Array<IndividualPath?>

    // transient --> property is ignored from Gson while building json
    @Transient
    var worker: Worker? = null

    init {
        paths = arrayOfNulls(populationSize)
        // If we need to initialize a population of paths do so
        if (initialize) { // Loop and create individuals
            for (i in 0 until populationSize()) {
                val newPath = IndividualPath()
                newPath.generateIndividual()
                savePath(i, newPath)
            }
        }
    }

    fun getPaths(): ArrayList<IndividualPath?> {
        val arrayList = ArrayList<IndividualPath?>()
        paths.toCollection(arrayList)
        return arrayList
    }

    fun setPaths(paths: ArrayList<IndividualPath?>) {
        this.paths = paths.toTypedArray()
    }

    /**
     * Saves a path
     * @param index
     * @param path
     */
    private fun savePath(index: Int, path: IndividualPath?) {
        paths[index] = path
    }

    /**
     * Gets a path from population
     * @param index
     * @return path
     */
    private fun getPath(index: Int): IndividualPath? {
        return paths[index]
    }

    /**
     * Gets the best path in the population
     * @return fittest Path
     */
    fun getFittest(): IndividualPath? {
        var fittest = paths[0]
        // Loop through individuals to find fittest
        for (i in 1 until populationSize()) {
            if (fittest!!.fitness <= getPath(i)!!.fitness) {
                fittest = getPath(i)
            }
        }
        return fittest
    }

    /**
     * @return Individual with the shortest/best distance.
     */
    fun getBestIndividual(): IndividualPath? = paths.minBy { it?.distance ?: Double.MAX_VALUE }

    /**
     * Gets population size
     * @return size of population
     */
    fun populationSize(): Int {
        return paths.size
    }

    /**
     * update [paths] with the individuals from [population]
     */
    fun updateIndividuals(population: Population) {
        paths = population.paths
    }
}