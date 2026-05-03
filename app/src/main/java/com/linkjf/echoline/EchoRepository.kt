package com.linkjf.echoline

import kotlinx.coroutines.delay

interface EchoRepository {
    suspend fun validate(text: String): Boolean
}

class FakeEchoRepository(private val delayMillis: Long = 700L) : EchoRepository {
    override suspend fun validate(text: String): Boolean {
        if (delayMillis > 0L) {
            delay(delayMillis)
        }

        return !text.contains("fail", ignoreCase = true) &&
            !text.contains("error", ignoreCase = true)
    }
}
