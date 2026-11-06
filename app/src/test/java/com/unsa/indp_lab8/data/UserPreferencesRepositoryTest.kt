package com.unsa.indp_lab8.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Tarea 7: pruebas del valor inicial, del cambio de preferencia y del reinicio.
 *
 * Se usa un DataStore real escrito en un archivo temporal. El "reinicio" de la app se
 * simula cancelando el CoroutineScope que mantiene vivo al DataStore (como ocurre al
 * matar el proceso) y creando una instancia nueva sobre el mismo archivo: lo que se lea
 * entonces solo puede venir del disco, no de la memoria.
 */
class UserPreferencesRepositoryTest {

    @get:Rule
    val carpeta = TemporaryFolder()

    private lateinit var archivo: File
    private lateinit var scopeJob: Job

    @Before
    fun setUp() {
        archivo = File(carpeta.root, "campusdocs_test.preferences_pb")
    }

    @After
    fun tearDown() = runBlocking { scopeJob.cancelAndJoin() }

    /** Crea un DataStore sobre [archivo] con su propio scope, como haría un proceso nuevo. */
    private fun abrirDataStore(): DataStore<Preferences> {
        scopeJob = Job()
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + scopeJob),
            produceFile = { archivo }
        )
    }

    /** Simula cerrar la app: libera el DataStore y abre otro sobre el mismo archivo. */
    private suspend fun reiniciar(): UserPreferencesRepository {
        scopeJob.cancelAndJoin()
        return UserPreferencesRepository(abrirDataStore())
    }

    @Test
    fun valorInicial_sinDatosGuardados_devuelveValoresPorDefecto() = runBlocking {
        val repo = UserPreferencesRepository(abrirDataStore())

        val prefs = repo.userPreferencesFlow.first()

        assertEquals(UserPreferences(), prefs)
        assertEquals(false, prefs.darkMode)
        assertEquals(false, prefs.gridView)
        assertEquals("Ingeniería de Sistemas", prefs.selectedFaculty)
    }

    @Test
    fun cambioDePreferencia_seEmiteEnElFlow() = runBlocking {
        val repo = UserPreferencesRepository(abrirDataStore())

        repo.setDarkMode(true)
        repo.setGridView(true)
        repo.setSelectedFaculty("Ingeniería Civil")

        val prefs = repo.userPreferencesFlow.first()
        assertEquals(true, prefs.darkMode)
        assertEquals(true, prefs.gridView)
        assertEquals("Ingeniería Civil", prefs.selectedFaculty)
    }

    @Test
    fun reinicio_conservaLasPreferenciasGuardadas() = runBlocking {
        val antes = UserPreferencesRepository(abrirDataStore())
        antes.updateAllPreferences(darkMode = true, gridView = true, faculty = "Ingeniería Industrial")

        val despues = reiniciar()

        assertEquals(
            UserPreferences(darkMode = true, gridView = true, selectedFaculty = "Ingeniería Industrial"),
            despues.userPreferencesFlow.first()
        )
    }

    @Test
    fun restablecer_yReiniciar_vuelveALosValoresPorDefecto() = runBlocking {
        val repo = UserPreferencesRepository(abrirDataStore())
        repo.updateAllPreferences(darkMode = true, gridView = true, faculty = "Ingeniería Electrónica")
        repo.clearPreferences()

        val despues = reiniciar()

        assertEquals(UserPreferences(), despues.userPreferencesFlow.first())
    }
}
