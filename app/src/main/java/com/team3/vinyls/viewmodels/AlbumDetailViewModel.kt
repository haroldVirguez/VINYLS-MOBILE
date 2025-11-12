package com.team3.vinyls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.data.repositories.TrackRepository
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.models.TrackDto
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _album = MutableLiveData<AlbumDto>()
    val album: LiveData<AlbumDto> = _album

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadAlbumDetail(albumId: Int) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val data = albumRepository.getAlbumDetail(albumId)
                _album.value = data
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTrackToAlbum(albumId: Int, trackName: String, trackDuration: String) {
        val track = TrackDto(id = 0, name = trackName, duration = trackDuration)
        viewModelScope.launch {
            try {
                val newTrack = trackRepository.addTrackToAlbum(albumId, track)
                println("Track agregado: ${newTrack.name}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
