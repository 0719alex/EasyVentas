package com.aazm.easyadmin.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["Articulo"]),
        Index(value = ["CodigoBarra"], unique = false)
    ]
)
data class ProductEntity(
    @PrimaryKey val CCProducto: String,
    val CodigoBarra: String = "",   // principal (primera repetición)
    val CodigosBarra: String = "",  // TODAS las repeticiones, separadas por coma: "A,B,C"
    val Articulo: String = "",
    val Existencia: Double = 0.0,
    val Precio1: Double = 0.0,
    val Precio2: Double = 0.0,
    val Precio3: Double = 0.0,
    val Precio4: Double = 0.0,
    val Categoria: String = ""
)
