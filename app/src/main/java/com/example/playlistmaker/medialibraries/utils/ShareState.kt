package com.example.playlistmaker.medialibraries.utils

sealed class ShareState {
    object Default : ShareState()
    object Empty : ShareState()
    data class Content(val message: String) : ShareState()
}