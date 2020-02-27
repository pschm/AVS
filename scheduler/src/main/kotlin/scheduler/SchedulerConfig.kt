package scheduler

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import utility.readFile
import java.io.File


@Serializable
data class SchedulerConfig(
    val WORKER_COUNT: Int = 12,
    val POPULATION_SIZE: Int = 1200,
    val WORKER_RESPONSE_TIME: Int = 2,
    val DEMO_INDIVIDUAL_SIZE: Int = 90,
    val MIN_DELTA: Int = 10,
    val INDIVIDUAL_EXCHANGE_COUNT: Int = 10
) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getInstance(): SchedulerConfig = try {
            val configFile = File("src/main/resources/config.yml")
            val yaml = configFile.readFile() ?: ""
            val parsedConfig = Yaml.default.parse(serializer(), yaml)
            logger.info { "Scheduler configuration: $parsedConfig" }
            parsedConfig
        } catch (e: Exception) {
            logger.warn { "Could not read config file. Using default values." }
            SchedulerConfig()
        }

    }
}

