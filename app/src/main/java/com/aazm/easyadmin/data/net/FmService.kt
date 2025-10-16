package com.aazm.easyadmin.data.net

import android.util.Base64
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.roundToInt

class FmService(private val api: FmApi, private val store: AuthTokenStore) {

    suspend fun ensureLogin(): String {
        store.token?.let { if (it.isNotBlank()) return it }
        val b64 = Base64.encodeToString("${FmConfig.FM_USER}:${FmConfig.FM_PASS}".toByteArray(), Base64.NO_WRAP)
        try {
            val resp = api.login(FmConfig.FM_DB, "Basic $b64")
            val token = resp.response?.token ?: error("Login sin token: ${resp.messages}")
            store.token = token
            return token
        } catch (e: HttpException) {
            throw RuntimeException("Error de login (${e.code()}): revisa usuario/clave o permisos del FileMaker.", e)
        } catch (e: IOException) {
            throw RuntimeException("Error de red al iniciar sesión. Verifica conexión/SSL.", e)
        }
    }

    /**
     * Descarga paginada con progreso (0..100).
     * onProgress se invoca varias veces. Al final se asegura 100.
     */
    suspend fun fetchAllProducts(onProgress: (Int) -> Unit): List<Map<String, Any?>> {
        ensureLogin()
        val out = mutableListOf<Map<String, Any?>>()
        var offset = 1 // FileMaker es 1-based
        val limit = FmConfig.PAGE_SIZE
        var total = -1
        var acumulado = 0

        try {
            onProgress(0)
            while (true) {
                val page = api.listRecords(FmConfig.FM_DB, FmConfig.FM_LAYOUT, limit, offset)
                val items = page.response?.data.orEmpty()
                val info = page.response?.dataInfo
                if (total < 0) total = info?.foundCount ?: info?.totalRecordCount ?: -1

                out += items.map { it.fieldData }

                val returned = info?.returnedCount ?: items.size
                acumulado += returned
                offset += returned

                if (total > 0) {
                    val pct = ((acumulado.toDouble() / total.toDouble()) * 100.0).coerceIn(0.0, 100.0)
                    onProgress(pct.roundToInt())
                }

                if (returned < limit || items.isEmpty()) break
            }
            onProgress(100)
            return out
        } catch (e: HttpException) {
            throw RuntimeException("Error al leer productos (${e.code()}). Revisa el layout '${FmConfig.FM_LAYOUT}' y privilegios.", e)
        } catch (e: IOException) {
            throw RuntimeException("Error de red al descargar productos. Verifica conexión/SSL.", e)
        }
    }
}
