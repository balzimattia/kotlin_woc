package com.example.progettowoc.coaching.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRelationRepositoryInterface
import com.example.progettowoc.users.data.CoachUserRepositoryInterface
import com.example.progettowoc.users.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientiListViewModel @Inject constructor(
    private val authRepository: AuthRepositoryInterface,
    private val userRepository: CoachUserRepositoryInterface,
    private val coachingRepository: CoachingRelationRepositoryInterface
) : ViewModel() {
    //stato
    private val _clientiList = MutableStateFlow<List<User>>(emptyList())
    val clientiList: StateFlow<List<User>> = _clientiList.asStateFlow()

    private val _isLoadingClientiList = MutableStateFlow(false)
    val isLoadingClientiList: StateFlow<Boolean> = _isLoadingClientiList

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()


    init {
        getClientiList()
    }

    fun getClientiList() {
        viewModelScope.launch {
            try {
                _isLoadingClientiList.value = true

                val id = authRepository.currentUser.value?.id ?: return@launch
                _clientiList.value = userRepository.getClientiList(coachId = id)
            } catch (e: Exception) {
            } finally {
                _isLoadingClientiList.value = false
            }
        }
    }

    fun removeRelationWithCliente(cliente: User) {
        viewModelScope.launch {
            try {
                //non uso getClientiList perche dovrei usare un altro caricamento
                _isLoadingClientiList.value = true
                coachingRepository.removeCoachingRelation(clienteId = cliente.id)

                val id = authRepository.currentUser.value?.id ?: return@launch
                _clientiList.value = userRepository.getClientiList(coachId = id)
            } catch (e: Exception) {
                if (_clientiList.value.isEmpty()) _errorMessage.emit("Impossibile caricare la lista")
                else _errorMessage.emit("Qualcosa è andato storto")
            } finally {
                _isLoadingClientiList.value = false
            }
        }
    }
}