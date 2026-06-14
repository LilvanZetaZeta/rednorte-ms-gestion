# RedNorte - Microservicio de Gestión (ms-gestion)

El Microservicio de Gestión es el núcleo transaccional y operativo de **RedNorte**. Se encarga de manejar la asignación del personal médico, la creación y reserva de citas médicas, el historial de consultas y los flujos críticos de negocio.

## Características Principales

*   **Registro y Gestión de Usuarios**: Registro de perfiles, asignación de roles (Paciente, Médico, Administrativo, Director, Secretaria).
*   **Gestión de Sucursales y Especialidades**: CRUD de centros médicos y especialidades clínicas, asignación de especialidades a médicos.
*   **Reservas de Citas Médicas**: Agendamiento de citas, cancelaciones automáticas y flujos de reserva prioritaria por parte de secretarias.
*   **Bloqueo de Agenda**: Bloqueo de agendas para médicos con reprogramación automática integrada al motor de reasignación.
*   **Validaciones en el Backend**: Validaciones estrictas implementadas en la capa de entidades/DTOs y validación manual integrada con el controlador global de excepciones (`FormValidationException`).
*   **Unicidad en Centros y Especialidades**: Validación e impedimento de nombres de especialidades y centros médicos duplicados.

## Tecnologías Utilizadas

*   **Java 21**
*   **Spring Boot 3.4.1**
*   **Spring Data JPA & PostgreSQL**
*   **Jakarta Validation (Bean Validation)**
*   **Spring Security & Resource Server (Supabase JWKs)**
*   **Spring Dotenv**
*   **Lombok**
*   **Spring Cloud OpenFeign** (para comunicación inter-microservicio)

## Requisitos Previos

*   Java 21 o superior.
*   Maven 3.8 o superior.
*   PostgreSQL (o conexión a base de datos externa).
*   Archivo de variables de entorno `.env` en este directorio.

## Variables de Entorno (.env)

Crea un archivo `.env` en la raíz de este directorio con la siguiente estructura de variables (reemplaza los valores entre `<>` con tus configuraciones correspondientes):

```env
# Puerto en el que se ejecutará el microservicio de Gestión (por defecto 8081)
PORT=<puerto_ms_gestion>

# URL base de tu proyecto de Supabase (ej: https://<project-id>.supabase.co)
SUPABASE_URL=<url_proyecto_supabase>

# URL base del API Gateway (ej: http://localhost:8080)
MS_APIGATEWAY_URL=<url_api_gateway>

# URL base del Microservicio de Gestión (ej: http://localhost:8081)
MS_GESTION_URL=<url_microservicio_gestion>

# URL base del Microservicio Portal (ej: http://localhost:8082)
MS_PORTAL_URL=<url_microservicio_portal>

# URL base del Microservicio de Reasignación (ej: http://localhost:8083)
MS_REASIGNACION_URL=<url_microservicio_reasignacion>

# URL base del Microservicio de Notificaciones (ej: http://localhost:8085)
MS_NOTIFICACIONES_URL=<url_microservicio_notificaciones>

# URL base de la Aplicación Frontend (ej: http://localhost:5173)
MS_FRONTEND_URL=<url_frontend>

# Secreto JWT para la firma de tokens de Supabase (utilizado en autenticación y validación de tokens)
SUPABASE_JWT_SECRET=<jwt_secret_supabase>

# URL JDBC de conexión a PostgreSQL (ej: jdbc:postgresql://<host>:<port>/<dbname>)
DB_URL=<jdbc_conexion_postgresql>

# Nombre de usuario para la base de datos PostgreSQL
DB_USERNAME=<usuario_database>

# Contraseña del usuario de la base de datos PostgreSQL
DB_PASSWORD=<password_database>

# URL de los JWKs de Supabase para validar los tokens JWT
SUPABASE_JWKS_URI=<url_supabase_jwks>

# URI del emisor de tokens (issuer) de Supabase
issuer-uri=<uri_emisor_tokens_supabase>

# Clave pública anónima de Supabase (Anon Key)
SUPABASE_ANON_KEY=<anon_key_supabase>
```

## Instrucciones de Ejecución

### Desarrollo Local

Para iniciar el microservicio localmente en el puerto `8081`:

```bash
./mvnw spring-boot:run
```

### Ejecutar Pruebas

Para correr las pruebas unitarias y de integración de la base de datos y validaciones:

```bash
./mvnw test -Dnet.bytebuddy.experimental=true
```

## Dockerización

Construir la imagen de Docker:

```bash
docker build -t rednorte-ms-gestion .
```
