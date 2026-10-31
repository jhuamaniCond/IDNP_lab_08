package com.unsa.indp_lab8.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Nombre del archivo de almacenamiento en disco: /data/data/com.unsa.indp_lab8/files/datastore/campusdocs_preferences.preferences_pb
private const val PREFERENCES_DATASTORE_NAME = "campusdocs_preferences"

// Creación de la instancia Singleton de DataStore vinculada al ciclo del Context mediante el delegado
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_DATASTORE_NAME)

/**
 * Tarea 3: Repositorio responsable de la persistencia, lectura y actualización de las
 * preferencias de usuario de CampusDocs usando Jetpack DataStore Preferences.
 *
 * Implementa el patrón Repository para abstraer la fuente de datos subyacente y ofrece
 * una API reactiva basada en Kotlin Coroutines y Flow.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * Constructor de conveniencia que utiliza el Context de la aplicación para
     * acceder a la instancia delegate de DataStore.
     */
    constructor(context: Context) : this(context.applicationContext.userDataStore)

    /**
     * Flujo reactivo (Flow) que emite el estado actual de las preferencias del usuario
     * cada vez que ocurre un cambio en disco.
     *
     * Incluye manejo seguro de excepciones de entrada/salida (IOException) emitiendo
     * un conjunto de preferencias vacío para evitar que la aplicación falle ante
     * archivos corruptos o bloqueos de lectura.
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                // Si ocurre un error de lectura de archivo en disco, emitir preferencias vacías para usar los valores por defecto
                emit(emptyPreferences())
            } else {
                // Propagar cualquier otro error inesperado
                throw exception
            }
        }
        .map { preferences ->
            mapUserPreferences(preferences)
        }

    /**
     * Mapea el objeto genérico [Preferences] de DataStore a nuestro modelo de dominio [UserPreferences].
     * Si una clave aún no ha sido guardada en disco, se asigna su respectivo valor por defecto.
     */
    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val darkMode = preferences[UserPreferencesKeys.DARK_MODE] ?: false
        val gridView = preferences[UserPreferencesKeys.GRID_VIEW] ?: false
        val selectedFaculty = preferences[UserPreferencesKeys.SELECTED_FACULTY] ?: "Ingeniería de Sistemas"

        return UserPreferences(
            darkMode = darkMode,
            gridView = gridView,
            selectedFaculty = selectedFaculty
        )
    }

    // ========================================================================
    // OPERACIONES DE ACTUALIZACIÓN / ESCRITURA TRANSACCIONAL (suspend functions)
    // ========================================================================

    /**
     * Actualiza la preferencia del Modo Oscuro en DataStore.
     * La operación [edit] es atómica, consistente y se ejecuta en un hilo de E/S.
     *
     * @param enabled true para activar el modo oscuro, false para modo claro.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.DARK_MODE] = enabled
        }
    }

    /**
     * Actualiza el tipo de visualización del material académico (Cuadrícula o Lista).
     *
     * @param isGrid true para vista en cuadrícula, false para lista.
     */
    suspend fun setGridView(isGrid: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.GRID_VIEW] = isGrid
        }
    }

    /**
     * Actualiza la facultad universitaria seleccionada para el filtrado de apuntes.
     *
     * @param faculty Nombre de la facultad académica.
     */
    suspend fun setSelectedFaculty(faculty: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SELECTED_FACULTY] = faculty
        }
    }

    /**
     * Actualiza simultáneamente todas las preferencias en una sola transacción atómica en disco.
     */
    suspend fun updateAllPreferences(darkMode: Boolean, gridView: Boolean, faculty: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.DARK_MODE] = darkMode
            preferences[UserPreferencesKeys.GRID_VIEW] = gridView
            preferences[UserPreferencesKeys.SELECTED_FACULTY] = faculty
        }
    }

    /**
     * Restablece todas las preferencias a sus valores predeterminados eliminando las entradas.
     */
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.DARK_MODE)
            preferences.remove(UserPreferencesKeys.GRID_VIEW)
            preferences.remove(UserPreferencesKeys.SELECTED_FACULTY)
        }
    }
}
