package com.team3.vinyls.data.models

data class PerformerDto(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: String? = null
)