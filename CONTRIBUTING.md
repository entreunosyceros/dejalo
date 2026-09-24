# Guía de contribución

¡Gracias por interesarte en **[Déjalo!](https://github.com/entreunosyceros/dejalo)**! Es una app Android **offline-first** (Kotlin, Jetpack Compose, Room) para acompañar la deshabituación tabáquica, publicada bajo [GPL-3.0](LICENSE). Cualquier mejora bien planteada es bienvenida.

## Antes de empezar

- Lee el [README](README.md) y la [documentación](docs/) (`privacidad`, `funcionalidades`, `arquitectura`, `desarrollo`).
- Revisa las [issues abiertas](https://github.com/entreunosyceros/dejalo/issues) por si alguien ya trabaja en lo mismo.
- Para el comportamiento en la comunidad: [Código de conducta](CODE_OF_CONDUCT.md).
- Para vulnerabilidades: [SECURITY.md](SECURITY.md) (no abras issues públicas con detalles de explotación).

## Cómo puedes ayudar

- **Reportar errores** con pasos claros (versión de la app, Android, dispositivo/emulador).
- **Proponer mejoras** explicando el problema que resuelven (sobre todo UX en crisis/ansia y privacidad).
- **Enviar pull requests** con cambios acotados y probados.
- **Mejorar documentación** (`README.md`, `docs/`).
- **Añadir o ajustar tests** (`app/src/test`, migraciones Room en `androidTest`).

## Entorno de desarrollo

Requisitos: **JDK 17**, Android SDK (API 26+), Android Studio recomendado.

```bash
git clone https://github.com/entreunosyceros/dejalo.git
cd dejalo
./gradlew :app:assembleDebug
```

Detalle: [docs/desarrollo.md](docs/desarrollo.md).

### Pruebas

```bash
./gradlew :app:testDebugUnitTest
```

Migraciones Room (dispositivo o emulador):

```bash
./gradlew :app:connectedDebugAndroidTest
```

### APK

```bash
./gradlew :app:assembleDebug    # Déjalo!-debug.apk
./gradlew :app:assembleRelease  # Déjalo!-release.apk (firma local de debug por defecto)
```

No subas `local.properties`, `*.jks`, `keystore.properties` ni APKs firmados con un keystore de producción.

## Estructura del código (resumen)

| Ruta | Contenido |
|------|-----------|
| `app/src/main/java/com/dejalo/app/data/` | Room, DAOs, repositorio, backup JSON |
| `app/src/main/java/com/dejalo/app/domain/` | Métricas, salud, badges, análisis |
| `app/src/main/java/com/dejalo/app/ui/` | Compose (dashboard, emergencia, hubs, …) |
| `app/src/main/java/com/dejalo/app/widget/` | Widgets RemoteViews |
| `app/src/main/java/com/dejalo/app/notifications/` | WorkManager y canales |
| `docs/` | Documentación del producto |
| `app/schemas/` | Esquema Room exportado |

Más detalle en [docs/arquitectura.md](docs/arquitectura.md).

## Estilo de código

- Sigue el estilo del código existente (Kotlin, Compose, nombres en el dominio).
- Cambios **mínimos y enfocados**: un objetivo por PR.
- Los textos visibles para el usuario van en **español**.
- Respeta la identidad de **privacidad**: sin analytics obligatorios, sin servidores de datos de usuario.
- No incluyas secretos, `local.properties`, keystores ni archivos de backup con datos reales de usuarios.
- Si tocas Room: añade `Migration` y actualiza el schema; no reintroduzcas `fallbackToDestructiveMigration` a la ligera.

## Pull requests

1. Crea una rama descriptiva desde `main` (por ejemplo `fix/widget-update` o `feat/backup-ui`).
2. Describe **qué** cambias y **por qué**.
3. Indica cómo lo has probado (`assembleDebug`, tests, dispositivo).
4. Actualiza `docs/` o el README solo si el cambio lo requiere.

Usa la [plantilla de pull request](.github/pull_request_template.md).

## Reportar problemas de seguridad

No abras issues públicas para vulnerabilidades. Sigue la [política de seguridad](SECURITY.md).

## Licencia

Al contribuir, aceptas que tu aportación se publique bajo la misma licencia del proyecto: [GPL-3.0](LICENSE).
