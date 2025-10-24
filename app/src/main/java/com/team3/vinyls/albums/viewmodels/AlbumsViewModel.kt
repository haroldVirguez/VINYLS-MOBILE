package com.team3.vinyls.albums.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.albums.data.AlbumRepository
import com.team3.vinyls.core.network.AlbumsService
import com.team3.vinyls.albums.ui.AlbumUiModel
import com.team3.vinyls.core.network.NetworkModule
import kotlinx.coroutines.launch

class AlbumsViewModel(
    private val repositoryFactory: () -> AlbumRepository = {
        val retrofit = NetworkModule.retrofit
        val service = retrofit.create(AlbumsService::class.java)
        AlbumRepository(service)
    }
) : ViewModel() {
    private val _albums = MutableLiveData<List<AlbumUiModel>>()
    val albums: LiveData<List<AlbumUiModel>> = _albums
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        fetch()
    }

    private fun fetch() {
        val repository = repositoryFactory()
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