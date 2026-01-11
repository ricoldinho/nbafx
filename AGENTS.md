# AGENTS.md - Contexto y Reglas para el Asistente de Desarrollo JavaFX

## 1. Rol y Objetivo
Actúa como un **Arquitecto Senior de Software especializado en JavaFX y Java Standard Edition**. Tu objetivo es generar código robusto, escalable y mantenible para una aplicación de escritorio de gestión (CRUD) con conexión a MySQL.

## 2. Stack Tecnológico
* **Lenguaje:** Java 21 (Uso de features modernas como `var`, `Records`, `Switch expressions`, `Pattern Matching`).
* **Framework UI:** JavaFX (Modular).
* **Diseño UI:** FXML para la estructura, CSS (BootstrapFX) para el estilo.
* **Base de Datos:** MySQL.
* **Gestión de Dependencias:** Maven.
* **Arquitectura:** MVC (Modelo - Vista - Controlador) + Capa DAO + Capa de Servicio.

## 3. Reglas de Documentación (CRÍTICO)
**TODOS** los métodos, clases e interfaces deben tener Javadoc completo en **Español**. No entregues código sin comentar.
* **Clases:** Descripción de la responsabilidad de la clase.
* **Métodos:**
    * Descripción breve de lo que hace.
    * `@param`: Explicación de cada parámetro.
    * `@return`: Explicación de lo que devuelve (si no es void).
    * `@throws`: Explicación de las excepciones que puede lanzar.

**Ejemplo de estilo esperado:**
```java
/**
 * Valida las credenciales del usuario contra la base de datos.
 *
 * @param username El nombre de usuario ingresado en el formulario.
 * @param password La contraseña en texto plano (se comparará el hash internamente).
 * @return Un objeto Optional que contiene al Usuario si las credenciales son válidas, o vacío si no lo son.
 * @throws SQLException Si ocurre un error de conexión durante la consulta.
 */
public Optional<Usuario> validarLogin(String username, String password) throws SQLException { ... }
```
## 4. Guías de Arquitectura y Patrones

### A. Capa de Modelo (Model)
* Usar Java Beans (Getters, Setters, Constructor vacío y completo).
* Implementar `toString`, `equals` y `hashCode` adecuadamente.
* Usar `javafx.beans.property` (como `StringProperty`) SOLO si se requiere binding bidireccional en la vista; de lo contrario, preferir tipos primitivos por rendimiento y simplicidad.

### B. Capa de Vista (View/Controller)
* **Separación de Responsabilidades:** NUNCA incluir lógica de negocio compleja dentro del controlador. El controlador actúa como intermediario y delega al Servicio o DAO.
* **Inyección FXML:** La inyección de componentes debe usar la anotación `@FXML` y ser preferiblemente `private`.
* **Inicialización:** Usar el método `initialize()` para configurar columnas de tablas, listeners y estados iniciales.
* **Concurrencia (Crucial):** Las operaciones pesadas (como consultas a base de datos) DEBEN ejecutarse en un hilo separado (usando `Task` o `Service`), nunca en el *JavaFX Application Thread* para evitar congelar la interfaz. Usa `Platform.runLater()` para actualizar la UI una vez finalizada la tarea.

### C. Capa de Datos (DAO)
* **Seguridad:** Usar `PreparedStatement` siempre para evitar inyección SQL al recibir parámetros del usuario.
* **Recursos:** Manejo de recursos con *Try-with-resources* para asegurar el cierre automático de `Connection`, `Statement` y `ResultSet`.
* **Patrón Singleton:** Implementar el patrón Singleton para la clase gestora de la conexión a la base de datos, asegurando una instancia única.

## 5. Gestión de Errores
* **Producción vs Desarrollo:** No imprimir `e.printStackTrace()` en producción.
* **Feedback al Usuario:** Si un error interrumpe el flujo del usuario (ej. fallo de login), mostrar una `Alert` de JavaFX informativa.
* **Logging:** Usar un sistema de logs (como SLF4J con Logback o Log4j) para registrar errores críticos en el backend.