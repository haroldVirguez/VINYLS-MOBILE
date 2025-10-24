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

class AlbumDetailViewModel(
    private val repositoryFactory: () -> AlbumRepository = {
        val retrofit = NetworkModule.retrofit
        val service = retrofit.create(AlbumsService::class.java)
        AlbumRepository(service)
    }
) : ViewModel() {

    private val _album = MutableLiveData<AlbumUiModel>()
    val album: LiveData<AlbumUiModel> get() = _album

    fun fetchAlbumDetail(albumId: String) {
        val repository = repositoryFactory()
        viewModelScope.launch {
            try {
                val albumDetail = repository.getAlbumDetail(albumId)
                _album.postValue(albumDetail)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}