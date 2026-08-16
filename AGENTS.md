# AGENTS.md — blok-android

> Lee primero `../AGENTS.md` (el harness compartido en `blok-app`) para las
> reglas de proyecto: convenciones de commits, una feature a la vez, dónde
> viven los specs. Este archivo cubre solo lo específico de este repo.

## Reglas específicas de Kotlin / Jetpack Compose

- Usa Jetpack Compose para toda UI nueva — sin Views/XML layouts.
- Estado de UI vive en `ViewModel`, no en el Composable — flujo unidireccional de datos.
- Cliente HTTP: Retrofit + OkHttp (`logging-interceptor` ya incluido). `API_BASE_URL` viene de `buildConfigField`, no hardcodeado en el código.
- `DataStore` (no `SharedPreferences`) para persistencia local simple (ej. token de sesión).
- Este proyecto está en etapa inicial (scaffold): antes de construir una pantalla nueva, confirma que el módulo correspondiente ya existe y está estable en `blok-api` (ver `../ROADMAP.md`).

## Si te bloqueas

Documenta el bloqueo en `../progress/current.md` (harness compartido) y para la sesión.
