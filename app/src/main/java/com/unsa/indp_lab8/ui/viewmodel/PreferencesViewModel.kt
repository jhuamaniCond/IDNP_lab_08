package com.unsa.indp_lab8.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unsa.indp_lab8.data.UserPreferences
import com.unsa.indp_lab8.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Tarea 4: ViewModel responsable de exponer el estado de las preferencias
 * hacia la interfaz de usuario en Jetpack Compose.
 *
 * Utiliza [StateFlow] para permitir que los componentes composables observen
 * de manera reactiva y eficiente cualquier cambio en las preferencias de CampusDocs,
 * garantizando la coherencia entre el almacenamiento persistente y la UI.
 *
 * @param repository Instancia de [UserPreferencesRepository] que gestiona las operaciones con DataStore.
 */
class PreferencesViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    /**
     * Estado observable de tipo [StateFlow] que emite el objeto [UserPreferences].
     *
     * La estrategia [SharingStarted.WhileSubscribed(5_000)]:
     * - Inicia la recolección cuando el primer Composable se suscribe.
     * - Mantiene el flujo activo durante 5000 ms cuando la UI deja de observar
     *   (protege contra recreación de Activity por rotación de pantalla).
     * - Cancela la recolección si la app pasa a segundo plano prolongado, ahorrando recursos.
     * - [initialValue] provee el estado por defecto inmediato mientras se lee el disco por primera vez.
     */
    val userPreferencesUiState: StateFlow<UserPreferences> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UserPreferences()
        )

    /**
     * Notifica el cambio de estado del Modo Oscuro (DARK_MODE).
     * Ejecuta una corrutina en [viewModelScope] para persistir el valor en disco.
     *
     * @param enabled true para activar modo oscuro, false para modo claro.
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }

    /**
     * Notifica el cambio en la distribución visual del contenido (GRID_VIEW).
     *
     * @param isGrid true para vista en cuadrícula, false para vista en lista.
     */
    fun toggleGridView(isGrid: Boolean) {
        viewModelScope.launch {
            repository.setGridView(isGrid)
        }
    }

    /**
     * Notifica la selección de una nueva facultad académica (SELECTED_FACULTY).
     *
     * @param faculty Nombre de la facultad académica seleccionada.
     */
    fun selectFaculty(faculty: String) {
        viewModelScope.launch {
            repository.setSelectedFaculty(faculty)
        }
    }

    /**
     * Restablece todas las preferencias a sus valores predeterminados de fábrica.
     */
    fun resetPreferences() {
        viewModelScope.launch {
            repository.clearPreferences()
        }
    }

    /**
     * Fábrica complementaria (Factory) para construir [PreferencesViewModel] inyectando
     * el repositorio con el contexto de la aplicación, facilitando su instanciación
     * tanto en Compose con viewModel() como en pruebas unitarias.
     */
    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
                        val repository = UserPreferencesRepository(context.applicationContext)
                        return PreferencesViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Clase ViewModel no reconocida: ${modelClass.name}")
                }
            }
    }
}
