package com.unsa.indp_lab8.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unsa.indp_lab8.data.UserPreferences
import com.unsa.indp_lab8.ui.theme.INDP_LAB8Theme

/** Facultades que el estudiante puede elegir como filtro predeterminado (SELECTED_FACULTY). */
val FACULTADES = listOf(
    "Ingeniería de Sistemas",
    "Ingeniería Civil",
    "Ingeniería Industrial",
    "Ingeniería Electrónica"
)

/** Apuntes de ejemplo de CampusDocs que se filtran por facultad y se muestran en lista o cuadrícula. */
private data class Apunte(val titulo: String, val facultad: String)

private val APUNTES = listOf(
    Apunte("Estructuras de Datos", "Ingeniería de Sistemas"),
    Apunte("Base de Datos II", "Ingeniería de Sistemas"),
    Apunte("Redes y Comunicaciones", "Ingeniería de Sistemas"),
    Apunte("Desarrollo Móvil", "Ingeniería de Sistemas"),
    Apunte("Resistencia de Materiales", "Ingeniería Civil"),
    Apunte("Mecánica de Suelos", "Ingeniería Civil"),
    Apunte("Investigación de Operaciones", "Ingeniería Industrial"),
    Apunte("Gestión de la Calidad", "Ingeniería Industrial"),
    Apunte("Circuitos Eléctricos", "Ingeniería Electrónica"),
    Apunte("Microcontroladores", "Ingeniería Electrónica")
)

/**
 * Tarea 5: Pantalla de configuración de CampusDocs con controles de UI que modifican
 * las preferencias persistentes. Cada control solo notifica el evento (callbacks); la
 * escritura en DataStore la hace el ViewModel, y el nuevo valor vuelve a esta pantalla
 * a través del StateFlow, cerrando el flujo unidireccional de datos.
 */
@Composable
fun PreferencesScreen(
    preferences: UserPreferences,
    onDarkModeChange: (Boolean) -> Unit,
    onGridViewChange: (Boolean) -> Unit,
    onFacultySelected: (String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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

        // ---------------- Tarea 5: controles que modifican la preferencia ----------------
        SeccionCard(titulo = "Configuración (se guarda en DataStore)") {
            SwitchRow(
                label = "Tema oscuro",
                detalle = "DARK_MODE = ${preferences.darkMode}",
                checked = preferences.darkMode,
                onCheckedChange = onDarkModeChange,
                tag = "switch_dark_mode"
            )
            SwitchRow(
                label = "Vista en cuadrícula",
                detalle = "GRID_VIEW = ${preferences.gridView}",
                checked = preferences.gridView,
                onCheckedChange = onGridViewChange,
                tag = "switch_grid_view"
            )
            Text(
                text = "Facultad predeterminada (SELECTED_FACULTY)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            FacultadSelector(
                seleccionada = preferences.selectedFaculty,
                onFacultySelected = onFacultySelected
            )
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("boton_restablecer")
            ) {
                Text("Restablecer valores por defecto")
            }
        }

        // ---------------- Efecto visible de las preferencias ----------------
        SeccionCard(titulo = "Apuntes de ${preferences.selectedFaculty}") {
            ApuntesView(
                apuntes = APUNTES.filter { it.facultad == preferences.selectedFaculty },
                enCuadricula = preferences.gridView
            )
        }
    }
}

@Composable
private fun SeccionCard(titulo: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    detalle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FacultadSelector(seleccionada: String, onFacultySelected: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FACULTADES.forEach { facultad ->
            FilterChip(
                selected = facultad == seleccionada,
                onClick = { onFacultySelected(facultad) },
                label = { Text(facultad.removePrefix("Ingeniería ")) }
            )
        }
    }
}

@Composable
private fun ApuntesView(apuntes: List<Apunte>, enCuadricula: Boolean) {
    // GRID_VIEW decide si se agrupan de 2 en 2 (cuadrícula) o de 1 en 1 (lista)
    val columnas = if (enCuadricula) 2 else 1
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        apuntes.chunked(columnas).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { apunte ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = apunte.titulo,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                // Completa la última fila impar de la cuadrícula para mantener el ancho
                if (fila.size < columnas) {
                    repeat(columnas - fila.size) {
                        Column(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenPreview() {
    INDP_LAB8Theme(dynamicColor = false) {
        PreferencesScreen(
            preferences = UserPreferences(darkMode = false, gridView = true),
            onDarkModeChange = {},
            onGridViewChange = {},
            onFacultySelected = {},
            onReset = {}
        )
    }
}
