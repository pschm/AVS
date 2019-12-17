package utility

/**
 * @return the first found element matching the [condition] or null if none was found
 */
fun <T> MutableList<T>.get(condition: (T) -> Boolean): T? {
    this.forEach {
        if (condition(it)) return it
    }

    return null
}