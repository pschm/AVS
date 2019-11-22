package genetic_algorithm

import genetic_algorithm.PathManager.Companion.numberOfProducts
import java.util.*
import kotlin.collections.ArrayList

class IndividualPath(var IndividualPath: ArrayList<Product?> = ArrayList()) {

    // Holds our IndividualPath of products
//    var IndividualPath = ArrayList<Product?>()
    // Cache
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
        if (IndividualPath.isEmpty()) {
            for (i in 0 until numberOfProducts()) {
                IndividualPath.add(null)
            }
        }
    }

    /**
     * Creates a random individual
     */
    fun generateIndividual() { // Loop through all our destination products and add them to our IndividualPath
        for (productIndex in 0 until numberOfProducts()) {
            setProduct(productIndex, PathManager.getProduct(productIndex))
        }
        // Randomly reorder the IndividualPath
        IndividualPath.shuffle()
    }

    /**
     * Gets a product from the IndividualPath
     * @param tourPosition
     * @return Product
     */
    fun getProduct(tourPosition: Int): Product? {
        return IndividualPath[tourPosition]
    }

    /**
     * Sets a product in a certain position within a IndividualPath
     * @param IndividualPathPosition
     * @param product
     */
    fun setProduct(IndividualPathPosition: Int, product: Product?) {
        IndividualPath[IndividualPathPosition] = product
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0.0
        distance = 0
    }

    /**
     * Gets the total distance of the IndividualPath
     * @return Distance (INT)
     */
    fun calcDistance(oldDistance: Int): Int {
        var newDistance = oldDistance
        if (oldDistance == 0) {
            var IndividualPathDistance = 0
            // Loop through our IndividualPath's products
            for (productIndex in 0 until pathSize()) { // Get product we're travelling from
                val fromProduct = getProduct(productIndex)
                // Product we're travelling to
                var destinationProduct: Product?
                // Check we're not on our IndividualPath's last product, if we are set our
// IndividualPath's final destination product to our starting product
                destinationProduct = if (productIndex + 1 < pathSize()) {
                    getProduct(productIndex + 1)
                } else {
                    getProduct(0)
                }
                // Get the distance between the two products
                IndividualPathDistance += fromProduct!!.distanceTo(destinationProduct!!).toInt()
            }
            newDistance = IndividualPathDistance
        }
        return newDistance
    }

    /**
     * Get number of products on our IndividualPath
     * @return
     */
    fun pathSize(): Int {
        return IndividualPath.size
    }

    /**
     * Check if the IndividualPath contains a product
     * @param product
     * @return Boolean
     */
    fun containsProduct(product: Product?): Boolean {
        return IndividualPath.contains(product)
    }

    override fun toString(): String {
        var geneString = "|"
        for (i in 0 until pathSize()) {
            geneString += getProduct(i).toString() + "|"
        }
        return geneString
    }
}