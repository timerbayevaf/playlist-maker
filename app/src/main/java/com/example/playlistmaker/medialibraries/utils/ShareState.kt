package com.example.playlistmaker.medialibraries.utils

sealed interface ShareState {
    object Default : ShareState
    object Empty : ShareState
    data class Content(val message: String) : ShareState
}