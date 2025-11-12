package com.team3.vinyls.data.repositories

import com.team3.vinyls.data.services.TrackService
import com.team3.vinyls.data.models.TrackDto

open class TrackRepository(private val service: TrackService) {

    suspend fun addTrackToAlbum(albumId: Int, track: TrackDto): TrackDto {
        return service.addTrackToAlbum(albumId, track)
    }
}
