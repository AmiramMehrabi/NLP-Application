# Intelligent NLP Pipeline: Translation & Entity Extraction 🧠

An advanced Android application demonstrating **On-Device Machine Learning** using Google ML Kit. This project implements a dual-stage NLP pipeline to process Persian text, translate it to English, and extract semantic entities in real-time.

## 🚀 Overview

This project bridges the gap between modern Android development and AI. It was developed to evaluate the performance of **Google ML Kit** in a practical scenario involving low-resource languages (Persian).

**The Pipeline:**
1.  **Input:** User enters text in Persian (Farsi).
2.  **Stage 1 (Translation):** The app translates the text to English using the **ML Kit Translation API** (On-Device).
3.  **Stage 2 (NER):** The translated text is passed to the **Entity Extraction API** to identify key information such as *Dates, Places, Organizations, and Person Names*.

## 🛠 Tech Stack

* **Language:** Kotlin (100%)
* **UI Framework:** Jetpack Compose (Modern Toolkit)
* **Architecture:** MVVM (Model-View-ViewModel)
* **AI/ML:** Google ML Kit (On-Device Inference)
    * Translation API
    * Entity Extraction API
* **Concurrency:** Kotlin Coroutines & Flow

## ⚡ Key Features

* **Offline-First AI:** All processing happens on the device, ensuring user privacy and zero network latency.
* **Reactive UI:** Built with Jetpack Compose for a declarative and smooth user experience.
* **Clean Architecture:** Separation of logic using `MainViewModel` to manage state and ML pipeline execution.

## 📸 Usage Example

**Input (Persian):**
> "شرکت پردازش موازی قصد دارد در تاریخ ۱۰ خرداد ۱۴۰۴ کنفرانس خود را در هتل اسپیناس تهران برگزار کند."

**Output (Processed):**
* **Translation:** "Parallel Processing Company plans to hold its conference on June 10, 2025, at Espinas Hotel in Tehran."
* **Extracted Entities:**
    * 🏢 **Organization:** Parallel Processing Company
    * 📅 **Date:** June 10, 2025
    * 📍 **Location:** Tehran, Espinas Hotel

## 🔧 Future Improvements

* Implement Speech-to-Text (STT) for voice input.
* Add support for more source languages.
* Enhance error handling for low-confidence translations.

---
*Developed by **Amir Mehrabi** as part of an academic research on NLP systems.*
