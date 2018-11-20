package com.zipsted.justtimer.util

fun String.parseDuration(): Pair<Int, Int> {
    if (this.indexOf(":") == -1) {
        throw StringIsNotDurationException()
    }
    val min: Int
    val sec: Int
    try {
        val minString = substring(0, indexOf(":"))
        val secString = substring(indexOf(":") + 1, length)

        min = Integer.parseInt(minString)
        sec = Integer.parseInt(secString)
    } catch (e: Exception) {
        throw StringIsNotDurationException()
    }

    return Pair(min, sec)
}

fun Pair<Int, Int>.duration(): String {
    return String.format("%d:%02d", first, second)
}