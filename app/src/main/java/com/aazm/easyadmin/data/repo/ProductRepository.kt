package com.aazm.easyadmin.data.repo

import com.aazm.easyadmin.data.db.ProductDao
import com.aazm.easyadmin.data.db.ProductEntity
import com.aazm.easyadmin.data.net.FmService
import com.aazm.easyadmin.util.toSafeDouble
import com.aazm.easyadmin.util.toSafeString
import com.aazm.easyadmin.util.toCsv

class ProductRepository(private val fm: FmService, private val dao: ProductDao) {
    fun observeProducts() = dao.observeAll()
    fun observeById(cc: String) = dao.observeById(cc)

    suspend fun syncFromRemote(onProgress: (Int) -> Unit) {
        val maps = fm.fetchAllProducts(onProgress)

        val entities = maps.map { m ->
            val barcodes = extractBarcodes(m) // 👈 TODAS las repeticiones
            val mainBarcode = barcodes.firstOrNull().orEmpty()

            ProductEntity(
                CCProducto  = m["CCProducto"].toSafeString(),
                CodigoBarra = mainBarcode,
                CodigosBarra = barcodes.toCsv(),
                Articulo    = m["Articulo"].toSafeString(),
                Existencia  = m["Existencia"].toSafeDouble(),
                Precio1     = m["Precio1"].toSafeDouble(),
                Precio2     = m["Precio2"].toSafeDouble(),
                Precio3     = m["Precio3"].toSafeDouble(),
                Precio4     = m["Precio4"].toSafeDouble(),
                Categoria   = m["Categoria"].toSafeString()
            )
        }.filter { it.CCProducto.isNotBlank() }

        dao.clearAll()
        if (entities.isNotEmpty()) dao.upsertAll(entities)
    }

    suspend fun findByBarcode(barcode: String) = dao.getByBarcode(barcode)

    /**
     * Soporta formatos de FileMaker para campos repetidos:
     * - Claves "CodigoBarra(1)", "CodigoBarra(2)", ...
     * - A veces vienen como lista bajo la clave "CodigoBarra" (List<*>)
     * - Fallback: un único string "CodigoBarra"
     */
    private fun extractBarcodes(m: Map<String, Any?>): List<String> {
        val out = mutableListOf<String>()

        // 1) Lista en "CodigoBarra" (si FileMaker envía arreglo para repetidos)
        val raw = m["CodigoBarra"]
        when (raw) {
            is List<*> -> raw.forEach { it?.toString()?.takeIf { s -> s.isNotBlank() }?.let(out::add) }
            is String -> if (raw.isNotBlank()) out.add(raw)
        }

        // 2) Repeticiones explicitadas como CodigoBarra(n)
        val regex = Regex("""^CodigoBarra\((\d+)\)$""")
        val pairs = m.entries.mapNotNull { (k, v) ->
            val match = regex.matchEntire(k) ?: return@mapNotNull null
            val idx = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
            val str = v?.toString()?.trim().orEmpty()
            if (str.isNotBlank()) idx to str else null
        }.sortedBy { it.first }

        if (pairs.isNotEmpty()) {
            pairs.forEach { (_, s) -> if (!out.contains(s)) out.add(s) }
        }

        // Limpieza de duplicados y vacíos
        return out.distinct().filter { it.isNotBlank() }
    }
}
