package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.services.CollectorsService
import com.team3.vinyls.data.models.CollectorDto

open class CollectorRepository(private val service: CollectorsService) {
    open suspend fun fetchCollectors(): List<CollectorDto> {
        return service.getCollectors()
    }

    suspend fun fetchCollectorDetail(id: Int): CollectorDto {
        return service.getCollectorDetail(id)
    }
}

