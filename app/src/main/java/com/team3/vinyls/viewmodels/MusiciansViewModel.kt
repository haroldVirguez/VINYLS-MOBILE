package com.team3.vinyls.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team3.vinyls.data.repositories.MusicianRepository
import com.team3.vinyls.ui.models.MusicianUiModel
import com.team3.vinyls.ui.mapper.toUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusiciansViewModel(
    private val repository: MusicianRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _musicians = MutableLiveData<List<MusicianUiModel>>()
    val musicians: LiveData<List<MusicianUiModel>> = _musicians

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadMusicians()
    }

    fun refresh() {
        loadMusicians()
    }

    private fun loadMusicians() {
        _loading.value = true
        _error.value = null
        viewModelScope.launch(dispatcher) {
            try {
                val data = repository.fetchMusicians()
                // Log calls can throw in plain JVM unit tests (android.util.Log not mocked).
                // Envolver en try/catch para evitar que el logging rompa la coroutine en tests.
                try {
                    Log.d("MusiciansVM", "fetched ${'$'}{data.size} musicians: ${'$'}{data.take(5)}")
                } catch (_: Throwable) {
                }
                val uiList = data.map { it.toUi() }.sortedBy { it.name }
                try {
                    Log.d("MusiciansVM", "mapped to ui ${'$'}{uiList.size} items: ${'$'}{uiList.take(5)}")
                } catch (_: Throwable) {
                }
                _musicians.value = uiList
            } catch (t: Throwable) {
                try {
                    Log.e("MusiciansVM", "error loading musicians", t)
                } catch (_: Throwable) {
                }
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }
}
