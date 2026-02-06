# 🐾 VeterinariaApp - Semana 5: Gestión de Memoria y Optimización

## 📖 Descripción del Proyecto
**VeterinariaApp** es una solución móvil integral diseñada para la gestión de atenciones veterinarias y ventas de farmacia. Durante esta quinta semana, el proyecto se ha centrado en la **detección y corrección de 'memory leaks' y en la optimización del uso de memoria** utilizando herramientas profesionales como Android Profiler y LeakCanary.

---

## 📄 Documentación de la Actividad (Semana 5)
Puedes revisar el informe detallado con las evidencias de perfilado de memoria, implementación de LeakCanary y las correcciones de código en el siguiente enlace:

👉 **[Ver Informe de Gestión de Memoria (PDF)](./Documentación/Liliana_Tapia_Gestion_Memoria_S5.pdf)**

*También puedes encontrar la documentación técnica general aquí:*
👉 **[Ver Informe de Documentación Técnica Básica (PDF)](./Documentación/Informe%20documentación%20técnica%20básica.pdf)**

---

## 🛠️ Avances Semana 5: Gestión de Memoria

### 1. Diagnóstico Inicial (Android Profiler)
- Análisis del **Heap** en tiempo real para monitorear el consumo de memoria durante la ejecución de flujos completos de la aplicación.
- Identificación de un comportamiento estable y saludable de la memoria, sin fugas evidentes en el uso normal.
- Uso de **Heap Dumps** para obtener una "fotografía" detallada de los objetos en memoria en un instante específico.

### 2. Detección Automática (LeakCanary)
- Integración de **LeakCanary 2.14** en la compilación de `debug` para la detección automática de fugas de memoria en `Activities`, `Fragments` y `Views`.
- Tras la ejecución de múltiples flujos de navegación y rotación de pantalla, LeakCanary reportó **0 fugas de memoria**, validando la solidez de la arquitectura MVVM implementada.

### 3. Corrección de Malas Prácticas (Análisis de Código)
- **Identificación de Fuga Latente:** A pesar de los resultados positivos de las herramientas, un análisis de código estático reveló una mala práctica en `VeterinariaRepository`. El Singleton almacenaba una referencia a un `Context` que podía ser una `Activity`, creando un riesgo de **Memory Leak**.
- **Solución Implementada:** Se modificó el repositorio para que utilice exclusivamente `context.applicationContext`, garantizando que nunca se retenga una referencia a una pantalla.
- **Optimización de Recursos:** Se mejoró el algoritmo de búsqueda de citas en `AgendaVeterinario` para evitar bucles infinitos en corutinas, previniendo la fuga de recursos de CPU y memoria en hilos de fondo.

### 4. Validación Posterior a la Corrección
- Se repitieron las pruebas con **Profiler** y **LeakCanary** después de las correcciones.
- Los resultados confirmaron que la aplicación no solo sigue libre de fugas, sino que ahora es arquitectónicamente más robusta y segura contra problemas de memoria a futuro.

---

## 🏗️ Pilares Tecnológicos y Arquitectura

### 1. Arquitectura y Patrones
- **MVVM (Model-View-ViewModel):** Separación clara entre la lógica de estado y la interfaz Compose.
- **StateFlow y Coroutines:** Manejo reactivo de estados con optimización de suspensión para tareas asíncronas.
- **Repository Pattern:** Abstracción unificada de fuentes de datos locales.

### 2. Componentes Nativos
- **Services (Foreground):** Feedback mediante notificaciones persistentes.
- **Broadcast Receivers:** Monitoreo global del estado de conectividad.
- **Room Persistence:** Persistencia robusta para Mascotas, Consultas y Pedidos.
- **Content Provider:** Acceso seguro a datos para aplicaciones externas.

---

## 📂 Estructura del Proyecto
```text
cl.duoc.veterinaria
├── data             # Repositorio y persistencia (Room / Entities)
├── model            # Entidades de dominio y modelos de datos
├── service          # Lógica de agenda, costos y NotificacionService
├── ui               # Componentes de interfaz (Compose)
│   ├── registro     # Flujo de agendamiento y pantallas de resumen
│   ├── viewmodel    # Lógica de estado y diagnóstico
│   └── theme        # Tematización adaptativa (Material Design 3)
└── util             # Validaciones (Regex) y funciones de utilidad
```

---
**Desarrollado por:** Liliana Tapia  
**Carrera:** Desarrollo de aplicaciones II
**Institución:** DUOC UC
**Semana:** 5 - Formativa Individual
