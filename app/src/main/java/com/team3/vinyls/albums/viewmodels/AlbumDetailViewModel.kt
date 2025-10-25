package com.team3.vinyls.albums.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.albums.data.AlbumDto
import kotlinx.coroutines.launch

class AlbumDetailViewModel(
    private val repository: AlbumRepository
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
                val data = repository.getAlbumDetail(albumId)
                _album.value = data
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }
}