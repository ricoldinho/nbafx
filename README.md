# 🏀 Proyecto NbaFX - Sistema de Gestión con JavaFX

Este proyecto es una aplicación de escritorio robusta desarrollada en **Java 21** utilizando **JavaFX** para la interfaz gráfica. Implementa una arquitectura profesional por capas (MVC + DAO) y conecta con una base de datos **MySQL**.

Actualmente, el proyecto cuenta con un sistema de **Login y Registro** funcional, gestionado bajo la arquitectura modular de Java (JPMS).

---

## 🏗️ Arquitectura del Proyecto

El código sigue estrictamente el patrón de separación de responsabilidades para facilitar el mantenimiento y la escalabilidad:

1.  **Modelo (`edu.rico.nbafx.model`)**:
    *   Clases POJO (Plain Old Java Objects) que representan las tablas de la base de datos (ej. `Usuario`).
    *   No contienen lógica de negocio, solo datos.

2.  **DAO (`edu.rico.nbafx.dao`)**:
    *   **Data Access Object**: Es la única capa que toca SQL.
    *   Gestiona las operaciones CRUD (Create, Read, Update, Delete).
    *   Usa `PreparedStatement` para seguridad y *Try-with-resources* para gestión de memoria.

3.  **Servicio (`edu.rico.nbafx.service`)**:
    *   Contiene la lógica de negocio (ej. validar si una contraseña es segura, si el usuario existe, etc.).
    *   Actúa de intermediario entre el Controlador y el DAO.

4.  **Controlador (`edu.rico.nbafx.controller`)**:
    *   Gestiona la interacción con la interfaz gráfica (eventos de botones, lectura de campos de texto).
    *   Delega la lógica pesada a la capa de Servicio.

5.  **Vista (`resources/fxml`)**:
    *   Archivos `.fxml` que definen la estructura visual de las ventanas.

6.  **Infraestructura (`edu.rico.nbafx.util`)**:
    *   `DatabaseConnection`: Clase Singleton para gestionar la conexión a MySQL de forma eficiente.

---

## 🚀 Requisitos Previos

Para ejecutar este proyecto necesitas:
*   **Java JDK 21** o superior.
*   **Maven** (para gestión de dependencias).
*   **MySQL Server** instalado y corriendo.
*   Un IDE compatible (IntelliJ IDEA recomendado).

---

## ⚙️ Configuración de la Base de Datos

Antes de iniciar, debes crear la base de datos y la tabla de usuarios. Ejecuta el siguiente script en tu cliente MySQL (Workbench, DBeaver, etc.):

```sql
CREATE DATABASE IF NOT EXISTS nbafx;
USE nbafx;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- En producción, usar hash!
    rol VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario de prueba inicial
INSERT INTO usuarios (nombre, password, rol) VALUES ('admin', 'admin123', 'ADMIN');
```

### Archivo de Propiedades
Asegúrate de tener el archivo `src/main/resources/config.properties` con tus credenciales locales:

```properties
db.url=jdbc:mysql://localhost:3306/nbafx_db
db.user=root
db.password=tu_contraseña_aqui
```

---

## ▶️ Cómo Ejecutar el Proyecto

Este proyecto utiliza el sistema de **Módulos de Java (JPMS)**, definido en `module-info.java`. Esto significa que no requiere trucos ni clases "Launcher" auxiliares.

1.  Abre el proyecto en IntelliJ IDEA.
2.  Espera a que Maven descargue las dependencias.
3.  Busca la clase principal: `src/main/java/edu/rico/nbafx/MainApp.java`.
4.  Haz clic derecho -> **Run 'MainApp.main()'**.

> **Nota:** Si ves errores sobre componentes de JavaFX faltantes, asegúrate de haber recargado el proyecto Maven (Click derecho en `pom.xml` -> Reload Project) para que el IDE reconozca el archivo `module-info.java`.

---

## 📂 Estructura de Carpetas

```text
src/main/java/edu/rico/nbafx
├── controller/       # Controladores de JavaFX (LoginController)
├── dao/              # Acceso a Datos (UsuarioDAO)
├── model/            # Entidades (Usuario, Rol)
├── service/          # Lógica de Negocio (UsuarioService)
├── util/             # Utilidades (DatabaseConnection)
├── MainApp.java      # Clase Principal (extends Application)
└── module-info.java  # Definición del Módulo Java

src/main/resources
├── fxml/             # Vistas (.fxml)
├── css/              # Estilos (.css)
└── config.properties # Configuración de BD
```