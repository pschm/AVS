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


fun <T> Array<T>.toBeautyString(): String {
    val stringBuilder = StringBuilder()

    for (element in this)
        stringBuilder.append(element)

    return stringBuilder.toString()
}