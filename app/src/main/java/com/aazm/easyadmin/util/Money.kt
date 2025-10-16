package com.aazm.easyadmin.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formatea un Double como Lempira (HNL) con el formato: L. 1,560.00
 * Usa es-HN para separadores correctos.
 */
fun Double.asLempira(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "HN"))
    // Aseguramos el símbolo "L." con espacio correcto si el dispositivo trae otro
    format.currency = java.util.Currency.getInstance("HNL")
    return format.format(this) // típico resultado: L. 1,560.00
}
