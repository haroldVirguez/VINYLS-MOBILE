package com.team3.vinyls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.models.AlbumCreateDto
import com.team3.vinyls.data.models.AlbumDto
import com.team3.vinyls.data.repositories.AlbumRepository
import com.team3.vinyls.ui.models.AlbumUiModel
import kotlinx.coroutines.launch
import com.team3.vinyls.ui.mapper.toUi

class AlbumsViewModel(
    private val repository: AlbumRepository
) : ViewModel() {

    private val _albums = MutableLiveData<List<AlbumUiModel>>()
    val albums: LiveData<List<AlbumUiModel>> = _albums

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _createAlbumResult = MutableLiveData<Result<AlbumDto>?>()
    val createAlbumResult: LiveData<Result<AlbumDto>?> = _createAlbumResult

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
                _albums.value = data.map { it.toUi() }
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createAlbum(dto: AlbumCreateDto) {
        viewModelScope.launch {
            try {
                val createdAlbum = repository.createAlbum(dto)   // now returns AlbumDto
                _createAlbumResult.postValue(Result.success(createdAlbum))
            } catch (e: Exception) {
                _createAlbumResult.postValue(Result.failure(e))
            }
        }
    }

    fun clearCreateResult() {
        _createAlbumResult.value = null
    }
}