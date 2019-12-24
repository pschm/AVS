package json_structure

import genetic_algorithm.Population
import java.util.*

data class WorkerRespond(val uuid: UUID? = null, val population: Population?, val navMesh: List<MeshNode>?)