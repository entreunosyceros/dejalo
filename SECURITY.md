# Política de seguridad

## Versiones con soporte

| Versión | Soportada |
| ------- | --------- |
| 1.0.x   | ✅        |
| < 1.0   | ❌        |

## Alcance

**Déjalo!** es una aplicación **Android offline-first** (Kotlin, Compose, Room). Los datos de cesación viven en el dispositivo (`dejalo.db`). En seguridad nos interesa especialmente:

- **Privacidad de datos locales**: perfil, episodios de ansia, recaídas, rutinas, backups JSON.
- **Exportación / importación** (SAF): integridad del archivo, sustitución accidental o maliciosa de datos.
- **Almacenamiento**: `dejalo.db`, preferencias, archivos exportados por el usuario.
- **Widgets y notificaciones**: `PendingIntent`, canales, permiso `POST_NOTIFICATIONS`.
- **Dependencias**: vulnerabilidades en bibliotecas AndroidX / Room / WorkManager del `build.gradle.kts`.
- **Firma y distribución**: keystores, APKs de terceros no oficiales.

**Fuera de alcance habitual:**

- Consejos médicos o eficacia clínica de la cesación.
- Disponibilidad de emuladores, OEM o la Play Store.
- Pérdida de datos por desinstalación o borrado manual sin backup (comportamiento esperado offline).

## Cómo reportar una vulnerabilidad

1. **No** abras un issue público con detalles del fallo ni adjuntos con bases de datos o backups reales.
2. Usa [GitHub Security Advisories](https://github.com/entreunosyceros/dejalo/security/advisories/new) (**Report a vulnerability**) si tienes acceso.
3. Si no puedes usar Advisories, abre un issue con título `SECURITY (sin detalles)` y pide un canal privado; no incluyas pasos de explotación en público.

Incluye, en la medida de lo posible:

- Descripción del problema y componente (`BackupCodec`, Room, widgets, notificaciones, etc.).
- Pasos para reproducirlo (sin datos personales reales).
- Impacto estimado (lectura/escritura de datos locales, ejecución, escalada).
- Versión (`versionName` / `versionCode`, p. ej. 1.0.0) o commit afectado.
- Modelo de Android / API level.
- Sugerencia de mitigación, si la tienes.

## Qué esperar

- **Acuse de recibo** en un plazo razonable (habitualmente en pocos días).
- Evaluación del informe y, si procede, parche en una versión posterior.
- Crédito al informante en las notas de la corrección, salvo que prefiera anonimato.

## Buenas prácticas para usuarios

- No subas `dejalo.db`, backups JSON ni capturas con datos personales a issues o PRs.
- Usa solo APKs o código del repositorio oficial: [github.com/entreunosyceros/dejalo](https://github.com/entreunosyceros/dejalo).
- Haz copias de seguridad periódicas (Ajustes → Copia de seguridad) antes de cambiar de teléfono.
- Protege el dispositivo con bloqueo de pantalla; cualquiera con acceso físico puede leer datos locales de una app sin cuenta.
