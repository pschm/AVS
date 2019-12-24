package genetic_algorithm

import kotlin.math.abs
import kotlin.math.sqrt

data class Position(
    var x: Double = (Math.random() * 200),
    var y: Double = (Math.random() * 200)
)

class Product(
    private val position: Position = Position(),
    private val name: String? = null
) {

    /**
     * Gets the distance to given product
     * @param product
     * @return Distance
     */
    fun distanceTo(product: Product): Double {
        val xDistance = abs(position.x - product.position.x)
        val yDistance = abs(position.y - product.position.y)
        return sqrt(xDistance * xDistance + (yDistance * yDistance))
    }

    override fun toString(): String {
        return "$name"
    }
}