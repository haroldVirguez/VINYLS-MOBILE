package com.team3.vinyls.data

import com.team3.vinyls.data.models.MusicianDto

open class MusicianRepository(private val service: MusiciansService) {
    open suspend fun fetchMusicians(): List<MusicianDto> {
        return service.getMusicians()
    }
}

