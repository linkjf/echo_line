package com.linkjf.echoline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.linkjf.echoline.ui.theme.EchoLineTheme

@Composable
fun EchoScreen(
    uiState: EchoUiState,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
    ) {
        val compactHeight = maxHeight < 520.dp
        val compactWidth = maxWidth < 420.dp
        val compactSpace = compactHeight || compactWidth
        val outerPadding = if (compactSpace) 16.dp else 24.dp
        val cardPadding = if (compactSpace) 20.dp else 24.dp
        val cardMaxWidth = if (maxWidth >= 840.dp) 640.dp else 520.dp
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(outerPadding),
            contentAlignment = if (compactHeight) Alignment.TopCenter else Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = cardMaxWidth),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(cardPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.echo_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.echo_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSubmitting,
                        singleLine = true,
                        label = { Text(stringResource(R.string.echo_input_label)) },
                        isError = uiState.inputError != null,
                        supportingText = {
                            uiState.inputError?.let { Text(it.asText()) }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (uiState.canSubmit) {
                                    onSubmit()
                                }
                            }
                        )
                    )

                    Button(
                        onClick = onSubmit,
                        enabled = uiState.canSubmit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSubmitting) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(stringResource(R.string.echo_validating))
                            }
                        } else {
                            Text(stringResource(R.string.echo_submit))
                        }
                    }

                    uiState.submittedText?.let { submittedText ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.echo_output_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = submittedText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    uiState.errorMessage?.let { errorMessage ->
                        Text(
                            text = errorMessage.asText(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EchoError.asText(): String = when (this) {
    EchoError.EmptyInput -> stringResource(R.string.error_empty_input)
    is EchoError.MinimumLength -> stringResource(R.string.error_minimum_length, minLength)
    EchoError.UnsupportedCharacters -> stringResource(R.string.error_unsupported_characters)
    EchoError.ServerValidationFailed -> stringResource(R.string.error_server_validation_failed)
}

@Preview(showBackground = true)
@Composable
private fun EchoScreenPreview() {
    EchoLineTheme {
        EchoScreen(
            uiState = EchoUiState(inputText = "Hello Joist", submittedText = "Hello Joist"),
            onTextChange = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Compact height", showBackground = true, widthDp = 390, heightDp = 360)
@Composable
private fun EchoScreenCompactHeightPreview() {
    EchoLineTheme {
        EchoScreen(
            uiState = EchoUiState(
                inputText = "fail this",
                inputError = null,
                errorMessage = EchoError.ServerValidationFailed
            ),
            onTextChange = {},
            onSubmit = {}
        )
    }
}

@Preview(name = "Foldable wide", showBackground = true, widthDp = 840, heightDp = 600)
@Composable
private fun EchoScreenFoldableWidePreview() {
    EchoLineTheme {
        EchoScreen(
            uiState = EchoUiState(
                inputText = "Hello foldable",
                submittedText = "Hello foldable"
            ),
            onTextChange = {},
            onSubmit = {}
        )
    }
}
