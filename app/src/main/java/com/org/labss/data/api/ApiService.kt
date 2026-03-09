package com.org.labss.data.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

private const val DEFAULT_BIN_ID = "69ac8da543b1c97be9beaf9b"

interface ApiService {
    @Headers(
        "X-Bin-Meta: false",
        "X-Master-Key: \$2a\$10\$UOCV8g/MYvU.mLtD8RF/puQ12LaTxvXtdL/nlvLCKOTd1QPdscRaO"
    )
    @GET("b/{binId}")
    suspend fun getProducts(
        @Path("binId") binId: String = DEFAULT_BIN_ID
    ): ProductResponse
}