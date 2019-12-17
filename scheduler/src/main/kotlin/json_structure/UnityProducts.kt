package json_structure

import genetic_algorithm.Position
import genetic_algorithm.Product

data class UnityProducts(val Items: List<Product>, val NavMesh: List<MeshNode>)

data class PathResponse(val Items: List<Product>?, val DemoItems: List<Product>, val Distance: Int)

data class MeshNode(val id: Int, val position: Position, val nextNodes: List<Int>)