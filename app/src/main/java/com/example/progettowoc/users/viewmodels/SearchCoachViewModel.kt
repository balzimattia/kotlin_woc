package com.example.progettowoc.users.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.users.uiStates.SearchCoachUiState
import com.example.progettowoc.users.data.ClienteUserRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchCoachViewModel @Inject constructor(
    private val userRepository: ClienteUserRepositoryInterface
) : ViewModel() {

    private val _searchCoachUiState = MutableStateFlow(SearchCoachUiState())
    val searchCoachUiState: StateFlow<SearchCoachUiState> = _searchCoachUiState.asStateFlow()


    fun searchCoaches() {
        viewModelScope.launch {
            try {
                _searchCoachUiState.update { it.copy(isLoading = true) }
                val search = searchCoachUiState.value.search
                val list = userRepository.searchCoachesList(search)
                _searchCoachUiState.update {
                    it.copy(
                        coachesList = list,
                        errorMessage = null
                    )
                }
            }
            catch (e: Exception) {
                _searchCoachUiState.update {
                    it.copy(
                        errorMessage = "Coach non trovato"
                    )
                }
            } finally {
                _searchCoachUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchChange(value: String) {
        _searchCoachUiState.update {
            it.copy(search = value)
        }
    }
}