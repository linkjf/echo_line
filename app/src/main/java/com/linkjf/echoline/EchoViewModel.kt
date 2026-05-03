package com.linkjf.echoline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EchoViewModel(
    private val repository: EchoRepository = FakeEchoRepository(),
    private val localTextValidator: LocalTextValidator = LocalTextValidator()
) : ViewModel() {
    private val _uiState = MutableStateFlow(EchoUiState())
    val uiState: StateFlow<EchoUiState> = _uiState.asStateFlow()

    fun onInputChanged(text: String) {
        _uiState.update { state ->
            state.copy(
                inputText = text,
                inputError = localTextValidator.validate(text),
                errorMessage = null
            )
        }
    }

    fun onSubmit() {
        if (uiState.value.isSubmitting) {
            return
        }

        val submittedText = uiState.value.inputText.trim()
        val inputError = localTextValidator.validate(submittedText)

        if (inputError != null) {
            _uiState.update { state ->
                state.copy(
                    inputText = submittedText,
                    inputError = inputError,
                    submittedText = null,
                    errorMessage = null,
                    isSubmitting = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    inputText = submittedText,
                    inputError = null,
                    errorMessage = null,
                    isSubmitting = true
                )
            }

            val isValid = repository.validate(submittedText)

            _uiState.update { state ->
                if (isValid) {
                    state.copy(
                        submittedText = submittedText,
                        errorMessage = null,
                        isSubmitting = false
                    )
                } else {
                    state.copy(
                        submittedText = null,
                        errorMessage = EchoError.ServerValidationFailed,
                        isSubmitting = false
                    )
                }
            }
        }
    }
}
