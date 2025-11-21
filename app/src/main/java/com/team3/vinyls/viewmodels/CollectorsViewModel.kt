package com.team3.vinyls.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.repositories.CollectorRepository
import com.team3.vinyls.ui.models.CollectorUiModel
import com.team3.vinyls.ui.mapper.toUi
import kotlinx.coroutines.launch

class CollectorsViewModel(
    private val repository: CollectorRepository
) : ViewModel() {

    private val _collectors = MutableLiveData<List<CollectorUiModel>>()
    val collectors: LiveData<List<CollectorUiModel>> = _collectors

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadCollectors()
    }

    fun refresh() {
        loadCollectors()
    }

    private fun loadCollectors() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val data = repository.fetchCollectors()
                try {
                    Log.d("CollectorsVM", "fetched ${'$'}{data.size} collectors: ${'$'}{data.take(5)}")
                } catch (_: Throwable) {
                }
                val uiList = data.map { it.toUi() }.sortedBy { it.name }
                try {
                    Log.d("CollectorsVM", "mapped to ui ${'$'}{uiList.size} items: ${'$'}{uiList.take(5)}")
                } catch (_: Throwable) {
                }
                _collectors.value = uiList
            } catch (t: Throwable) {
                try {
                    Log.e("CollectorsVM", "error loading collectors", t)
                } catch (_: Throwable) {
                }
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }
}

