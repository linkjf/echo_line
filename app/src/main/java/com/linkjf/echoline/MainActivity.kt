package com.linkjf.echoline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.linkjf.echoline.ui.theme.EchoLineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoLineTheme {
                val viewModel: EchoViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EchoScreen(
                        uiState = uiState,
                        onTextChange = viewModel::onInputChanged,
                        onSubmit = viewModel::onSubmit,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
