package com.linkjf.echoline

data class EchoUiState(
    val inputText: String = "",
    val inputError: EchoError? = null,
    val submittedText: String? = null,
    val errorMessage: EchoError? = null,
    val isSubmitting: Boolean = false
) {
    val canSubmit: Boolean
        get() = inputText.trim().isNotEmpty() && inputError == null && !isSubmitting
}
