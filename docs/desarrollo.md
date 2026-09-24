# Desarrollo

## Requisitos

- Android 8.0+ (API 26) en dispositivo/emulador
- Android Studio (Ladybug o superior) o JDK 17 + Android SDK

## Clonar y abrir

```bash
git clone https://github.com/entreunosyceros/dejalo.git
cd dejalo
```

1. Abre la carpeta como proyecto Gradle.
2. Android Studio creará `local.properties` (no se sube a Git).
3. Sincroniza dependencias (`./gradlew` incluido).
4. Ejecuta en emulador o dispositivo (**API 26+**).

## Compilar APK

```bash
./gradlew :app:assembleDebug
# → app/build/outputs/apk/debug/Déjalo!-debug.apk

./gradlew :app:assembleRelease
# → app/build/outputs/apk/release/Déjalo!-release.apk
```

## Tests

```bash
./gradlew :app:testDebugUnitTest
```

Migraciones Room (instrumentados, dispositivo/emulador):

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Roadmap cubierto (MVP)

| Fase | Estado |
|------|--------|
| 1. Core & Room + fórmulas | Hecho |
| 2. Onboarding + Dashboard Compose | Hecho |
| 3. Modo crisis (timer, 4-7-8, cravings) | Hecho |
| 4. Widget + notificaciones | Hecho |
| Extra: selector fecha/hora | Hecho |
| Extra: diseño de marca | Hecho |
| Extra: minijuegos de distracción | Hecho |
| Extra: bottom nav, backup, RemoteViews | Hecho |
