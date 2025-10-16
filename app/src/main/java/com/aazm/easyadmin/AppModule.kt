package com.aazm.easyadmin

import android.content.Context
import androidx.room.Room
import com.aazm.easyadmin.data.db.AppDatabase
import com.aazm.easyadmin.data.net.FmApi
import com.aazm.easyadmin.data.net.FmService
import com.aazm.easyadmin.data.net.RetrofitProvider
import com.aazm.easyadmin.data.repo.ProductRepository

object AppModule {
    fun provideDb(ctx: Context) =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "easyadmin.db").build()

    fun provideFmService(): FmService {
        val retrofit = RetrofitProvider.fmRetrofit()
        val api: FmApi = retrofit.create(FmApi::class.java)
        return FmService(api, RetrofitProvider.tokenStore())
    }

    fun provideProductRepository(ctx: Context): ProductRepository {
        val db = provideDb(ctx)
        val fm = provideFmService()
        return ProductRepository(fm, db.productDao())
    }
}
