package genetic_algorithm

data class Position(
    var x: Double = (Math.random() * 200),
    var y: Double = (Math.random() * 200)
)

class Product(
    private val id: String,
    private val position: Position = Position(),
    private val name: String? = null
) {

    override fun toString(): String {
        return "$name"
    }
}