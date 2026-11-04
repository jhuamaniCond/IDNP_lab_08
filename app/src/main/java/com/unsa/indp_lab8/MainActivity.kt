package com.unsa.indp_lab8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.indp_lab8.ui.screens.PreferencesScreen
import com.unsa.indp_lab8.ui.theme.INDP_LAB8Theme
import com.unsa.indp_lab8.ui.viewmodel.PreferencesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            // Tarea 4: Instanciación del ViewModel con su Factory
            val preferencesViewModel: PreferencesViewModel = viewModel(
                factory = PreferencesViewModel.provideFactory(context)
            )

            // Tarea 4: La interfaz observa reactivamente los cambios emitidos por StateFlow
            val preferencesState by preferencesViewModel.userPreferencesUiState.collectAsState()

            // El tema general de la app responde reactivamente al valor observado de DARK_MODE
            INDP_LAB8Theme(
                darkTheme = preferencesState.darkMode,
                dynamicColor = false
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Tarea 5: los controles de la pantalla envían eventos al ViewModel,
                    // que persiste el cambio en DataStore; el nuevo valor regresa por el StateFlow.
                    PreferencesScreen(
                        preferences = preferencesState,
                        onDarkModeChange = preferencesViewModel::toggleDarkMode,
                        onGridViewChange = preferencesViewModel::toggleGridView,
                        onFacultySelected = preferencesViewModel::selectFaculty,
                        onReset = preferencesViewModel::resetPreferences,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
