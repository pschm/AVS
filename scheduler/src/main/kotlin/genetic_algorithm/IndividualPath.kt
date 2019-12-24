package genetic_algorithm

import com.google.gson.annotations.SerializedName
import genetic_algorithm.PathManager.Companion.numberOfProducts

/**
 * @property individualPath Holds our IndividualPath of products
 */
class IndividualPath(
    @SerializedName("IndividualPath")
    var individualPath: ArrayList<Product?> = ArrayList()
) {

    var fitness = 0.0
        get() {
            if (field == 0.0) {
                field = 1 / distance.toDouble()
            }
            return field
        }
    var distance = 0
        get() = calcDistance(field)

    init {
        if (individualPath.isEmpty()) {
            for (i in 0 until numberOfProducts()) {
                individualPath.add(null)
            }
        }
    }

    fun getIndividualPathWithoutNulls(): List<Product> {
        val list = mutableListOf<Product>()
        individualPath.forEach { p ->
            p?.let { list.add(it) }
        }

        return list.toList()
    }

    /**
     * Creates a random individual
     */
    fun generateIndividual() { // Loop through all our destination products and add them to our IndividualPath
        for (productIndex in 0 until numberOfProducts()) {
            setProduct(productIndex, PathManager.getProduct(productIndex))
        }

        val start = individualPath.removeAt(0)
        val finish = individualPath.removeAt(individualPath.lastIndex)

        // Randomly reorder the IndividualPath
        individualPath.shuffle()

        individualPath.add(0, start)
        individualPath.add(finish)
    }

    /**
     * Gets a product from the IndividualPath
     * @param tourPosition
     * @return Product
     */
    private fun getProduct(tourPosition: Int): Product? {
        return individualPath[tourPosition]
    }

    /**
     * Sets a product in a certain position within a IndividualPath
     * @param individualPathPosition
     * @param product
     */
    private fun setProduct(individualPathPosition: Int, product: Product?) {
        individualPath[individualPathPosition] = product
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0.0
        distance = 0
    }

    /**
     * Gets the total distance of the IndividualPath
     * @return Distance (INT)
     */
    private fun calcDistance(oldDistance: Int): Int {
        var newDistance = oldDistance
        if (oldDistance == 0) {
            var individualPathDistance = 0
            // Loop through our IndividualPath's products
            for (productIndex in 0 until pathSize()) { // Get product we're travelling from
                val fromProduct = getProduct(productIndex)
                // Product we're travelling to
                // Check we're not on our IndividualPath's last product, if we are set our
                // IndividualPath's final destination product to our starting product
                val destinationProduct = if (productIndex + 1 < pathSize()) {
                    getProduct(productIndex + 1)
                } else {
                    getProduct(0)
                }
                // Get the distance between the two products
                individualPathDistance += fromProduct!!.distanceTo(destinationProduct!!).toInt()
            }
            newDistance = individualPathDistance
        }
        return newDistance
    }

    /**
     * Get number of products on our IndividualPath
     * @return
     */
    private fun pathSize(): Int {
        return individualPath.size
    }

    override fun toString(): String {
        var geneString = "|"
        for (i in 0 until pathSize()) {
            geneString += getProduct(i).toString() + "|"
        }
        return geneString
    }
}