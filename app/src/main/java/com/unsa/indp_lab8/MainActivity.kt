package com.unsa.indp_lab8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.indp_lab8.data.UserPreferences
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
                    PreferencesObserverScreen(
                        preferences = preferencesState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Pantalla que demuestra la Tarea 4: observación de los valores expuestos por el ViewModel
 * desde DataStore Preferences en la arquitectura de CampusDocs.
 */
@Composable
fun PreferencesObserverScreen(
    preferences: UserPreferences,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CampusDocs",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "IDNP Lab 08 - Persistencia de Preferencias con DataStore",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "Estado observado reactivamente desde StateFlow (DataStore Preferences):",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Preferencias Activas del Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                PreferenceItemView(
                    label = "Tema de la aplicación (DARK_MODE):",
                    value = if (preferences.darkMode) "Oscuro (Activado)" else "Claro (Desactivado)"
                )

                PreferenceItemView(
                    label = "Distribución de apuntes (GRID_VIEW):",
                    value = if (preferences.gridView) "Cuadrícula (2 columnas)" else "Lista vertical (1 columna)"
                )

                PreferenceItemView(
                    label = "Filtro predeterminado (SELECTED_FACULTY):",
                    value = preferences.selectedFaculty
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Nota de Arquitectura (Tareas 3 y 4):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "El repositorio lee de DataStore mediante Flow<Preferences> y el ViewModel lo expone mediante StateFlow<UserPreferences>. Cuando se modifiquen los valores en la Tarea 5, este componente se recompondrá de forma automática e inmediata.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun PreferenceItemView(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesObserverScreenPreview() {
    INDP_LAB8Theme {
        PreferencesObserverScreen(
            preferences = UserPreferences(
                darkMode = true,
                gridView = true,
                selectedFaculty = "Ingeniería de Sistemas"
            )
        )
    }
}
