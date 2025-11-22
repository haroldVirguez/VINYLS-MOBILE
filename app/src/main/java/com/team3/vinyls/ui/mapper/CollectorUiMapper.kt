package com.team3.vinyls.ui.mapper

import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.ui.models.CollectorUiModel

fun CollectorDto.toUi(): CollectorUiModel {
    // Usar email o teléfono como subtítulo si no hay otra info
    val subtitle = this.email?.takeIf { it.isNotBlank() }
        ?: this.telephone?.takeIf { it.isNotBlank() }
        ?: "Ubicación desconocida"

    // No hay imagen en CollectorDto por defecto; mantener null
    val imageUrl = null

    return CollectorUiModel(
        id = id,
        name = name,
        subtitle = subtitle,
        image = imageUrl
    )
}

