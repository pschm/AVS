import genetic_algorithm.Individual
import java.time.LocalDateTime

data class Worker(val ipAddress: String, var individual: Individual, var timestamp: LocalDateTime)