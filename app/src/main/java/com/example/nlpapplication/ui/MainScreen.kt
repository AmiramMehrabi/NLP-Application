package com.example.nlpapplication.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


@Composable
fun MainScreen(
    uiState: MainUiState,
    onIntent: (MainIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MainScreen(
        inputText = uiState.inputText,
        translatedText = uiState.translatedText,
        isProcessing = uiState.isProcessing,
        errorMessage = uiState.errorMessage,
        showResult = uiState.showResult,
        extractedEntities = uiState.extractedEntities,
        onInputTextChange = { newText: String ->
            onIntent(MainIntent.InputTextChange(newText))
        },
        onTranslateButtonClick = {
            onIntent(MainIntent.TranslateButtonClick)
        },
        modifier = modifier,
    )

}

@Composable
private fun MainScreen(
    inputText: String,
    translatedText: String,
    isProcessing: Boolean,
    errorMessage: String?,
    showResult: Boolean,
    extractedEntities: List<Pair<String, String>>,
    onInputTextChange: (String) -> Unit,
    onTranslateButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (showResult) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Translated Text:",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = translatedText,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(24.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(extractedEntities) { item ->
                        EntityItem(item)
                    }
                }
            }
        } else {
            Column {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = onInputTextChange,
                        label = { Text(text = "متن فارسی را وارد کنید") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage != null,
                        supportingText = if (errorMessage != null) {
                            { Text(text = errorMessage, color = MaterialTheme.colorScheme.error) }
                        } else {
                            null
                        },
                        enabled = !isProcessing,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onTranslateButtonClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator()
                    } else {
                        Text("Process")
                    }
                }
            }
        }
    }
}

@Composable
private fun EntityItem(
    entity: Pair<String, String>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entity.first,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = entity.second,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(3f)
            )
        }
    }

}


data class MainUiState(
    val inputText: String = "",
    val translatedText: String = "",
    val isProcessing: Boolean = false,
    val errorMessage: String? = null,
    val showResult: Boolean = false,
    val extractedEntities: List<Pair<String, String>> = emptyList(),
)

sealed interface MainIntent {
    data class InputTextChange(val inputText: String) : MainIntent
    data object TranslateButtonClick : MainIntent
}