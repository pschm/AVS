package json_structure

import com.google.gson.annotations.SerializedName
import genetic_algorithm.Position
import genetic_algorithm.Product

data class UnityMapStructure(
    @SerializedName("Items")
    val products: List<Product>,
    @SerializedName("NavMesh")
    val navMesh: List<MeshNode>
)

data class PathResponse(
    @SerializedName("Items")
    val path: List<Product>?,
    @SerializedName("DemoItems")
    val demoPath: List<Product>,
    @SerializedName("Distance")
    val distance: Int
)

data class MeshNode(val id: Int, val position: Position, val nextNodes: List<Int>)