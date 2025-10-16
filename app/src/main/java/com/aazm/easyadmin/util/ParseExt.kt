package com.aazm.easyadmin.util

fun Any?.toSafeString(): String = when (this) {
    null -> ""
    is String -> this
    else -> this.toString()
}

fun Any?.toSafeDouble(): Double = when (this) {
    null -> 0.0
    is Number -> this.toDouble()
    is String -> this.toDoubleOrNull() ?: 0.0
    else -> 0.0
}
