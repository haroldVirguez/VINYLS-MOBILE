package com.team3.vinyls.core.network

import com.team3.vinyls.BuildConfig

object ApiConstants {
    // Base URL comes from product flavor via BuildConfig
    val BASE_URL: String = BuildConfig.BASE_URL

    // Endpoints
    const val ALBUMS_ENDPOINT = "albums"
    const val MUSICIANS_ENDPOINT = "musicians"
}
