# Blok Android

Cliente Kotlin + Jetpack Compose para **Blok**. Consume `blok-api` con JWT (mismo contrato que `blok-web`).

## Stack

- Kotlin, Jetpack Compose, Material 3
- Retrofit + OkHttp + Gson
- DataStore (token)
- Navigation Compose + ViewModel

## Requisitos

- Android Studio (o SDK local)
- Emulador o dispositivo
- `blok-api` corriendo en el host (`docker compose up` en `blok-api`)

## Base URL

Por defecto (emulador):

```
http://10.0.2.2:8000/
```

Definida en `app/build.gradle.kts` como `BuildConfig.API_BASE_URL`.

Para un dispositivo físico, cambia esa URL a la IP LAN de tu PC (ej. `http://192.168.1.20:8000/`).

## Arranque

1. Abre esta carpeta en Android Studio
2. Sync Gradle
3. Run en emulador

Desde terminal (JDK 17):

```bash
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

## Estructura

```
app/src/main/java/com/adrian/blok/
├── core/
│   ├── network/     # Retrofit client + JWT interceptor
│   └── auth/        # API, TokenStore, ViewModel, pantallas login/register
├── modules/notas/   # data, viewmodel, ui
└── MainActivity.kt
```

## Flujo MVP

Login/registro → JWT en DataStore → listado CRUD de notas contra `/notas`.
