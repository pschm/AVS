package genetic_algorithm

/**
 * needed for json parsing :(
 */
data class Items(val Items: List<Product>)

data class Position(
    var x: Double = (Math.random() * 200),
    var y: Double = (Math.random() * 200)
)

class Product(
    var position: Position = Position(),
    var name: String? = null
) {

    /**
     * Gets the distance to given product
     * @param product
     * @return Distance
     */
    fun distanceTo(product: Product): Double {
        val xDistance = Math.abs(position.x - product.position.x)
        val yDistance = Math.abs(position.y - product.position.y)
        return Math.sqrt(xDistance * xDistance + (yDistance * yDistance).toDouble())
    }

    override fun toString(): String {
        return "$position,$name"
    }
}