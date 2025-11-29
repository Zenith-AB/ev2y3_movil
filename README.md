RuletAPP es una app Android (Jetpack Compose) que permite gestionar una lista de opciones (crear/borrar) almacenadas localmente, girar una ruleta que elige una opción al azar, persistir cada resultado en un historial local y dar feedback nativo al finalizar el giro (haptic) y notificación del resultado.
Tecnologías utilizadas: Kotlin, Jetpack Compose (Material 3), Room (SQLite), Navigation, Lifecycle, Coroutines.

Funcionalidades
1.1 Pantalla Ruleta
muestra la ruleta con animación de giro
lee opciones directamente desde Room (solo lectura)
al terminar el giro resalta (enciende) la opción ganadora en la lista
al terminar el giro guarda el resultado en Room
al terminar el giro dispara haptic feedback (no requiere permisos)
al terminar el giro muestra la notificación “Salió: {opción}”
incluye accesos a: Gestionar y Historial

1.2 Pantalla Gestionar
agregar nueva opción (texto)
eliminar opciones existentes (una por una)
todas las operaciones escriben en Room; la pantalla de ruleta se actualiza automáticamente porque observa el flujo de datos (flujo reactivo)

1.3 Pantalla Historial
lista todos los resultados (tiradas previas) almacenados
permite eliminar resultados (individualmente o en lote, según implementación)

Arquitectura (alto nivel)
2.1 Capa UI (Compose)
PantallaRuleta, PantallaGestion, PantallaHistorial
NavController configurado con las rutas: "ruleta", "gestion", "historial"

2.2 Capa Datos (Room)
AppDatabase: base de datos local
RuletaDao: operaciones sobre opciones y resultados

2.3 Entidades
OpcionItem(id: Int = 0, texto: String)
ResultadoItem(id: Int = 0, resultado: String /*, timestamp: Long? opcional */)

2.4 Estado y reactividad
los Flow<List<...>> del DAO se recogen con collectAsState(initial = emptyList()) para renderizado reactivo en Compose
la animación de la ruleta se realiza con Animatable y LaunchedEffect

2.5 Integración nativa Android
haptic feedback con LocalHapticFeedback
notificaciones con NotificationChannel + NotificationCompat

Modelo de datos (Room)
3.1 Entidades (ejemplo mínimo)

@Entity(tableName = "opciones")
data class OpcionItem(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
val texto: String
)

@Entity(tableName = "resultados")
data class ResultadoItem(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
val resultado: String
// val timestamp: Long = System.currentTimeMillis() // opcional
)

3.2 DAO (firma típica usada por la app)

@Dao
interface RuletaDao {

// Opciones
@Query("SELECT * FROM opciones ORDER BY id DESC")
fun getOpciones(): Flow<List<OpcionItem>>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertOpcion(item: OpcionItem)

@Delete
suspend fun deleteOpcion(item: OpcionItem)

// Resultados
@Query("SELECT * FROM resultados ORDER BY id DESC")
fun getResultados(): Flow<List<ResultadoItem>>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertResultado(item: ResultadoItem)

@Delete
suspend fun deleteResultado(item: ResultadoItem)

// (Opcional) borrar todo
@Query("DELETE FROM resultados")
suspend fun clearResultados()


}

3.3 Base de datos

@Database(entities = [OpcionItem::class, ResultadoItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
abstract fun ruletaDao(): RuletaDao
}

el acceso se hace con un singleton usando Room.databaseBuilder(context, AppDatabase::class.java, "ruleta_database")

Flujo de datos
4.1 Gestionar
insertOpcion() o deleteOpcion() actualizan Room
getOpciones() (Flow) emite el nuevo estado y la pantalla de Ruleta se refresca sola

4.2 Ruleta
lee las opciones usando getOpciones() (Flow)
al hacer click en “girar” calcula la opción ganadora y anima hasta ese sector
después de que termina la animación:

guarda un ResultadoItem con insertResultado()

ejecuta haptic

lanza notificación

resalta la opción ganadora en la lista de opciones actuales

4.3 Historial
lee getResultados() (Flow) para mostrar todas las tiradas
permite deleteResultado() o clearResultados() según la implementación

Navegación
las rutas declaradas en el NavHost son:
"ruleta" → PantallaRuleta
"gestion" → PantallaGestion
"historial" → PantallaHistorial

en PantallaRuleta hay botones para navegar a “gestion” y “historial”
en PantallaGestion y PantallaHistorial se puede volver a “ruleta” usando navController.navigate("ruleta") o popBackStack()

Integraciones nativas
6.1 Haptic feedback (sin permisos)
se usa LocalHapticFeedback.current + performHapticFeedback(HapticFeedbackType.LongPress)
se ejecuta solo al terminar el giro, después de que la animación con Animatable termina

6.2 Notificaciones
en AndroidManifest.xml se declara (Android 13+):
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

en tiempo de ejecución se crea el canal de notificación (API 26+)
se usa NotificationCompat para mostrar:

título: “RuletAPP”

texto: “Salió: {resultado}”

Dependencias de Gradle
dependencies {
// Compose BOM / Material 3
implementation(platform("androidx.compose:compose-bom:<versión>"))
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
debugImplementation("androidx.compose.ui:ui-tooling")

// Navigation Compose
implementation("androidx.navigation:navigation-compose:<versión>")

// Lifecycle (collectAsStateWithLifecycle si lo usas)
implementation("androidx.lifecycle:lifecycle-runtime-compose:<versión>")

// Room
implementation("androidx.room:room-runtime:<versión>")
kapt("androidx.room:room-compiler:<versión>")
implementation("androidx.room:room-ktx:<versión>")

// Core KTX + AppCompat (para soporte de notificaciones)
implementation("androidx.core:core-ktx:<versión>")
implementation("androidx.appcompat:appcompat:<versión>")

// Notificaciones (si la versión de core no lo trae en el BOM)
implementation("androidx.core:core-splashscreen:<versión>") // opcional
implementation("androidx.core:core:<versión>")
}

Consideraciones de permisos
POST_NOTIFICATIONS es requerido en Android 13+ para mostrar notificaciones
el haptic por Compose no requiere permisos
si en el futuro se usa el Vibrator del sistema, se debe agregar:

<uses-permission android:name="android.permission.VIBRATE" />

y manejar versiones para no usar APIs deprecadas

Buenas prácticas y manejo de errores
crear el canal de notificación antes de intentar mostrar la notificación (API 26+)
todas las operaciones de escritura en Room deben ser suspend y llamarse dentro de scope.launch{} para no bloquear el hilo de UI
en LaunchedEffect(spinId) se debe comprobar que la lista de opciones no esté vacía
el estado que muestra la opción ganadora debe actualizarse después de animateTo(...) para que la UI encienda la opción solo cuando el giro terminó
evitar usar TODO("Not yet implemented") en funciones que se llamen desde la UI porque provocan un crash inmediato

Ejecución y pruebas manuales
compilar y ejecutar en emulador o dispositivo
en la pantalla Gestionar agregar 2 o 3 opciones (por ejemplo “Pizza”, “Taco”, “Sushi”)
volver a la pantalla Ruleta y girar varias veces: la ruleta debe animar, encender la opción ganadora, guardar el resultado, hacer haptic y mostrar la notificación
abrir la pantalla Historial y verificar que aparezcan las tiradas recién hechas
probar eliminar registros del historial (si está implementado)
cerrar y abrir la app: las opciones agregadas y el historial deben seguir ahí porque están en Room

Extensiones futuras
agregar timestamp en ResultadoItem y mostrar fecha y hora en el historial
agregar sonido al terminar el giro usando MediaPlayer o SoundPool con archivos en res/raw
agregar botón para compartir el resultado usando un intent implícito
generar exportación de historial a CSV
permitir cambiar colores/tema de la ruleta
agregar efectos visuales en el puntero cuando hay ganador
agregar tests de UI (Compose) y tests de base de datos para el DAO

Git 
revisar cambios en Android Studio: VCS > Commit
usar un mensaje como: feat: ruleta con notificaciones e historial
luego hacer Commit and Push (o primero Commit y después Push) para subirlo al remoto
