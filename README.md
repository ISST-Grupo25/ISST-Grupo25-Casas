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

- MySQL
- mvm
- Java 11+
- Apache Tomcat 9+

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

   - Edita los archivos en conflicto.
   - Una vez resueltos, guarda los cambios y haz:

   ```bash
   git add .
   git commit -m "Conflictos resueltos en feature/nombre-rama"
   ```

   6️⃣ **_Sube los cambios a GitHub:_**

   ```bash
   git push origin feature/nombre-rama
   ```

   7️⃣ **_Crea un Pull Request en GitHub:_**

   - Desde GitHub, abre un Pull Request de feature/nombre-rama hacia develop.
   - Espera la revisión y aprobación del equipo.

   8️⃣ **_Fusiona el Pull Request cuando esté aprobado:_**

   - Una vez aprobado, merge en develop.
   - Luego, actualiza tu repositorio local:

   ```bash
   git checkout develop
   git pull origin develop
   ```

   9️⃣ **_Borra la rama después de fusionarla:_**

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

1. **Simulación de la cerradura:**

   - Abrir otra terminal para ejecutar la simulacion de la cerradura.
   - Acceder al repositorio de la simulación de la cerradura:
     ```bash
     git clone https://github.com/ISST-Grupo25/cerraduraSoftware.git
     cd cerraduraSoftware
     ```
   - Seguir las instrucciones del readme de ese repositorio

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

## Guía de Instalaciones

### 1. Instalar MySQL

#### ✅ **Instalación en Windows:**

- Descarga MySQL desde: [MySQL Installer](https://dev.mysql.com/downloads/installer/).
- Instala MySQL Server y MySQL Workbench (opcional).
- Durante la instalación, configura:
  - Usuario: root
  - Contraseña: Mirar el proyecto.
- Agrega MySQL al PATH del sistema.

#### ✅ Instalación en macOS (con Homebrew):

```bash
brew install mysql
brew services start mysql
```

Para ingresar a MySQL:

```bash
mysql -u root -p
```

✅ **Configuración de la Base de Datos**

Abre MySQL y ejecuta:

```sql
CREATE DATABASE isst_db;
```

Verifica que se haya creado con:

```sql
SHOW DATABASES;
```

### 2. Instalar Maven (mvn)

💡 Maven es el gestor de dependencias del proyecto.

#### ✅ Instalación en Windows:

- Descarga [Maven](https://maven.apache.org/download.cgi).
- Extrae el .zip en C:\Program Files\Apache\Maven.
- Agrega C:\Program Files\Apache\Maven\bin al PATH del sistema.
- Verifica la instalación con:

```bash
 mvn -v
```

#### ✅ Instalación en macOS/Linux (con Homebrew o apt):

```bash
brew install maven # macOS
sudo apt install maven # Ubuntu
```

Verifica la instalación con:

```bash
mvn -v
```

### 3. Instalar Java 11 o superior

💡 Java es necesario para ejecutar la aplicación.

#### ✅ Instalación en Windows:

- Descarga Java 11+ desde [OpenJDK](https://jdk.java.net/) o [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).
- Instala y configura la variable de entorno `JAVA_HOME` apuntando a la carpeta de instalación.
  Verifica la instalación con:
  ```bash
  java -version
  ```

#### ✅ Instalación en macOS/Linux (con Homebrew o apt):

```bash
brew install openjdk@11 # macOS
sudo apt install openjdk-11-jdk # Ubuntu
```

Verifica la instalación con:

```bash
java -version
```

### 4. Instalar Apache Tomcat 9+

💡 Apache Tomcat es el servidor de aplicaciones que ejecutará nuestro backend en Spring Boot.

#### ✅ Instalación en Windows y macOS:

- Descarga Tomcat 9+ desde: [Apache Tomcat](https://tomcat.apache.org/download-90.cgi).
- Extrae el archivo `.zip` o `.tar.gz`.

Para iniciar Tomcat:

- En Windows, ejecuta:

```bash
C:\ruta\de\tomcat\bin\startup.bat
```

- En macOS/Linux:

```bash
cd /ruta/de/tomcat/bin
./startup.sh
```

Verifica que Tomcat está corriendo abriendo en el navegador:

```
http://localhost:8080
```

🎯 **Verificación Final**

Después de instalar todo, revisa que los comandos funcionan correctamente:

```bash
mysql --version
mvn -v
java -version
```
