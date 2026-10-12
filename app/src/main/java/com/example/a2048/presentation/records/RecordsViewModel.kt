package com.example.a2048.presentation.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2048.domain.usecase.ClearRecordsUseCase
import com.example.a2048.domain.usecase.ObserveRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class RecordsViewModel @Inject constructor(
    observeRecords: ObserveRecordsUseCase,
    private val clearRecords: ClearRecordsUseCase,
) : ViewModel() {
    val records = observeRecords().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _clearing = MutableStateFlow(false)
    val clearing = _clearing.asStateFlow()

    fun onClearRecords() {
        if (_clearing.value) return
        _clearing.value = true
        viewModelScope.launch {
            try {
                clearRecords()
            } finally {
                _clearing.value = false
            }
        }
    }
}
