package com.unsa.indp_lab8.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Punto 2: Definición de Claves de Preferencias (Preferences Keys) para CampusDocs.
 * Las claves en DataStore son objetos fuertemente tipados (Preferences.Key<T>).
 */
object UserPreferencesKeys {
    // 1. Clave booleana para guardar el estado del Tema Oscuro en CampusDocs
    val DARK_MODE = booleanPreferencesKey("dark_mode")

    // 2. Clave booleana para la vista de apuntes universitarios (true = Cuadrícula, false = Lista)
    val GRID_VIEW = booleanPreferencesKey("grid_view")

    // 3. Clave de texto para guardar la facultad preferida del estudiante (ej. "Ingeniería de Sistemas")
    val SELECTED_FACULTY = stringPreferencesKey("selected_faculty")
}
