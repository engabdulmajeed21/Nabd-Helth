package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.AppNavGraph
import com.example.ui.theme.AuratechTheme
import com.example.viewmodel.AuratechViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AuratechViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val language by viewModel.language.collectAsState()

            AuratechTheme(
                themeMode = themeMode,
                language = language
            ) {
                AppNavGraph(viewModel = viewModel)
            }
        }
    }
}
