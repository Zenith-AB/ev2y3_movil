# RuletAPP 🎡

Aplicación móvil Android que permite crear opciones personalizadas y girar una ruleta para elegir una al azar. Los datos se sincronizan con un backend en la nube para mantener tus opciones guardadas.

## ¿Qué hace esta app?

- **Gestiona opciones**: Agrega y elimina las opciones que quieras (por ejemplo: "Pizza", "Sushi", "Tacos")
- **Gira la ruleta**: Presiona el botón y deja que el azar decida por ti
- **Historial**: Revisa los resultados anteriores de tus tiradas
- **Sincronización en la nube**: Tus opciones se guardan en un servidor, así que no las pierdes aunque desinstales la app

## Tecnologías

### App Android
- Kotlin
- Jetpack Compose (Material 3)
- Retrofit (para conectarse al backend)
- Arquitectura MVVM

### Backend (Microservicio)
- Node.js + Express
- PostgreSQL (en producción)
- SQLite (para desarrollo local)
- Desplegado en Railway

## Cómo ejecutar localmente

### Backend
```bash
cd backend
npm install
npm start
```
El servidor estará en `http://localhost:3000`

### App Android
1. Abre el proyecto en Android Studio
2. Ejecuta la app en un emulador o dispositivo físico
3. La app se conectará automáticamente al backend en Railway

## Estructura del proyecto

```
ev2y3_movil-master/
├── backend/              # API REST (Node.js)
│   ├── server.js        # Servidor principal
│   ├── package.json     # Dependencias del backend
│   └── ruleta.db        # Base de datos local (SQLite)
│
└── ev2y3_movil-master/  # App Android
    └── app/
        └── src/main/java/com/duoc/materiald/
            ├── data/           # Capa de datos (API, modelos)
            ├── ui/             # Pantallas (Compose)
            └── viewmodel/      # Lógica de negocio
```

## API Endpoints

- `GET /api/opciones` - Obtener todas las opciones
- `POST /api/opciones` - Crear una nueva opción
- `DELETE /api/opciones/:id` - Eliminar una opción
- `GET /api/resultados` - Obtener historial de resultados
- `POST /api/resultados` - Guardar un resultado
- `DELETE /api/resultados/:id` - Eliminar un resultado

## Despliegue

El backend está desplegado en Railway: `https://ev2y3movil-production.up.railway.app`

---

Desarrollado como proyecto académico - DUOC UC
