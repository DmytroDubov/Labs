package com.org.labss.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

const val DEFAULT_BIN_ID = "69b03ad66a0858658be23052"
const val SEARCH_HISTORY_BIN_ID = "69b0605984682b3562864069"

interface ApiService {


    @Headers(
        "X-Bin-Meta: false",
        "X-Master-Key: \$2a\$10\$UOCV8g/MYvU.mLtD8RF/puQ12LaTxvXtdL/nlvLCKOTd1QPdscRaO"
    )
    @GET("b/$DEFAULT_BIN_ID")
    suspend fun getProducts(): ProductResponse

    @Headers(
        "X-Bin-Meta: false",
        "X-Master-Key: \$2a\$10\$UOCV8g/MYvU.mLtD8RF/puQ12LaTxvXtdL/nlvLCKOTd1QPdscRaO"
    )
    @GET("b/{binId}")
    suspend fun getProductsByBinId(
        @Path("binId") binId: String
    ): ProductResponse


    @Headers(
        "X-Bin-Meta: false",
        "X-Master-Key: \$2a\$10\$UOCV8g/MYvU.mLtD8RF/puQ12LaTxvXtdL/nlvLCKOTd1QPdscRaO"
    )
    @GET("b/$SEARCH_HISTORY_BIN_ID")
    suspend fun getSearchHistory(): SearchHistoryResponse

    @Headers(
        "Content-Type: application/json",
        "X-Master-Key: \$2a\$10\$UOCV8g/MYvU.mLtD8RF/puQ12LaTxvXtdL/nlvLCKOTd1QPdscRaO"
    )
    @PUT("b/$SEARCH_HISTORY_BIN_ID")
    suspend fun updateSearchHistory(@Body body: SearchHistoryResponse): SearchHistoryResponse
}