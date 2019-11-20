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

    @Transient var worker: Worker? = null

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

    fun getPaths(): ArrayList<IndividualPath?>? {
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
    fun savePath(index: Int, path: IndividualPath?) {
        paths[index] = path
    }

    /**
     * Gets a path from population
     * @param index
     * @return path
     */
    fun getPath(index: Int): IndividualPath? {
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
     * Gets population size
     * @return size of population
     */
    fun populationSize(): Int {
        return paths.size
    }
}