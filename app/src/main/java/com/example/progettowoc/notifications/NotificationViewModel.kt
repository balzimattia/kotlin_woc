package com.example.progettowoc.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettowoc.notifications.data.Notification
import com.example.progettowoc.notifications.data.NotificationsRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepositoryInterface
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()


    //per quando arrivano le notifiche e sei nell'app
    companion object {
        val notificationReceived = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    }

    init {
        viewModelScope.launch {
            notificationReceived.collect {
                loadNotifications()
            }
        }
    }


    fun loadNotifications() {
        viewModelScope.launch {
            try {
                _notifications.value = notificationsRepository.getNotification()
            } catch (e: Exception) {
                clearNotifications()
            }
        }
    }


    fun deleteAllNotifications() {
        viewModelScope.launch {
            try {
                notificationsRepository.deleteAllNotification()
                loadNotifications()
            } catch (e: Exception) {
                clearNotifications()
            }
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}