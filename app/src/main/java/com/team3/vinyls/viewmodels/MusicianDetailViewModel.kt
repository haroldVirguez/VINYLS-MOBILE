package com.team3.vinyls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.models.MusicianDto
import com.team3.vinyls.data.repositories.MusicianRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusiciansDetailViewModel(
    private val repository: MusicianRepository
) : ViewModel() {

    private val _musician = MutableLiveData<MusicianDto>()
    val musician: LiveData<MusicianDto> get() = _musician

    fun loadMusicianDetail(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.fetchMusicianDetail(id)
                _musician.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}