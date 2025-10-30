package com.team3.vinyls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.AlbumRepository
import com.team3.vinyls.ui.AlbumUiModel
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val repository: AlbumRepository
) : ViewModel() {

    private val _albums = MutableLiveData<List<AlbumUiModel>>()
    val albums: LiveData<List<AlbumUiModel>> = _albums

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadAlbums()
    }

    fun refresh() {
        loadAlbums()
    }

    private fun loadAlbums() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val data = repository.fetchAlbums()
                _albums.value = data
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }
}