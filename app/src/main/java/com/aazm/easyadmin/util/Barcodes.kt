package com.aazm.easyadmin.util

fun List<String>.toCsv(): String =
    this.filter { it.isNotBlank() }.joinToString(",")

fun String.csvToList(): List<String> =
    if (this.isBlank()) emptyList()
    else this.split(",").map { it.trim() }.filter { it.isNotBlank() }
