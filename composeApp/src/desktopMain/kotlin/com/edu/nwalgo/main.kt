package com.edu.nwalgo

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.edu.nwalgo.graphics.app

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Animated Matrix") {
        MaterialTheme {
            app()

        }
    }
}
