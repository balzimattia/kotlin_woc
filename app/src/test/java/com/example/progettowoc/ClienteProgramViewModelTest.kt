package com.example.progettowoc

import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.programs.data.ClienteProgramsRepositoryInterface
import com.example.progettowoc.programs.data.Day
import com.example.progettowoc.programs.data.Exercise
import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.programs.data.Week
import com.example.progettowoc.programs.viewmodels.ClienteProgramViewModel
import com.example.progettowoc.users.data.User
import com.example.progettowoc.users.data.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever


@ExtendWith(MockitoExtension::class)
class ClienteProgramViewModelTest {

    @Mock
    private lateinit var programsRepository: ClienteProgramsRepositoryInterface

    @Mock
    private lateinit var authRepository: AuthRepositoryInterface

    private lateinit var clienteProgramViewModel: ClienteProgramViewModel

    private val testDispatcher = StandardTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clienteProgramViewModel = ClienteProgramViewModel(programsRepository = programsRepository, authRepository = authRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `WHEN latestProgramNum != 0 THEN return programUiState currentProgram not null`() = runTest {
        val clienteId = "prova"
        val fakeCurrentUser = User(id = clienteId, email = "provacliente1@example.it", name = "prova cliente primo", role = UserRole.CLIENTE)
        val fakeCurrentUserStateFlow = MutableStateFlow<User?>(fakeCurrentUser)

        val fakeLatestProgramNumber = 1
        val fakeProgram = ProgramSheet(
            number = fakeLatestProgramNumber, weeks = listOf(
                Week(number = 1, days = listOf(
                        Day(number = 1, isCompleted = false, exercises = listOf(
                                Exercise(
                                    name = "esempio",
                                    sets = 3,
                                    reps = 10,
                                    rest = 120,
                                    weight = 100f,
                                    coachComment = "prova",
                                    clienteComment = "prova"
                                )
                            )
                        )
                    )
                )
            )
        )

        whenever(authRepository.currentUser).thenReturn(fakeCurrentUserStateFlow)
        whenever(programsRepository.getLatestProgramNumber(clienteId = clienteId)).thenReturn(fakeLatestProgramNumber)
        whenever(programsRepository.getProgram(clienteId = clienteId, programNumber = fakeLatestProgramNumber)).thenReturn(fakeProgram)

        clienteProgramViewModel.loadInitialData()

        advanceUntilIdle()

        val currentProgram = clienteProgramViewModel.currentProgramUiState.value.currentProgram
        val isLoading = clienteProgramViewModel.currentProgramUiState.value.isLoadingScreen
        val latestProgNum = clienteProgramViewModel.latestProgramNumState.value

        assertEquals(fakeProgram, currentProgram)
        assertFalse(isLoading)
        assertEquals(fakeLatestProgramNumber, latestProgNum)
    }
}