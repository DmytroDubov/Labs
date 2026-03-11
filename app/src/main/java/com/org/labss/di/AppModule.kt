package com.org.labss.di

import android.content.Context
import androidx.room.Room
import com.org.labss.data.api.ApiService
import com.org.labss.data.local.CategoryDao
import com.org.labss.data.local.ProductDao
import com.org.labss.data.local.ECDatabase
import com.org.labss.data.local.SearchHistoryDao
import com.org.labss.data.repository.ProductRepositoryImpl
import com.org.labss.domain.repository.ProductRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    private const val DB_NAME = "sport_db"
    private const val BASE_URL = "https://api.jsonbin.io/v3/"

    @Volatile
    private var database: ECDatabase? = null

    @Volatile
    private var retrofit: Retrofit? = null

    fun provideDatabase(context: Context): ECDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                        context.applicationContext,
                        ECDatabase::class.java,
                        DB_NAME
                    ).fallbackToDestructiveMigration(false)
                .build()
                .also { database = it }
        }
    }

    fun provideProductDao(context: Context): ProductDao = provideDatabase(context).productDao()

    fun provideCategoryDao(context: Context): CategoryDao = provideDatabase(context).categoryDao()

    fun provideSearchHistoryDao(context: Context): SearchHistoryDao = provideDatabase(context).searchHistoryDao()

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun provideRetrofit(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { retrofit = it }
        }
    }

    fun provideApiService(): ApiService = provideRetrofit().create(ApiService::class.java)

    fun provideProductRepository(context: Context): ProductRepository {
        return ProductRepositoryImpl(
            apiService = provideApiService(),
            productDao = provideProductDao(context),
            categoryDao = provideCategoryDao(context),
            searchHistoryDao = provideSearchHistoryDao(context)
        )
    }
}