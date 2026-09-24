# Funcionalidades

## Onboarding y configuración

- Splash de arranque con el logo centrado (2 s).
- Pedido amable de **notificaciones locales** (hitos / zonas de riesgo) en el onboarding; reintento en Ajustes.

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
- **Copia de seguridad** (Ajustes): exportar / importar JSON con el Storage Access Framework.

## Navegación (barra inferior)

| Pestaña | Contenido |
|---------|-----------|
| **Inicio** | Racha, progreso, CTAs emergencia / recaída / «ahora no» |
| **Progreso** | Salud, logros, aprendizajes, historial de ansia |
| **Herramientas** | Emergencia, minijuegos, rutinas, zonas de riesgo |
| **Ajustes** | Configuración, backup, docs, acerca de, notificaciones |

## Dashboard (inicio)

<p align="center">
<img width="403" height="883" alt="pantalla-principal" src="https://github.com/user-attachments/assets/36e964ae-2abf-4124-92a5-4cf11b9bd1c8" />
</p>

- Marca **Déjalo!** con logo; al pulsar el logo/nombre se abre el repo en el navegador.
- **Racha actual** en vivo (desde la última recaída o el abandono).
- **Mejor racha**, **progreso acumulado** (con interrupciones si las hubo) y **cigarrillos evitados** — una recaída no pone todo a cero.
- Ahorro económico y estimación de vida recuperada.
- Barra de progreso hacia la meta de ahorro (si está definida).
- Acciones prioritarias: **Modo emergencia**, **Registrar recaída**, enlace a **No voy a fumar ahora**.

## Modo emergencia

<p align="center">
<img width="402" height="875" alt="modo-emergencia" src="https://github.com/user-attachments/assets/6e659c0e-f8cb-410d-adc3-709e331a75e5" />
</p>

Pensado para picos de 3–5 minutos:

- Intensidad **0–10** al entrar y al terminar.
- Temporizador de crisis (~4 min).
- Respiración guiada **4-7-8**.
- Tarjetas de distracción y minijuegos.
- Registro de desencadenante y herramientas usadas.
- Historial de ansia con ranking de desencadenantes.

## Minijuegos

<p align="center">
<img width="397" height="879" alt="minijuegos" src="https://github.com/user-attachments/assets/1bee7d98-747e-42fe-b55e-c40952025860" />
</p>

| Mecanismo | Juegos |
|-----------|--------|
| **Manos** | Rompe el impulso, Reacción rápida, Traza el camino |
| **Mente** | Memoria libre, Orden numérico |
| **Esperar** | Aguanta la ola, Temporizador progresivo |
| **Relajación** | Ritmo suave, Sigue el punto |

Sin castigo: fallar o soltar antes es normal; se puede repetir.

## No voy a fumar ahora

<p align="center">
<img width="402" height="572" alt="ahora-no" src="https://github.com/user-attachments/assets/965b1bab-469e-4f42-bf7a-5da7a8eb189e" />
</p>

- Solo «ahora no» (no «nunca más»).
- Temporizador de 5 minutos + intensidad; se guarda en el historial de ansia.

## Salud y recuperación biológica

<p align="center">
<img width="401" height="874" alt="salud" src="https://github.com/user-attachments/assets/105bf3ac-3779-4390-b84f-d992b390310b" />
</p>

- Hitos fisiológicos con explicación y progreso.
- Estimaciones poblacionales marcadas aparte.
- **Tiempo estimado de vida no expuesto** con disclaimer.
- No sustituye consejo médico.

## Logros

- 1 día limpio · 7 días de racha · 100 € ahorrados · 500 cigarrillos evitados · 30 días limpios

## Recaídas

- Registro sin borrar el historial.
- «No has vuelto a cero»: se reinicia la racha; se conserva el progreso acumulado.
- Enfoque no punitivo («guardar sin castigo»).

## Aprendizajes

<p align="center">
<img width="401" height="677" alt="aprendizajes" src="https://github.com/user-attachments/assets/11e81977-6d41-499a-856c-4b3957693f81" />
</p>

- Patrones de desencadenantes, franjas y causas.
- **¿Qué me funciona a mí?** ranking de herramientas por tus datos.
- **Ansia → recaída** + crear rutina alternativa.

## Widgets

| Widget | Contenido |
|--------|-----------|
| **Déjalo! — resumen** | Ahorro, racha vs acumulado, modo emergencia |
| **Déjalo! — ahorrado** | Solo el dinero ahorrado |

RemoteViews; refresco en eventos clave y ~45 min (WorkManager).

## Notificaciones locales

- Canales: hitos, refuerzo y zonas de riesgo.
- WorkManager + reprogramación tras `BOOT_COMPLETED`.
- Permiso `POST_NOTIFICATIONS` (Android 13+): onboarding / Ajustes, no al abrir en frío.
