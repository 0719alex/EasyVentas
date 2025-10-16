package com.aazm.easyadmin.data.net

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.*

data class FmMessage(val code: String?, val message: String?)
data class FmLoginInner(val token: String?)
data class FmLoginResponse(val response: FmLoginInner?, val messages: List<FmMessage>?)

data class FmDataInfo(
    val database: String?, val layout: String?, val table: String?,
    val totalRecordCount: Int?, val foundCount: Int?, val returnedCount: Int?
)
data class FmRecord(val fieldData: Map<String, Any?>, val recordId: String?, val modId: String?)
data class FmListInner(val data: List<FmRecord>?, val dataInfo: FmDataInfo?)
data class FmListResponse(val response: FmListInner?, val messages: List<FmMessage>?)

/**
 * Helper para body vacío "{}"
 */
private val emptyJsonBody: RequestBody =
    "{}".toRequestBody("application/json".toMediaType())

interface FmApi {
    @Headers("Content-Type: application/json")
    @POST("databases/{db}/sessions")
    suspend fun login(
        @Path("db") db: String,
        @Header("Authorization") basicAuth: String,
        @Body body: RequestBody = emptyJsonBody
    ): FmLoginResponse

    @GET("databases/{db}/layouts/{layout}/records")
    suspend fun listRecords(
        @Path("db") db: String,
        @Path("layout") layout: String,
        @Query("_limit") limit: Int,
        @Query("_offset") offset: Int
    ): FmListResponse

    @DELETE("databases/{db}/sessions/{token}")
    suspend fun logout(@Path("db") db: String, @Path("token") token: String): FmLoginResponse
}
