# PROYECTO ELECCIONES

## 1. Introducción y Propósito del Sistema
El sistema **EleccionesUni** es una solución de software integral diseñada para la gestión, administración y auditoría de procesos electorales en el ámbito universitario. Su objetivo principal es garantizar la transparencia, seguridad y eficiencia en la configuración de elecciones, el empadronamiento de votantes (censo), la inscripción de candidatos, la emisión de sufragios y el cómputo final de resultados.

El sistema está construido sobre una arquitectura robusta orientada a servicios, asegurando escalabilidad y mantenibilidad, y expone una Interfaz de Programación de Aplicaciones (API) RESTful para la integración con diversos clientes (web, móvil).

## 2. Arquitectura del Sistema
El proyecto sigue el patrón arquitectónico **Modelo-Vista-Controlador (MVC)**, adaptado para el desarrollo de APIs REST (Representational State Transfer). La estructura del código promueve la separación de responsabilidades en las siguientes capas:

### 2.1. Capa de Controladores (Controllers)
Encargada de recibir las peticiones HTTP, validar los datos de entrada y delegar la lógica de negocio a la capa de servicios.
*   **Paquete**: `com.universidad.elecciones.controller`
*   **Componentes Principales**:
    *   `VotanteController`: Gestión de información de votantes.
    *   `EleccionController`: Administración del ciclo de vida de una elección.
    *   `AuthController`: Endpoints para autenticación y autorización.

### 2.2. Capa de Servicios (Services)
Contiene la lógica de negocio central, reglas de validación complejas y coordinación de transacciones.
*   **Paquete**: `com.universidad.elecciones.service`
*   **Módulos Clave**:
    *   **Gestión de Procesos Electorales**: (`EleccionService`, `ProcesoService`) Define la creación, apertura y cierre de elecciones.
    *   **Gestión del Censo**: (`CensoService`, `VotanteService`) Administra el padrón electoral y valida la elegibilidad de los votantes.
    *   **Sistema de Votación**: (`InscripcionService`) Maneja la postulación de candidatos.
    *   **Cómputo de Resultados**: (`ResultadoService`) Realiza el conteo y generación de informes de resultados electorales.
    *   **Seguridad y Auditoría**: (`AuthService`, `JwtAuthenticationFilter`) Implementa la lógica de autenticación y protección de recursos.

### 2.3. Capa de Persistencia (Repositories)
Interactúa directamente con la base de datos utilizando **Spring Data JPA**. Abstrae las consultas SQL mediante interfaces que extienden de `JpaRepository`.
*   **Paquete**: `com.universidad.elecciones.repository`

### 2.4. Modelo de Datos (Entities)
Representa el esquema de la base de datos mapeado a objetos Java mediante JPA (Java Persistence API).
*   **Paquete**: `com.universidad.elecciones.entity`
*   **Entidades Principales**: `Eleccion`, `Votante`, `Candidato`, `Voto`, `Facultad`.

### 2.5. Objetos de Transferencia de Datos (DTOs)
Para desacoplar el modelo de base de datos de la interfaz pública, se utilizan DTOs para la transferencia de información entre el cliente y el servidor.
*   **Paquete**: `com.universidad.elecciones.dto`

## 3. Especificaciones Técnicas

### 3.1. Stack Tecnológico
*   **Lenguaje**: Java 17 (LTS)
*   **Framework**: Spring Boot 3.2.5
*   **Seguridad**: Spring Security + JWT (JSON Web Tokens)
*   **Base de Datos**: PostgreSQL 
*   **ORM**: Hibernate (vía Spring Data JPA)
*   **Documentación de API**: SpringDoc OpenAPI (Swagger UI)
*   **Utilidades**: Project Lombok (reducción de código repetitivo)
*   **Comunicación**: Java Mail Sender (notificaciones SMTP)

### 3.2. Seguridad
La seguridad del sistema se basa en un modelo **Stateless** (sin estado) utilizando tokens JWT.
1.  El usuario se autentica a través del endpoint `/auth/login`.
2.  El servidor valida las credenciales y emite un token JWT firmado.
3.  Cada petición subsiguiente debe incluir este token en el encabezado `Authorization: Bearer <token>`.
4.  El filtro `JwtAuthenticationFilter` intercepta cada petición para validar la firma y vigencia del token antes de permitir el acceso al recurso protegido.

## 4. Guía de Instalación y Despliegue

### 4.1. Requisitos del Sistema
*   **Java JDK 17** instalado y configurado en el `PATH` del sistema.
*   **Apache Maven 3.8+** (opcional si se utiliza el wrapper `mvnw` incluido).
*   **Servidor de Base de Datos PostgreSQL** en ejecución.

### 4.2. Configuración del Entorno
1.  Clonar el repositorio del proyecto.
2.  Crear una base de datos PostgreSQL vacía (por defecto: `bd_pruebas`).
3.  Configurar las credenciales de conexión en el archivo `src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/bd_pruebas
    spring.datasource.username=USUARIO_DB
    spring.datasource.password=CONTRASEÑA_DB
    ```

### 4.3. Compilación y Ejecución
Para compilar el proyecto y descargar las dependencias, ejecute el siguiente comando en la terminal:

```bash
# Entornos Windows
./mvnw clean install

# Entornos Linux/Mac
./mvnw clean install
```

Para iniciar la aplicación:

```bash
./mvnw spring-boot:run
```

El servidor iniciará en el puerto **8080** (http://localhost:8080).

## 5. Documentación de la API

El proyecto incluye documentación interactiva generada con la especificación OpenAPI 3.0. Esta documentación permite visualizar todos los endpoints disponibles y sus contratos de datos (Request/Response).

**Acceso a Swagger UI:**
[http://localhost:8080/docs/swagger-ui.html](http://localhost:8080/docs/swagger-ui.html)

---
*Documentación generada para propósitos académicos y de desarrollo.*

