# CHECKPOINTS.md — blok-android

Verificaciones antes de marcar cualquier trabajo en este repo como terminado.

## Después de implementar

- [ ] `./gradlew build` pasa sin errores
- [ ] Sin `Log.d`/`println` de debug dejados en el código

## Antes de marcar como terminado

- [ ] `API_BASE_URL` u otras configs sensibles no quedaron hardcodeadas fuera de `buildConfigField`
- [ ] Commit en rama propia, Conventional Commits, no directo a `master`

## Huecos conocidos (no fingir que existen)

- No hay ktlint/detekt configurado — solo el build de Gradle.
- No hay tests instrumentados ni unitarios más allá de la plantilla default. Hasta que se agreguen, "verificado" significa: build limpio + revisión manual en emulador/dispositivo.
