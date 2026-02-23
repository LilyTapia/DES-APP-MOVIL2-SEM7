package cl.duoc.veterinaria.ui.viewmodel

import app.cash.turbine.test
import cl.duoc.veterinaria.data.IVeterinariaRepository
import cl.duoc.veterinaria.model.TipoServicio
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: IVeterinariaRepository
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setup() {
        // Configuramos el despachador de pruebas para que las corrutinas funcionen en el test
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = RegistroViewModel(repository)
    }

    @After
    fun tearDown() {
        // Limpiamos el despachador al terminar
        Dispatchers.resetMain()
    }

    @Test
    fun `updateDatosDueno actualiza correctamente el estado`() = runTest {
        // Usamos Turbine para testear el Flow
        viewModel.uiState.test {
            // El primer item es el estado inicial
            val initialState = awaitItem()
            
            viewModel.updateDatosDueno(nombre = "Liliana", telefono = "912345678")
            
            // El segundo item es el estado actualizado
            val updatedState = awaitItem()
            assertEquals("Liliana", updatedState.duenoNombre)
            assertEquals("912345678", updatedState.duenoTelefono)
        }
    }

    @Test
    fun `updateTipoServicio actualiza correctamente el estado`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Ignorar estado inicial
            
            viewModel.updateTipoServicio(TipoServicio.CIRUGIA)
            
            val updatedState = awaitItem()
            assertEquals(TipoServicio.CIRUGIA, updatedState.tipoServicio)
        }
    }

    @Test
    fun `clearData resetea el estado a los valores iniciales`() = runTest {
        // Seteamos un dato primero
        viewModel.updateDatosDueno(nombre = "Liliana")
        
        viewModel.uiState.test {
            // Capturamos el estado con datos
            val stateWithData = awaitItem()
            assertEquals("Liliana", stateWithData.duenoNombre)
            
            // Ejecutamos la limpieza
            viewModel.clearData()
            
            // Verificamos que volvió al estado inicial (vacío)
            val resetState = awaitItem()
            assertEquals("", resetState.duenoNombre)
        }
    }
}
