package com.team3.vinyls.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.models.CollectorDto
import com.team3.vinyls.data.repositories.CollectorRepository
import kotlinx.coroutines.launch

class CollectorsDetailViewModel(private val collectorsRepository: CollectorRepository): ViewModel() {

    private val _collector = MutableLiveData<CollectorDto>()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadCollectorDetail(collectorId: Int) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val data = collectorsRepository.fetchCollectorDetail(collectorId)
                _collector.value = data
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }
}