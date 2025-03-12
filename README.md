# ISST-Grupo25-Casas

## Descripción del Proyecto

El alquiler de alojamiento turístico es muy popular en grandes ciudades, pero la gestión manual de llaves y accesos puede ser lenta y propensa a errores. Este proyecto propone un **sistema web de gestión de accesos** para viviendas, habitaciones y edificios turísticos mediante **cerraduras inteligentes conectadas a internet**. En lugar de llaves físicas (disponibles solo para emergencias), los huéspedes podrán acceder utilizando **Bluetooth desde sus teléfonos móviles**.

## Funcionalidades Principales

- **Gestión de accesos:** Control remoto y automático de puertas mediante Bluetooth.
- **Modos de acceso personalizados:**
  - Por usuario identificado o token.
  - Por horarios y días específicos.
  - Acceso combinado a múltiples puertas (ej. portal y habitación).
  - Acceso libre en determinadas circunstancias.
- **Monitorización y seguridad:**
  - Registro de intentos de acceso para análisis y detección de intentos de intrusión.
  - Protección de datos según normativas legales.
- **Integración con Google Calendar:**
  - Carga masiva de restricciones de tiempo y usuarios mediante ficheros de Google Calendar.
- **Interfaz gráfica intuitiva** para visualizar y programar accesos.

## Tecnologías Utilizadas

El sistema se basa en una arquitectura **cliente-servidor en tres niveles**, utilizando las siguientes tecnologías:

### 🛠️ **Backend**

- **Java REST API**
- **Servidor de aplicaciones Tomcat**
- **Base de datos relacional (MySQL)**

### 💅🏻 **Frontend**

- **HTML5, CSS3, JavaScript**
- **Frameworks modernos (React, Angular o Vue.js)**

### 📲 **Móvil (Opcional)**

- Aplicación nativa para **Android (Kotlin)** y/o **iOS (Swift)**

### 🔗 **Integraciones**

- **Cerraduras inteligentes** compatibles con Bluetooth e IoT.
- **Google Calendar** para sincronización de reservas.

## Metodología de Desarrollo

- Desarrollo ágil con **Scrum**.
- Iteraciones incrementales para validar la viabilidad del producto.
- Implementación de un **MVP (Producto Mínimo Viable)**.
- Evaluación de la arquitectura y tecnologías elegidas.

## Despliegue

- El sistema se desplegará preferentemente en **Apache Tomcat**.
- Base de datos alojada en un servidor SQL.
- Posibilidad de implementación en la nube (AWS, Google Cloud, Azure).

## Instalación y Configuración

### **Requisitos Previos**

- Java 11+
- Apache Tomcat 9+
- MySQL
- Node.js y npm (para el frontend)

## Pasos para ejecutar el proyecto

### **Extensiones recomendadas de VSCode**

- Git Graph
- IntelliCode
- Prettier - Code formatter
- GitHub Copilot
- GitHub Codespaces
- Gradle for Java
- CSS Peek
- HTML/CSS/JavaScript Snippets

### **Gestión de Ramas de GitHub**

1. **Estructura de Ramas**

- main: Rama principal y estable. Solo se fusionan cambios cuando una versión está completamente probada.
- develop: Rama de desarrollo donde integramos nuevas funciones antes de pasar a main.
- feature/{nombre}: Ramas para desarrollar nuevas funcionalidades.
- bugfix/{nombre}: Ramas para corregir errores detectados en develop o main.
- hotfix/{nombre}: Ramas para correcciones urgentes en main.

2. **Flujo de Trabajo con Git**

   1️⃣ **_Clona el repositorio (solo la primera vez):_**

   ```bash
   git clone https://github.com/ISST-Grupo25/ISST-Grupo25-Casas.git
   cd ISST-Grupo25-Casas
   git checkout develop
   ```

   2️⃣ **_Crea una nueva rama para tu funcionalidad o corrección:_**

   ```bash
   git checkout -b feature/nombre-rama
   ```

   3️⃣ **_Trabaja en tu código y haz commits regularmente:_**

   ```bash
   git add .
   git commit -m "Descripción del cambio"
   ```

   4️⃣ **_Actualiza tu rama con develop antes de subir los cambios:_**

   ```bash
   git fetch origin
   git checkout develop
   git pull origin develop
   git checkout feature/nombre-rama
   git merge develop
   ```

   5️⃣ **_Resuelve conflictos si los hay:_**

   ```bash
   Edita los archivos en conflicto.
   Una vez resueltos, guarda los cambios y haz:
   git add .
   git commit -m "Conflictos resueltos en feature/nombre-rama"
   ```

   6️⃣ Sube los cambios a GitHub:

   ```bash
   git push origin feature/nombre-rama
   ```

   7️⃣ Crea un Pull Request en GitHub:
   Desde GitHub, abre un Pull Request de feature/nombre-rama hacia develop.
   Espera la revisión y aprobación del equipo.

   8️⃣ Fusiona el Pull Request cuando esté aprobado:
   Una vez aprobado, merge en develop.
   Luego, actualiza tu repositorio local:

   ```bash
   git checkout develop
   git pull origin develop
   ```

   9️⃣ Borra la rama después de fusionarla:

   ```bash
   git branch -d feature/nombre-rama
   git push origin --delete feature/nombre-rama
   ```

### **Creación del proyecto**

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/ISST-Grupo25/ISST-Grupo25-Casas.git
   cd ISST-Grupo25-Casas
   ```
1. **Configurar la base de datos:**

   - Crear una base de datos en MySQL.
     - En application.properties: 🔹 Configurar MySQL como base de datos (Sacar nombre db, pass, user)
   - Configurar las credenciales en el archivo de propiedades del backend.

1. 🍾 **Ejecutar el backend:** 🍾

   Cada vez que ejecutes la aplicación:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

1. **Acceder a la aplicación:**
   - Backend: `http://localhost:8080` 😎

## Equipo de Desarrollo

- **Grupo 25 - ISST**
- Metodología **Scrum** con reuniones periódicas y sprints.
- Roles: **Product Owner, Scrum Master, Developers, QA.**

## Licencia

Este proyecto está bajo la licencia **MIT**.

## Contacto

📧 **Email:** Product Owner: f.gfernandez-getino@alumnos.upm.es, Scrum Master: natalia.burguillo@alumnos.upm.es

🌐 **GitHub:** [Repositorio del Proyecto](https://github.com/ISST-Grupo25/ISST-Grupo25-Casas)
