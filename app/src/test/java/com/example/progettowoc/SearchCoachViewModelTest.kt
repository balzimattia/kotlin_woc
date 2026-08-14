package com.example.progettowoc

import com.example.progettowoc.users.data.ClienteUserRepositoryInterface
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import com.example.progettowoc.users.viewmodels.SearchCoachViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class SearchCoachViewModelTest {

    @Mock
    private lateinit var userRepository: ClienteUserRepositoryInterface

    private lateinit var searchCoachViewModel: SearchCoachViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val fakeCoachesList = listOf(
        User(id = "1", email = "provacoach1@example.it", name = "prova coach primo", role = UserRole.COACH),
        User(id = "2", email = "provacoach2@example.it", name = "prova coach secondo", role = UserRole.COACH),
        User(id = "3", email = "provacoach3@example.it", name = "prova coach terzo", role = UserRole.COACH)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchCoachViewModel = SearchCoachViewModel(userRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN search = prova THEN return coachesList`() = runTest {
        whenever(userRepository.searchCoachesList("prova")).thenReturn(fakeCoachesList)

        searchCoachViewModel.onSearchChange("prova")
        searchCoachViewModel.searchCoaches()

        advanceUntilIdle()

        val currentState = searchCoachViewModel.searchCoachUiState.value

        assertFalse(currentState.isLoading)
        assertEquals(fakeCoachesList, currentState.coachesList)
        assertNull(currentState.errorMessage)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN no coaches found THEN coachesList is empty`() = runTest {
        whenever(userRepository.searchCoachesList("nessuno")).thenReturn(emptyList())

        searchCoachViewModel.onSearchChange("nessuno")
        searchCoachViewModel.searchCoaches()

        advanceUntilIdle()

        val currentState = searchCoachViewModel.searchCoachUiState.value

        assertFalse(currentState.isLoading)
        assertEquals(emptyList<User>(), currentState.coachesList)
        assertNull(currentState.errorMessage)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN repository throws THEN errorMessage is set`() = runTest {
        whenever(userRepository.searchCoachesList("prova"))
            .thenThrow(RuntimeException("Errore di rete"))

        searchCoachViewModel.onSearchChange("prova")
        searchCoachViewModel.searchCoaches()

        advanceUntilIdle()

        val currentState = searchCoachViewModel.searchCoachUiState.value

        assertFalse(currentState.isLoading)
        assertEquals(emptyList<User>(), currentState.coachesList)
        assertNotNull(currentState.errorMessage)
    }
}