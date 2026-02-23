# VeterinariaApp - Actividad Formativa 5 (Semana 7)
## Estructurando y preparando tu aplicación para el cierre

### 📝 Descripción de la Actividad
Este proyecto corresponde a la fase de consolidación de la **VeterinariaApp**. El enfoque principal ha sido el diseño de una arquitectura modular y escalable (MVVM), asegurando una correcta separación de responsabilidades y la implementación de pruebas preliminares (unitarias y funcionales) para validar la robustez del software antes de su cierre y publicación.

---

### 🏗️ Arquitectura Implementada: MVVM
Se ha estructurado el código siguiendo el patrón **Model-View-ViewModel**, garantizando que cada componente cumpla con el principio de responsabilidad única:
1.  **Model (Capa de Datos):** 
    *   Gestión de persistencia local con **Room**.
    *   Modelos de datos para el consumo de API REST con **Retrofit**.
2.  **View (Capa de Interfaz):** 
    *   Interfaz moderna y reactiva desarrollada íntegramente en **Jetpack Compose**.
3.  **ViewModel (Capa de Lógica):** 
    *   Uso de `StateFlow` para la gestión de estados de la UI, asegurando que la lógica de negocio permanezca independiente de la vista.
4.  **Repository:** 
    *   Implementación de una capa intermedia que abstrae el origen de los datos (Local/Remoto) para el resto de la aplicación.

---

### 🛠️ Componentes de Android Jetpack Utilizados
*   **Navigation Compose:** Para un flujo de navegación fluido y tipado entre pantallas.
*   **Room Database:** Garantiza la persistencia de datos de mascotas, consultas y usuarios.
*   **ViewModel & StateFlow:** Para mantener el estado de la aplicación de forma consistente frente a cambios de configuración.

---

### 🧪 Estrategia de Pruebas Aplicada
Se han diseñado e implementado pruebas preliminares sobre los componentes principales:

1.  **Pruebas Unitarias (JUnit 4 + MockK):**
    *   **Lógica de Negocio:** Validación de formularios (email, nombres) en `ValidationUtils`.
    *   **Gestión de Estados:** Pruebas sobre `RegistroViewModel` para asegurar que el flujo de datos y la limpieza de información funcionen correctamente usando **Turbine**.
2.  **Pruebas Funcionales (Compose Test / Espresso):**
    *   **Navegación y UX:** Simulación de interacciones de usuario desde el Login hasta el registro de una nueva atención, verificando que la UI responda según lo esperado.

---

### 🚀 Instrucciones para Ejecución
1.  Clonar el repositorio.
2.  Abrir el proyecto en **Android Studio (versión Iguana o superior)**.
3.  Sincronizar el proyecto con los archivos de Gradle.
4.  Para ejecutar las pruebas:
    *   **Unitarias:** Click derecho en la carpeta `test` -> *Run Tests*.
    *   **Funcionales:** Abrir un emulador y ejecutar `./gradlew connectedDebugAndroidTest` desde la terminal.

---

### 📸 Evidencias de Validación
*   **Estructura Arquitectónica:** Visualización de la organización por capas en el IDE.
*   **Logs de Pruebas:** Registros de ejecución exitosa (BUILD SUCCESSFUL) tanto en pruebas unitarias como instrumentadas.

---
**Desarrollado por:** Liliana Tapia  
**Asignatura:** Desarrollo de Aplicaciones Móviles II  
**Institución:** DUOC UC
