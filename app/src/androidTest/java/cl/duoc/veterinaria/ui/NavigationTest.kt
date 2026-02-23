package cl.duoc.veterinaria.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import cl.duoc.veterinaria.MainActivity
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFullNavigationToRegistration() {
        performLoginOrRegister()

        // 1. Esperar a que la pantalla de Bienvenida cargue
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onAllNodesWithText("Nuevo Registro").onFirst().assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // 2. Click en Nuevo Registro
        composeTestRule.onAllNodesWithText("Nuevo Registro").onFirst().performClick()

        // 3. Verificar Pantalla de Contacto (DuenoScreen)
        composeTestRule.onNodeWithText("Información de Contacto", substring = true, ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun testNavigationToAgenda() {
        performLoginOrRegister()

        // 1. Esperar a que la pantalla de Bienvenida cargue
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onAllNodesWithText("Ver Agenda").onFirst().assertExists()
                true
            } catch (e: Throwable) { false }
        }

        // 2. Click en Ver Agenda
        composeTestRule.onAllNodesWithText("Ver Agenda").onFirst().performClick()

        // 3. Verificar que estamos en la Agenda (Basado en AgendaScreen.kt)
        // Buscamos "Mi Agenda" o "Próximas Citas"
        composeTestRule.onNodeWithText("Mi Agenda", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Próximas Citas", ignoreCase = true).assertIsDisplayed()
    }

    /**
     * Lógica de apoyo para asegurar que el test esté logueado,
     * registrando un usuario de prueba si es necesario.
     */
    private fun performLoginOrRegister() {
        composeTestRule.waitForIdle()
        try {
            // Si vemos el botón de INICIAR SESIÓN, procedemos
            val loginButton = composeTestRule.onNodeWithText("INICIAR SESIÓN", ignoreCase = true)
            loginButton.assertExists()
            
            composeTestRule.onNodeWithText("Usuario o Email").performTextInput("admin")
            composeTestRule.onNodeWithText("Contraseña").performTextInput("admin")
            loginButton.performClick()

            // Verificamos si falló por "usuario incorrecto"
            var loginFallido = false
            try {
                // Esperamos un segundo para ver si aparece el error
                composeTestRule.onNodeWithText("incorrectos", substring = true).assertExists()
                loginFallido = true
            } catch (e: Throwable) {}

            if (loginFallido) {
                // Ir a registro y crear el usuario admin
                composeTestRule.onNodeWithText("Regístrate aquí", substring = true).performClick()
                composeTestRule.onNodeWithText("Nombre de usuario").performTextInput("admin")
                composeTestRule.onNodeWithText("Correo electrónico").performTextInput("admin@test.cl")
                composeTestRule.onAllNodesWithText("Contraseña").onFirst().performTextInput("admin")
                composeTestRule.onNodeWithText("REGISTRARSE").performClick()
            }
        } catch (e: Throwable) {
            // Ya estamos logueados
        }
    }
}
