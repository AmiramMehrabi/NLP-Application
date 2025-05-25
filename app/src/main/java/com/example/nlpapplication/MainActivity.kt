package com.example.nlpapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nlpapplication.ui.MainScreen
import com.example.nlpapplication.ui.MainUiState
import com.example.nlpapplication.ui.MainViewModel
import com.example.nlpapplication.ui.theme.NLPApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NLPApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: MainViewModel by viewModels()
                    MainScreen(
                        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                        onIntent = viewModel::onIntent,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
