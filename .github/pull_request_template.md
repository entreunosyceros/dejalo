## Resumen

<!-- Qué cambia este PR y por qué (1-3 frases). -->

## Tipo de cambio

- [ ] Corrección de error
- [ ] Nueva funcionalidad
- [ ] Mejora de documentación
- [ ] Refactor / mantenimiento
- [ ] Interfaz / UX

## Área afectada

- [ ] Dashboard / navegación (bottom bar)
- [ ] Modo emergencia / ansia / minijuegos
- [ ] Recaídas / aprendizajes / rutinas
- [ ] Salud / logros
- [ ] Widgets
- [ ] Notificaciones / WorkManager
- [ ] Room / backup / migraciones
- [ ] Onboarding / ajustes / privacidad
- [ ] Documentación
- [ ] Otro: <!-- especificar -->

## Cómo se ha probado

<!-- Pasos manuales o comandos. Sin adjuntar dejalo.db ni backups reales. -->

- [ ] He compilado la app (`./gradlew :app:assembleDebug`)
- [ ] He ejecutado `./gradlew :app:testDebugUnitTest` si el cambio toca dominio, backup o métricas
- [ ] He probado en emulador o dispositivo (API 26+)

## Checklist

- [ ] El diff está acotado al objetivo del PR
- [ ] Los textos de usuario están en español
- [ ] He actualizado `docs/` o el README si el cambio lo requiere
- [ ] No incluyo `local.properties`, keystores, APKs con secretos ni datos personales de usuario
- [ ] Si toco Room: migración + schema actualizados (sin destructive fallback innecesario)

## Issues relacionadas

<!-- Closes #123 / Relates to #456 -->
