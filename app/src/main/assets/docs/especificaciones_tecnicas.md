# Propuesta de proyecto: aplicación Android para dejar de fumar

## 1. Visión general del proyecto
El objetivo principal de esta aplicación Android es proporcionar asistencia conductual, seguimiento cuantitativo en tiempo real y soporte ante picos de ansiedad (*ansia / ganas de fumar*) para usuarios en proceso de deshabituación tabáquica.

La arquitectura priorizará el diseño **offline-first**, garantizando respuesta inmediata en situaciones de crisis sin depender de conectividad a internet.

---

## 2. Características clave y módulos de la aplicación

### A. Onboarding y configuración inicial
Pantalla de captura de métricas base del usuario para la personalización de algoritmos:
* **Fecha y hora de inicio:** marca de tiempo exacta del último cigarrillo consumido o fecha objetivo.
* **Métricas de consumo:** cantidad habitual de cigarrillos fumados al día.
* **Métricas económicas:** precio del paquete o del tabaco de liar y número de cigarrillos por paquete.
* **Motivadores personales:** selección o entrada de texto con las razones principales para dejar de fumar (salud, ahorro, familia, etc.).

### B. Dashboard principal (métricas e impacto)
Interfaz central con actualización en tiempo real mediante *Flows* / *Observables*:
* **Tiempo acumulado:** contador dinámico en tiempo real (días, horas, minutos y segundos sin fumar).
* **Ahorro económico:** cálculo en tiempo real del dinero no gastado.
* **Cigarrillos evitados:** indicador numérico directo del impacto acumulado.
* **Tiempo de vida recuperado:** estimación cuantitativa basada en promedios estadísticos de salud.

### C. Módulo de salud y recuperación biológica
Barra de progreso e hitos con respaldo en literatura médica sobre la recuperación del organismo:
* **20 minutos:** normalización de la presión arterial y ritmo cardíaco.
* **8 horas:** reducción a la mitad de los niveles de nicotina y monóxido de carbono en sangre.
* **24 horas:** eliminación completa del monóxido de carbono del cuerpo.
* **48 horas:** regeneración de las terminaciones nerviosas; mejora del gusto y el olfato.
* **72 horas:** relajación de los bronquios y aumento de la capacidad pulmonar.
* **2 a 12 semanas:** mejora significativa de la circulación sanguínea.
* **1 a 9 meses:** reducción de la tos, congestión y dificultad respiratoria.
* **1 año:** reducción al 50% del riesgo de enfermedad coronaria en comparación con un fumador.

### D. Módulo de emergencia / gestión del ansia
Herramienta de acción rápida activable ante la tentación inminente de fumar:
* **Temporizador de crisis:** contador regresivo de 3 a 5 minutos (duración promedio de un pico agudo de ansia).
* **Respiración guiada:** componente visual e interactivo con técnica de respiración **4-7-8** (inhala 4s, mantén 7s, exhala 8s) o *box breathing*.
* **Tarjetas de distracción:** muestra aleatoria de motivos personales registrados, frases motivacionales o minijuegos simples dentro de la app.
* **Registro de episodio de ansia:** captura del desencadenante (estrés, café, alcohol, entorno social) e intensidad 0–10 antes/después para análisis de patrones.

### E. Gamificación, logros y recompensas
* **Sistema de medallas (*badges*):** desbloqueo automático al alcanzar hitos (1 día limpio, 100 € ahorrados, 1 semana sin caer, 500 cigarrillos evitados).
* **Meta de ahorro personalizada:** definición de un objetivo material (viaje, gadget, cena) financiado con el dinero ahorrado, con barra de progreso asociativa.

### F. Registro de recaídas
* Permite registrar un cigarrillo puntual o una caída sin reiniciar bruscamente la experiencia de usuario.
* Análisis de causas y reajuste del contador sin penalización punitiva ni borrado de métricas históricas de esfuerzo.

---

## 3. Integración nativa y funcionalidades Android

1. **Widget para pantalla de inicio (home screen widget):**
   * Vista previa rápida sin necesidad de abrir la app: días limpios, dinero ahorrado y acceso directo mediante un toque al **modo emergencia**.
2. **Notificaciones locales programadas (`WorkManager` / `AlarmManager`):**
   * Avisos de hitos alcanzados (ej. "¡Has cumplido 7 días limpio!").
   * Notificaciones contextuales de refuerzo en franjas horarias de alto riesgo (ej. sobremesa o pausas de trabajo).
3. **Persistencia local (`Room` / `DataStore`):**
   * Almacenamiento local SQLite seguro e instantáneo para garantizar el funcionamiento **100% offline**.

---

## 4. Fórmulas de cálculo del motor core

### A. Ahorro económico
Ahorro (€) = (cigarrillos al día ÷ cigarrillos por paquete × precio del paquete) × días transcurridos

### B. Cigarrillos evitados
Cigarrillos evitados = cigarrillos al día × días transcurridos

---

## 5. Roadmap de desarrollo (MVP)

### Fase 1 — Core y base de datos
* Esquema Room
* Fórmulas de cálculo en Kotlin

### Fase 2 — Interfaz (dashboard)
* Layout en Jetpack Compose
* Flows en tiempo real

### Fase 3 — Modo crisis
* Temporizador y respiración 4-7-8
* Registro de episodios de ansia

### Fase 4 — Extensión Android
* Widget de pantalla de inicio
* Notificaciones locales

Detalle por fase:

* **Fase 1 (MVP base):** implementación de la base de datos local y motor de cálculos en Kotlin.
* **Fase 2 (Interfaz):** desarrollo del onboarding y dashboard en **Jetpack Compose**.
* **Fase 3 (Soporte de ansiedad):** creación del módulo de respiración guiada e integración de animaciones.
* **Fase 4 (Extensión Android):** construcción del widget de pantalla de inicio y notificaciones programadas.
