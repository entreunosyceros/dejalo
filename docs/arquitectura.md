# Arquitectura y datos

## Stack

| Tecnología | Uso |
|------------|-----|
| Kotlin | Lenguaje |
| Jetpack Compose + Material 3 | UI |
| Room (SQLite) | Persistencia local 100 % offline |
| Kotlin Flows | Métricas en tiempo real (tick 1 s) |
| WorkManager | Notificaciones / refresco de widgets |
| RemoteViews | Widgets de pantalla de inicio |
| Navigation Compose | Navegación + barra inferior (4 pestañas) |

## Fórmulas del motor core

```
Ahorro (€) = (cig/día ÷ cig/paquete × precio paquete) × días transcurridos
Cigarrillos evitados = cig/día × días transcurridos
```

Las recaídas restan cigarrillos (y su coste proporcional) del acumulado **sin** eliminar el historial.

Vida / tiempo no expuesto ≈ cigarrillos evitados × 11 minutos (estimación poblacional; no predicción individual).

## Persistencia (Room)

| Tabla | Contenido |
|-------|-----------|
| `user_profile` | Fecha de abandono, consumo, precio, motivadores, meta |
| `craving_events` | Crisis / ansia (desencadenante, duración, intensidad, herramientas) |
| `alternative_routines` | Rituales por situación |
| `relapse_events` | Recaídas (cigarrillos, causa, notas, plan) |
| `badges` | Medallas desbloqueadas |

Base: `dejalo.db` (versión 4). Esquema en `app/schemas/`. Migraciones `1→2`, `2→3`, `3→4` sin `fallbackToDestructiveMigration`. Tests: `DejaloDatabaseMigrationTest`.

Para un cambio nuevo: añade `Migration(n, n+1)`, sube `version` y regenera el schema con un build.

Copia de seguridad JSON: ver [privacidad.md](privacidad.md) y Ajustes en la app.

## Estructura del código

```
app/src/main/java/com/dejalo/app/
  data/            Room, DAOs, QuitRepository, backup/
  domain/          Cálculos, hitos de salud, badges
  ui/
    navigation/    Bottom bar + MainScaffold
    hubs/          Progreso / Herramientas / Ajustes
    onboarding/    Configuración inicial
    dashboard/     Inicio
    backup/        Export/import JSON
    emergency/     Crisis + games/
    ...
  widget/          RemoteViews + WidgetUpdateWorker
  notifications/   WorkManager + canales
```

Especificaciones de origen detalladas: [especificaciones_tecnicas.md](especificaciones_tecnicas.md).
