package com.linkjf.echoline

sealed interface EchoError {
    data object EmptyInput : EchoError
    data class MinimumLength(val minLength: Int) : EchoError
    data object UnsupportedCharacters : EchoError
    data object ServerValidationFailed : EchoError
}
