package com.example.nlpapplication.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.entityextraction.DateTimeEntity
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractor
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import com.google.mlkit.nl.entityextraction.FlightNumberEntity
import com.google.mlkit.nl.entityextraction.MoneyEntity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var persianToEnglishTranslator: Translator? = null
    private var entityExtractor: EntityExtractor? = null

    init {
        prepareTranslator()
        prepareEntityExtractor()
    }

    private fun prepareEntityExtractor() {
        viewModelScope.launch {
            if (entityExtractor != null) return@launch
            try {
                val options = EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
                    .build()
                entityExtractor = EntityExtraction.getClient(options)
                entityExtractor!!.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = null
                        )
                        Log.d("MainViewModel", "دانلود مدل استخراج با موفقیت انجام شد.")
                    }
                    .addOnFailureListener {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "خطا در دانلود مدل استخراج: ${it.message}"
                        )
                        Log.e("MainViewModel", "خطا در دانلود مدل استخراج: ${it.message}")
                    }
            } catch (e: Exception) {
                persianToEnglishTranslator = null
                _uiState.value = _uiState.value.copy(
                    errorMessage = "خطا در آماده سازی مدل استخراج: ${e.message}"
                )
            }
        }
    }

    private fun prepareTranslator() {
        viewModelScope.launch {
            if (persianToEnglishTranslator != null) return@launch
            try {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.PERSIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build()
                persianToEnglishTranslator = Translation.getClient(options)

                val conditions = DownloadConditions.Builder()
                    //.requireWifi()
                    .build()
                persianToEnglishTranslator!!.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = null
                        )
                        Log.d("MainViewModel", "دانلود مدل با موفقیت انجام شد.")
                    }
                    .addOnFailureListener {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "خطا در دانلود مدل زبانی: ${it.message}"
                        )
                        Log.e("MainViewModel", "خطا در دانلود مدل زبانی: ${it.message}")
                    }
            } catch (e: Exception) {
                persianToEnglishTranslator = null
                _uiState.value = _uiState.value.copy(
                    errorMessage = "خطا در آماده سازی مترجم: ${e.message}"
                )
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.InputTextChange -> {
                _uiState.value = _uiState.value.copy(inputText = intent.inputText)
            }

            MainIntent.TranslateButtonClick -> {
                _uiState.value = _uiState.value.copy(
                    isProcessing = true
                )
                processText()
                _uiState.value = _uiState.value.copy(
                    isProcessing = false
                )
            }
        }
    }

    private fun processText() {
        val inputText = _uiState.value.inputText
        if (inputText.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "لطفا متن خود را وارد کنید."
            )
            return
        }
        if (persianToEnglishTranslator == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "مترجم همچنان آماده نیست. دوباره تلاش کنید."
            )
            prepareTranslator()
            return
        }

        if (entityExtractor == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "مدل استخراج همچنان آماده نیست. دوباره تلاش کنید."
            )
            prepareEntityExtractor()
            return
        }
        translateText(inputText)

        /*_uiState.value = _uiState.value.copy(
            showResult = true
        )*/
    }

    private fun extractEntities(translatedText: String) {
        viewModelScope.launch {


            val entitiesForUi = mutableListOf<Pair<String, String>>()
            Log.d("MainViewModel", "translatedText: $translatedText")
            entityExtractor!!.annotate(translatedText).addOnSuccessListener { entityAnnotations ->
                for (entityAnnotation in entityAnnotations) {
                    val entities: List<Entity> = entityAnnotation.entities
                    val annotatedText = entityAnnotation.annotatedText
                    for (entity in entities) {
                        Log.d("MainViewModel", "entity: ${entity.type} ${annotatedText}")
                        when (entity.type) {
                            Entity.TYPE_ADDRESS -> entitiesForUi.add(Pair("Address", annotatedText))
                            Entity.TYPE_DATE_TIME -> entitiesForUi.add(Pair("Date", annotatedText))
                            Entity.TYPE_EMAIL -> entitiesForUi.add(Pair("Email", annotatedText))
                            Entity.TYPE_FLIGHT_NUMBER -> entitiesForUi.add(
                                Pair(
                                    "Flight Number",
                                    annotatedText
                                )
                            )

                            Entity.TYPE_IBAN -> entitiesForUi.add(Pair("IBAN", annotatedText))
                            Entity.TYPE_ISBN -> entitiesForUi.add(Pair("ISBN", annotatedText))
                            Entity.TYPE_MONEY -> entitiesForUi.add(Pair("Money", annotatedText))
                            Entity.TYPE_PAYMENT_CARD -> entitiesForUi.add(
                                Pair(
                                    "Payment Card",
                                    annotatedText
                                )
                            )

                            Entity.TYPE_PHONE -> entitiesForUi.add(Pair("Phone", annotatedText))
                            Entity.TYPE_TRACKING_NUMBER -> entitiesForUi.add(
                                Pair(
                                    "Tracking Number",
                                    annotatedText
                                )
                            )

                            Entity.TYPE_URL -> entitiesForUi.add(Pair("URL", annotatedText))
                            else -> entitiesForUi.add(Pair("Others", annotatedText))
                        }
                    }
                }
                _uiState.value = _uiState.value.copy(
                    extractedEntities = entitiesForUi,
                    showResult = true,
                    isProcessing = false,
                )
            }
            for (p in entitiesForUi) {
                Log.d("MainViewModel", "entity: ${p.first} ${p.second}")
            }

            Log.d("MainViewModel", "entitiesForUi: ${_uiState.value.extractedEntities}")
        }
    }

    private fun translateText(inputText: String) {
        viewModelScope.launch {
            persianToEnglishTranslator!!.translate(inputText).addOnSuccessListener {
                _uiState.value = _uiState.value.copy(
                    translatedText = it
                )
                extractEntities(it)
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        persianToEnglishTranslator?.close()
        entityExtractor?.close()
    }
}