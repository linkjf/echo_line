package com.linkjf.echoline

class LocalTextValidator(private val minLength: Int = 3) {
    fun validate(text: String): EchoError? {
        val trimmedText = text.trim()

        return when {
            trimmedText.isEmpty() -> EchoError.EmptyInput
            trimmedText.length < minLength -> EchoError.MinimumLength(minLength)
            trimmedText.any { it.isISOControl() } -> EchoError.UnsupportedCharacters
            else -> null
        }
    }
}
