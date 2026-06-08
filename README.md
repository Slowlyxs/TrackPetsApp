# TrackPets Android - Documentación Técnica

Aplicación móvil Android desarrollada nativamente con Kotlin y Jetpack Compose, enfocada en la gestión y rastreo de mascotas mediante dispositivos GPS y geocercas virtuales. 

> [!NOTE]
> Este repositorio contiene el código fuente del cliente Android. La persistencia de datos y lógica de servidor se encuentra desplegada en un backend independiente (API RESTful) alojado en la nube.

---

## Arquitectura del Software

El proyecto ha sido diseñado bajo los principios de **Clean Architecture** y el patrón **MVVM** (Model-View-ViewModel), garantizando un alto grado de desacoplamiento, mantenibilidad y escalabilidad del código.

### Capa de Datos (`data`)
Encargada del acceso a fuentes de información externas e internas.
- **`network/`**: Implementación de Retrofit2 para consumo HTTP. Incluye interceptores de autenticación (JWT) para la cabecera `Authorization`.
- **`repository/`**: Implementaciones concretas de las interfaces del dominio. Orquestan la transformación de los DTOs (Data Transfer Objects) provenientes de la red a modelos de dominio puros.
- **`model/`**: Entidades exclusivas para el parseo JSON de la API.

### Capa de Dominio (`domain`)
El núcleo de la aplicación. Es completamente agnóstica de Android o librerías de terceros (UI/Red).
- **`model/`**: Modelos de negocio puros (`Pet`, `Owner`, `Device`, `Geofence`).
- **`repository/`**: Contratos (interfaces) que definen las reglas de acceso a datos sin especificar su origen.

### Capa de Presentación (`presentation`)
Responsable de la interfaz gráfica y de reaccionar a la interacción del usuario. Construida al 100% con **Jetpack Compose** (Material Design 3).
- **`uiState/`**: Gestión de estados encapsulados mediante `UiState<T>` (Manejo de Loading, Success, Error y Empty).
- **`navigation/`**: Grafo de navegación centralizado (`NavHost`) que protege el acceso a pantallas según el estado de sesión.
- **Views y ViewModels**: Cada módulo lógico (`pets`, `owners`, etc.) cuenta con sus Composables y su ViewModel (que expone `StateFlows`).

---

##  Guía de Compilación (Build Instructions)

### Requisitos Previos
- **IDE**: Android Studio Iguana (2023.2.1) o superior.
- **Java**: JDK 17.
- **Android SDK**: Compilado con API 34 (Mínimo requerido API 24 - Android 7.0).
- **Gradle**: Versión 8.x con soporte para Kotlin DSL (`.kts`).

### Pasos para compilar desde código fuente
1. Clonar el repositorio.
2. Abrir el proyecto desde la raíz en Android Studio.
3. Esperar a que Gradle sincronice las dependencias automáticamente (Sync Project with Gradle Files).
4. En la barra superior, seleccionar el target `app` y ejecutar `Run` (Shift + F10) en un emulador o dispositivo físico.

> [!WARNING]
> La aplicación requiere conexión a internet para funcionar, ya que no utiliza una base de datos local (Room) persistente, sino que consulta directamente la API.

---

##  APK Generado (Producción y Debug)

Si no cuentas con el entorno de desarrollo y solo necesitas probar la aplicación, puedes acceder directamente al instalador compilado (`.apk`).

Para generar el APK localmente mediante terminal:
```bash
# En Windows (Powershell / CMD)
.\gradlew assembleDebug

# En Linux / macOS
./gradlew assembleDebug
```

### Ubicación del archivo APK
Una vez finalizada la compilación, el archivo instalable se depositará en el siguiente directorio relativo a la raíz del proyecto:
`app/build/intermediates/apk/debug/app-debug.apk`

> [!TIP]
> Puedes enviar este archivo `app-debug.apk` a cualquier dispositivo Android (mediante WhatsApp, correo, o cable USB) e instalarlo directamente asegurándote de tener activada la opción de "Instalar aplicaciones de orígenes desconocidos".
