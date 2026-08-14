package com.example.progettowoc.programs.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.programs.data.CoachProgramsRepositoryInterface
import com.example.progettowoc.programs.data.ProgramSheet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CoachProgramViewModel @Inject constructor(
    private val programsRepository: CoachProgramsRepositoryInterface
): ViewModel() {
    private val _clienteProgramsList = MutableStateFlow<List<ProgramSheet>>(emptyList())
    val clienteProgramsList: StateFlow<List<ProgramSheet>> = _clienteProgramsList.asStateFlow()

    private val _isLoadingInfo = MutableStateFlow(false)
    val isLoadingInfo: StateFlow<Boolean> = _isLoadingInfo.asStateFlow()


    private var initialized = false
    fun getClienteProgramsList(clienteId: String, refresh: Boolean = false) {
        if (initialized && !refresh) return
        initialized = true

        viewModelScope.launch {
            try {
                _isLoadingInfo.value = true

                val list = programsRepository.getProgramsList(clienteId = clienteId)
                _clienteProgramsList.value = list
            } catch (e: Exception) {
                _clienteProgramsList.value = emptyList()
            } finally {
                _isLoadingInfo.value = false
            }
        }
    }
}