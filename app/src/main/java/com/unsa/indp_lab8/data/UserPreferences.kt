package com.unsa.indp_lab8.data

/**
 * Tarea 3: Modelo de datos inmutable que representa las preferencias persistentes del usuario
 * en la plataforma académica CampusDocs.
 *
 * Centraliza las 3 claves configuradas en [UserPreferencesKeys]:
 * @property darkMode true si la interfaz debe renderizarse con tema oscuro; false para tema claro.
 * @property gridView true si el material académico se organiza en cuadrícula interactiva; false para lista vertical.
 * @property selectedFaculty Facultad universitaria priorizada para el filtrado inicial de apuntes.
 */
data class UserPreferences(
    val darkMode: Boolean = false,
    val gridView: Boolean = false,
    val selectedFaculty: String = "Ingeniería de Sistemas"
)
