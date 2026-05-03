package com.linkjf.echoline

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun blankInputShowsRequiredErrorAndCannotSubmit() {
        val viewModel = EchoViewModel(repository = RecordingEchoRepository(result = true))

        viewModel.onInputChanged("   ")

        val state = viewModel.uiState.value
        assertEquals(EchoError.EmptyInput, state.inputError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun shortInputShowsMinimumLengthErrorAndCannotSubmit() {
        val viewModel = EchoViewModel(repository = RecordingEchoRepository(result = true))

        viewModel.onInputChanged("hi")

        val state = viewModel.uiState.value
        assertEquals(EchoError.MinimumLength(minLength = 3), state.inputError)
        assertFalse(state.canSubmit)
    }

    @Test
    fun validInputCanSubmit() {
        val viewModel = EchoViewModel(repository = RecordingEchoRepository(result = true))

        viewModel.onInputChanged("Hello EchoLine")

        val state = viewModel.uiState.value
        assertNull(state.inputError)
        assertTrue(state.canSubmit)
    }

    @Test
    fun submitWithLocalValidationErrorDoesNotCallRepository() = runTest {
        val repository = RecordingEchoRepository(result = true)
        val viewModel = EchoViewModel(repository = repository)

        viewModel.onInputChanged("no")
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(EchoError.MinimumLength(minLength = 3), state.inputError)
        assertNull(state.submittedText)
        assertFalse(repository.wasCalled)
    }

    @Test
    fun submitSuccessDisplaysSubmittedText() = runTest {
        val viewModel = EchoViewModel(repository = RecordingEchoRepository(result = true))

        viewModel.onInputChanged("Hello EchoLine")
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Hello EchoLine", state.submittedText)
        assertNull(state.errorMessage)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun submitWhileAlreadySubmittingDoesNotCallRepositoryAgain() = runTest {
        val repository = PendingEchoRepository()
        val viewModel = EchoViewModel(repository = repository)

        viewModel.onInputChanged("Hello EchoLine")
        viewModel.onSubmit()
        testDispatcher.scheduler.runCurrent()
        viewModel.onSubmit()

        assertEquals(1, repository.callCount)

        repository.complete(result = true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, repository.callCount)
    }

    @Test
    fun submitFailureShowsErrorAndClearsSubmittedText() = runTest {
        val viewModel = EchoViewModel(repository = RecordingEchoRepository(result = false))

        viewModel.onInputChanged("fail this")
        viewModel.onSubmit()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.submittedText)
        assertEquals(EchoError.ServerValidationFailed, state.errorMessage)
        assertFalse(state.isSubmitting)
    }

    private class RecordingEchoRepository(private val result: Boolean) : EchoRepository {
        var callCount = 0
        val wasCalled: Boolean
            get() = callCount > 0

        override suspend fun validate(text: String): Boolean {
            callCount += 1
            return result
        }
    }

    private class PendingEchoRepository : EchoRepository {
        private val result = CompletableDeferred<Boolean>()
        var callCount = 0
            private set

        override suspend fun validate(text: String): Boolean {
            callCount += 1
            return result.await()
        }

        fun complete(result: Boolean) {
            this.result.complete(result)
        }
    }
}
