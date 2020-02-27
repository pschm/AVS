package utility

import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException

/**
 * Read all lines from given file and return concatenated string or null if nothing could be read
 */
fun File.readFile(): String? {
    val lines = try {
        this.readLines()
    } catch (e: FileNotFoundException) {
        KotlinLogging.logger{}.error { "Couldn't find: ${this.path}" }
        return null
    }

    // concat all lines - maybe delete -> not really needed, because the generated json files only contain one line.
    val builder = StringBuilder()
    lines.forEach { builder.append(it + "\n") }

    return builder.toString()
}
