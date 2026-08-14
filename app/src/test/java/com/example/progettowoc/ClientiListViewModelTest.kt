package com.example.progettowoc

import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRelationRepositoryInterface
import com.example.progettowoc.users.data.CoachUserRepositoryInterface
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import com.example.progettowoc.coaching.viewmodels.ClientiListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever


@ExtendWith(MockitoExtension::class)
class ClientiListViewModelTest {

    @Mock
    private lateinit var authRepository: AuthRepositoryInterface

    @Mock
    private lateinit var userRepository: CoachUserRepositoryInterface

    @Mock
    private lateinit var coachingRepository: CoachingRelationRepositoryInterface

    private lateinit var clientiListViewModel: ClientiListViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val fakeClientiList = listOf(
        User(id = "1", email = "provacliente1@example.it", name = "prova cliente primo", role = UserRole.CLIENTE),
        User(id = "2", email = "provacliente2@example.it", name = "prova cliente secondo", role = UserRole.CLIENTE),
        User(id = "3", email = "provacliente3@example.it", name = "prova cliente terzo", role = UserRole.CLIENTE)
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clientiListViewModel = ClientiListViewModel(
            authRepository = authRepository,
            userRepository = userRepository,
            coachingRepository = coachingRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN clientiList not empty THEN return clientiList`() = runTest {
        val coachId = "prova"
        val fakeCurrentUser = User(id = coachId, email = "provacoach1@example.it", name = "prova coach primo", role = UserRole.COACH)
        val fakeCurrentUserStateFlow = MutableStateFlow<User?>(fakeCurrentUser)

        whenever(authRepository.currentUser).thenReturn(fakeCurrentUserStateFlow)
        whenever(userRepository.getClientiList(coachId = coachId)).thenReturn(fakeClientiList)

        val emittedErrors = mutableListOf<String>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            clientiListViewModel.errorMessage.collect { emittedErrors.add(it) }
        }

        clientiListViewModel.getClientiList()

        advanceUntilIdle()

        val clientiList = clientiListViewModel.clientiList.value
        val isLoading = clientiListViewModel.isLoadingClientiList.value

        assertEquals(fakeClientiList, clientiList)
        assertFalse(isLoading)
        assertTrue(emittedErrors.isEmpty())
        collectJob.cancel()
    }
}