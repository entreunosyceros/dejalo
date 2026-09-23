# Déjalo!
<p align="center">

<img width="1408" height="768" alt="dejalo" src="https://github.com/user-attachments/assets/cc5ae997-ea5c-4098-902c-209b26247749" />

</p>

Esta es una pequeña App Android **offline-first** para acompañar la deshabituación tabáquica: métricas en tiempo real, soporte ante *cravings*, minijuegos de distracción, recuperación biológica, logros y registro de recaídas.

---

## Visión

Ayudar a personas que dejan de fumar con respuesta inmediata **sin internet**: contadores en vivo, herramientas ante el ansia (respiración, tarjetas, minijuegos) y seguimiento del progreso sin castigar las recaídas.

Requisitos: **Android 8.0+ (API 26)**.

---

## Privacidad (identidad del producto)

<p align="center">

<img width="402" height="875" alt="documentacion" src="https://github.com/user-attachments/assets/06e9b748-2960-498b-b52a-5efabd00ff49" />

</p>

Déjalo! está pensada para hábitos personales **sin cuenta y sin servidor**. Los registros (ansia, recaídas, rutinas, configuración) viven en Room en el dispositivo.

- Sin analytics obligatorios  
- Sin publicidad  
- Sin sincronización externa  
- Sin envío de episodios de ansia/recaídas a un servidor  

Los únicos accesos de red opcionales son los que inicia el usuario (abrir GitHub o la documentación). Cualquier telemetría futura, si existiera, sería opt-in y estaría claramente separada.

En la app: **Acerca de → Privacidad**.

<p align="center">

<img width="399" height="870" alt="about-dejalo" src="https://github.com/user-attachments/assets/055802ed-1729-4b96-91e6-94884cc2584f" />

</p>

---

## Stack

| Tecnología | Uso |
|------------|-----|
| Kotlin | Lenguaje |
| Jetpack Compose + Material 3 | UI |
| Room (SQLite) | Persistencia local 100 % offline |
| Kotlin Flows | Métricas en tiempo real (tick 1 s) |
| WorkManager | Notificaciones de refuerzo / hitos |
| Glance | Widget de pantalla de inicio |
| Navigation Compose | Navegación entre pantallas |

---

## Funcionalidades

### Onboarding y configuración

- Splash de arranque con el logo centrado (2 s).

<p align="center">

<img width="402" height="876" alt="configuracion" src="https://github.com/user-attachments/assets/20b3f2af-6cbb-4c38-8b5d-90e964987b7c" />

</p>

- Fecha y hora del **último cigarrillo** con selector real (`DatePicker` + `TimePicker` Material 3).
- Atajos rápidos: Ahora, −1 h, −1 día.
- Validación: no se permiten fechas/horas futuras.
- Cigarrillos al día, precio del paquete (€) y cigarrillos por paquete.
- Motivadores (chips + texto personalizado).
- Meta de ahorro opcional (qué quieres conseguir + precio objetivo).
- Todo se guarda en el dispositivo (sin cuenta ni nube).

### Dashboard (inicio)

<p align="center">

<img width="403" height="883" alt="pantalla-principal" src="https://github.com/user-attachments/assets/36e964ae-2abf-4124-92a5-4cf11b9bd1c8" />

</p>

- Marca **Déjalo!** con logo; al pulsar el logo/nombre se abre el repo en el navegador.
- **Racha actual** en vivo (días/horas/minutos/segundos desde la última recaída o el abandono).
- Separado del progreso: **mejor racha**, **progreso acumulado** (días desde el abandono, con interrupciones si las hubo) y **cigarrillos evitados** — una recaída no pone todo a cero.
- Ahorro económico y estimación de vida recuperada.
- Barra de progreso hacia la meta de ahorro (si está definida).
- Accesos:
  - **Modo emergencia**
  - **No voy a fumar ahora** (compromiso de 5 minutos + check de intensidad)
  - **Minijuegos — distraer el ansia**
  - **Historial de ansia** (episodios con intensidad, herramientas, ranking de desencadenantes y franja horaria)
  - **Rutinas alternativas** (sustituir «situación → cigarrillo» por un ritual paso a paso)
  - **Zonas de riesgo** (franjas horarias detectadas + aviso en dashboard y notificaciones preventivas)
  - Salud, Logros, Registrar recaída
  - **Aprendizajes de tu proceso** (patrones de ansia, franjas y recaídas)
  - **Configuración** (editar consumo, precio, fecha de abandono, motivadores y meta)
  - **Documentación técnica** (especificaciones embebidas + enlace a GitHub)
  - **Acerca de** (logo, descripción, **privacidad** como identidad, enlace al repo)

### Modo emergencia (ansia / ganas de fumar)

<p align="center">

<img width="402" height="875" alt="modo-emergencia" src="https://github.com/user-attachments/assets/6e659c0e-f8cb-410d-adc3-709e331a75e5" />

</p>

Pensado para picos de 3–5 minutos:

- Escala de **intensidad 0–10** al entrar y otra al terminar (antes/después).
- Temporizador de crisis (~4 min).
- Respiración guiada **4-7-8** (inhala 4 s, mantén 7 s, exhala 8 s) con animación visual.
- Tarjetas de distracción (motivadores personales + frases de refuerzo).
- Minijuegos integrados (mismos que en la pantalla dedicada).
- Registro del desencadenante: estrés, café, alcohol, entorno social, aburrimiento, otro.
- Guardado del episodio (duración, herramientas usadas, intensidad inicial/final).
- En el dashboard: insight de media de bajada, desencadenante de la semana y franja horaria.
- **Historial de ansia:** lista de episodios (#N) con detalle (intensidad, duración, herramientas) y ranking de desencadenantes.

### Minijuegos (distraer el ansia)

<p align="center">

<img width="397" height="879" alt="minijuegos" src="https://github.com/user-attachments/assets/1bee7d98-747e-42fe-b55e-c40952025860" />

</p>

Pantalla propia y también disponibles en emergencia, agrupados por mecanismo:

| Mecanismo | Juegos |
|-----------|--------|
| **Manos** | Rompe el impulso, Reacción rápida, Traza el camino |
| **Mente** | Memoria libre, Orden numérico |
| **Esperar** | Aguanta la ola, Temporizador progresivo |
| **Relajación** | Ritmo suave, Sigue el punto |

Sin castigo: fallar o soltar antes es normal; se puede repetir.

### No voy a fumar ahora

<p align="center">

<img width="402" height="572" alt="ahora-no" src="https://github.com/user-attachments/assets/965b1bab-469e-4f42-bf7a-5da7a8eb189e" />

</p>

- Decisión pequeña: solo «ahora no» (no «nunca más»).
- Temporizador de 5 minutos + intensidad antes/después.
- Se guarda como episodio de ansia en el historial.

### Salud y recuperación biológica

<p align="center">

<img width="401" height="874" alt="salud" src="https://github.com/user-attachments/assets/105bf3ac-3779-4390-b84f-d992b390310b" />

</p>

- Sección **¿Qué está pasando en tu cuerpo ahora?** (hito actual + progreso).
- Hitos fisiológicos con explicación, barra de progreso y fuente orientativa.
- Estimaciones poblacionales (p. ej. riesgo coronario a 1 año) marcadas aparte.
- En dashboard y salud: **tiempo estimado de vida no expuesto** con disclaimer (referencia poblacional, no predicción individual).
- Aviso: no sustituye consejo médico.

### Logros (medallas)

Desbloqueo automático, entre otros:

- 1 día limpio  
- 7 días de racha  
- 100 € ahorrados  
- 500 cigarrillos evitados  
- 30 días limpios  

### Recaídas

- Registrar cigarrillos y causa (**¿qué ocurrió?**) sin borrar el historial de esfuerzo.
- Pregunta **¿qué podrías probar la próxima vez?** para convertir la caída en aprendizaje.
- Tras guardar: “No has vuelto a cero.” — la racha se reinicia; siguen contando mejor racha, progreso acumulado, cigarrillos evitados, ahorro y vida recuperada.
- Ranking de causas en el historial.
- Enfoque no punitivo (“guardar sin castigo”).

### Aprendizajes

<p align="center">

<img width="401" height="677" alt="aprendizajes" src="https://github.com/user-attachments/assets/11e81977-6d41-499a-856c-4b3957693f81" />

</p>

- Pantalla central de patrones: desencadenantes, franjas, zonas de riesgo y causas de recaída.
- **¿Qué me funciona a mí?** ranking de herramientas por bajada media de intensidad (0–10) y comparación por desencadenante (estadística descriptiva de tus datos, sin IA).
- **Ansia → recaída:** patrones entre desencadenantes de episodios y causas de recaída, con acceso a **crear rutina alternativa** para ese contexto.
- Contador de episodios superados con modo emergencia.

### Widget de inicio

Dos widgets en el selector del sistema (mantener pulsado el escritorio → Widgets → Déjalo!):

| Widget | Contenido |
|--------|-----------|
| **Déjalo! — resumen** | Dinero ahorrado, racha vs acumulado y acceso a modo emergencia |
| **Déjalo! — ahorrado** | Solo el dinero ahorrado (compacto) |

Se actualizan al cambiar configuración/recaídas y periódicamente (~30 min).

### Notificaciones locales

- Canales: hitos, refuerzo y **zonas de riesgo**.
- WorkManager: refuerzo genérico (~6 h) y riesgo personalizado (~1 h) según tu historial de ansia.
- Avisos de hitos (1, 3, 7, 14, 30 días).
- Aviso preventivo con acción «Abrir modo emergencia» al acercarse a una franja detectada.
- Reprogramación tras reinicio del dispositivo (`BOOT_COMPLETED`).

---

## Fórmulas del motor core

```
Ahorro (€) = (cig/día ÷ cig/paquete × precio paquete) × días transcurridos
Cigarrillos evitados = cig/día × días transcurridos
```

Las recaídas restan cigarrillos (y su coste proporcional) del acumulado **sin** eliminar el historial.

Vida / tiempo no expuesto ≈ cigarrillos evitados × 11 minutos (estimación poblacional; no predicción individual).

---

## Persistencia (Room)

| Tabla | Contenido |
|-------|-----------|
| `user_profile` | Fecha de abandono, consumo, precio, motivadores, meta |
| `craving_events` | Crisis / ansia (desencadenante, duración, intensidad, herramientas) |
| `alternative_routines` | Rituales por situación (patrón antiguo + pasos del nuevo) |
| `relapse_events` | Recaídas (cigarrillos, causa, notas, plan próxima vez) |
| `badges` | Medallas desbloqueadas |

Base de datos local: `dejalo.db`.

---

## Estructura del código

```
app/src/main/java/com/dejalo/app/
  data/            Room, DAOs, QuitRepository
  domain/          Cálculos, hitos de salud, catálogo de badges
  ui/
    onboarding/    Configuración inicial + Date/Time picker
    dashboard/     Pantalla principal
    emergency/     Crisis, respiración, tarjetas
    emergency/games/  Minijuegos (burbujas, memoria, ola)
    games/         Hub dedicado de minijuegos
    health/        Recuperación biológica
    achievements/  Medallas
    relapse/       Recaídas
    components/    Marca, botones, fondos
    theme/         Color, tipografía, tema
  widget/          Glance
  notifications/   WorkManager + canales
```

Especificaciones de origen: `docs/especificaciones_tecnicas.md`.

---

## Abrir y ejecutar

```bash
git clone https://github.com/entreunosyceros/dejalo.git
cd dejalo
```

1. Instala [Android Studio](https://developer.android.com/studio) (Ladybug o superior).
2. Abre esta carpeta como proyecto Gradle (Android Studio creará `local.properties` con tu SDK; no se sube a Git).
3. Sincroniza dependencias (el wrapper `./gradlew` ya está incluido).
4. Ejecuta en emulador o dispositivo (**API 26+**).

APK debug (tras compilar):

```bash
./gradlew :app:assembleDebug
# → app/build/outputs/apk/debug/Déjalo!-debug.apk

./gradlew :app:assembleRelease
# → app/build/outputs/apk/release/Déjalo!-release.apk
```

Tests unitarios del motor de cálculo:

```bash
./gradlew :app:testDebugUnitTest
```

---

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
