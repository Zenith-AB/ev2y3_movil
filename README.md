# RuletAPP - Aplicación de Ruleta Interactiva

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

Aplicación Android nativa desarrollada con Jetpack Compose que permite crear y girar una ruleta personalizable con opciones definidas por el usuario.

## 📱 Características

- ✅ **Ruleta Animada**: Giro suave con animaciones fluidas
- ✅ **Gestión de Opciones**: CRUD completo para personalizar la ruleta
- ✅ **Historial de Resultados**: Registro de todos los giros realizados
- ✅ **Temas Personalizables**: Clásico, Oscuro y Pastel
- ✅ **Frases Motivadoras**: Integración con API externa (ZenQuotes)
- ✅ **Base de Datos**: PostgreSQL en Supabase con sincronización automática
- ✅ **Notificaciones**: Alertas locales al obtener resultados

## 🏗️ Arquitectura

### Backend
- **Base de Datos**: PostgreSQL en Supabase
- **ORM Local**: Room Database
- **Sincronización**: Bidireccional Room ↔ Supabase
- **API REST**: Retrofit + OkHttp

### Frontend
- **UI Framework**: Jetpack Compose + Material3
- **Navegación**: Jetpack Navigation Compose
- **Gestión de Estado**: StateFlow + ViewModel
- **Animaciones**: Compose Animation API

### API Externa
- **ZenQuotes API**: Frases motivadoras aleatorias
- **Endpoint**: `https://zenquotes.io/api/random`

## 📦 Dependencias Principales

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose")

// Room Database
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")

// Retrofit (API REST)
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Testing
testImplementation("junit:junit")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
```

## 🚀 Instalación

### Requisitos
- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 11
- Android SDK API 24+ (Android 7.0+)

### Pasos

1. **Clonar el repositorio**
```bash
git clone https://github.com/TU_USUARIO/ev2y3_movil-master.git
cd ev2y3_movil-master
```

2. **Abrir en Android Studio**
- File → Open → Seleccionar la carpeta del proyecto

3. **Sync Gradle**
- Android Studio sincronizará automáticamente las dependencias

4. **Ejecutar la app**
- Conectar un dispositivo Android o iniciar un emulador
- Click en "Run" (▶️) o `Shift + F10`

## 📱 APK Release

El APK firmado se encuentra en:
```
app/build/outputs/apk/release/app-release.apk
```

### Generar APK
```bash
./gradlew assembleRelease
```

## 🧪 Testing

### Ejecutar pruebas unitarias
```bash
./gradlew test
```

### Pruebas incluidas
- `RuletaDaoTest`: Operaciones CRUD de la base de datos
- `RuletaRepositoryTest`: Sincronización y lógica de negocio
- `LogicaRuletaTest`: Validación de selección aleatoria

## 🗄️ Base de Datos

### Supabase Configuration
- **URL**: `https://zetlbhufsklsogjzritx.supabase.co/`
- **Tablas**:
  - `opciones`: Almacena las opciones de la ruleta
  - `resultados`: Registro de resultados de giros

### Room Database
- **Entities**: `OpcionItem`, `ResultadoItem`
- **DAO**: `RuletaDao`
- **Repository**: `RuletaRepository`

## 📸 Capturas de Pantalla

### Pantalla Principal
- Ruleta animada con opciones personalizadas
- Botón "GIRAR" en el centro
- Lista de opciones disponibles

### Gestión de Opciones
- Agregar nuevas opciones
- Eliminar opciones existentes
- Validación de entrada

### Historial
- Lista de resultados con timestamps
- Opción para limpiar historial

### Temas
- Selector de temas visuales
- Vista previa en tiempo real

## 👥 Autores

- **Desarrollador**: RuletAPP Team
- **Institución**: DUOC UC
- **Curso**: Programación Móvil

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 🔗 Enlaces

- [Supabase Dashboard](https://supabase.com/dashboard)
- [ZenQuotes API](https://zenquotes.io/)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)

## 📝 Notas de Desarrollo

### Configuración de Firma
El proyecto incluye configuración de firma para releases:
- Keystore: `app/ruletapp-keystore.jks`
- Alias: `ruletapp-key`
- **⚠️ IMPORTANTE**: No subir el keystore a repositorios públicos

### API Keys
Las credenciales de Supabase están incluidas para propósitos educativos.
En producción, usar variables de entorno.

## 🐛 Problemas Conocidos

- La API de ZenQuotes puede tener límite de requests
- Requiere conexión a internet para sincronización con Supabase

## 🔄 Roadmap

- [ ] Modo offline completo
- [ ] Compartir resultados en redes sociales
- [ ] Estadísticas avanzadas
- [ ] Exportar/Importar opciones
- [ ] Soporte para múltiples ruletas

---

**Desarrollado con ❤️ usando Kotlin y Jetpack Compose**
