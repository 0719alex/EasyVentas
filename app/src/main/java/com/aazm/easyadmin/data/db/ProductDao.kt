package com.aazm.easyadmin.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY Articulo ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE CCProducto = :cc LIMIT 1")
    fun observeById(cc: String): Flow<ProductEntity?>

    // Buscar por cualquier repetición:
    // - match exacto en CodigoBarra
    // - o contiene en CodigosBarra (CSV). Ojo con falsos positivos si un código es subcadena de otro.
    @Query("""
        SELECT * FROM products 
        WHERE CodigoBarra = :barcode 
           OR (',' || CodigosBarra || ',') LIKE ('%,' || :barcode || ',%')
        LIMIT 1
    """)
    suspend fun getByBarcode(barcode: String): ProductEntity?

    @Upsert
    suspend fun upsertAll(items: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()
}
