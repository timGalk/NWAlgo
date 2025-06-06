package com.edu.nwalgo

import AppNavHost
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Animated Matrix", resizable = true) {
        MaterialTheme {
            AppNavHost()
        }
    }
}
