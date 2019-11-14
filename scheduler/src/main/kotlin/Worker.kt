import genetic_algorithm.IndividualPath
import java.time.LocalDateTime
import java.util.*

data class Worker(
    val uuid: UUID,
    val ipAddress: String,
    var individual: IndividualPath,
    var timestamp: LocalDateTime
)