# Microservicio de Administración

## Sistema de Foros en Línea

**Asignatura:** Desarrollo Full Stack III

**Institución:** Duoc UC

**Proyecto:** Sistema de Foros en Línea

**Microservicio:** administracion-service

## 1. Descripción

Microservicio backend desarrollado con Java y Spring Boot para administrar las categorías, publicaciones y comentarios de un sistema de foros en línea.

Permite realizar operaciones CRUD sobre estas entidades y ejecutar acciones de moderación, como banear y reactivar publicaciones y comentarios.

El acceso a los endpoints está protegido mediante autenticación JWT y autorización basada en roles.

Este microservicio funciona de manera independiente de `usuarios-service`, pero utiliza el mismo esquema de Oracle Autonomous AI Database.

## 2. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JSON Web Token (JWT)
- Oracle Autonomous AI Database
- Maven
- Postman
- Git y GitHub

## 3. Arquitectura

El proyecto utiliza una arquitectura organizada por capas:

- **Controller:** expone los endpoints REST.
- **Service:** contiene la lógica de negocio y moderación.
- **Repository:** gestiona el acceso a Oracle mediante JPA.
- **Model:** representa las entidades de la base de datos.
- **DTO:** define los datos recibidos y enviados por la API.
- **Config:** contiene la configuración de seguridad y validación JWT.

Las entidades administradas son:

- Categoría
- Publicación
- Comentario

## 4. Base de datos

Se utiliza Oracle Autonomous AI Database.

**Esquema:** `FORO_APP`

### CATEGORIAS

Almacena las categorías que organizan las publicaciones del foro.

Campos principales:

- ID_CATEGORIA
- NOMBRE
- DESCRIPCION

### PUBLICACIONES

Almacena las publicaciones realizadas por los usuarios.

Campos principales:

- ID_PUBLICACION
- TITULO
- CONTENIDO
- ID_USUARIO
- ID_CATEGORIA
- ESTADO
- FECHA_CREACION

Los estados permitidos son `ACTIVO` y `BANEADO`.

### COMENTARIOS

Almacena los comentarios asociados a las publicaciones.

Campos principales:

- ID_COMENTARIO
- CONTENIDO
- ID_PUBLICACION
- ID_USUARIO
- ESTADO
- FECHA_CREACION

Los estados permitidos son `ACTIVO` y `BANEADO`.

### Relaciones entre entidades

- Una publicación pertenece a una categoría.
- Una publicación está asociada a un usuario.
- Un comentario pertenece a una publicación.
- Un comentario está asociado a un usuario.

La tabla `USUARIOS` es gestionada por el microservicio `usuarios-service`.

## 5. Configuración

El proyecto utiliza el archivo:

`src/main/resources/application.properties`

La conexión con Oracle y la validación JWT se configuran mediante variables de entorno.

| Variable | Descripción |
|----------|-------------|
| DB_URL | URL JDBC de conexión a Oracle, incluida la ruta local del wallet |
| DB_USERNAME | Usuario del esquema Oracle |
| DB_PASSWORD | Contraseña del usuario Oracle |
| JWT_SECRET | Clave Base64 compartida con usuarios-service para validar JWT |

Ejemplo de configuración de la conexión en Windows PowerShell:

```powershell
$env:DB_URL = "jdbc:oracle:thin:@crisdevdb_medium?TNS_ADMIN=C:/ruta/local/oracle-wallet/CrisDevDB"
$env:DB_USERNAME = "FORO_APP"
```

La contraseña debe configurarse de manera segura en la variable `DB_PASSWORD`.

Para validar los tokens emitidos por `usuarios-service`, ambos microservicios deben utilizar el mismo valor de `JWT_SECRET`.

La clave debe representar al menos 32 bytes aleatorios y estar codificada en Base64.

**Importante:** no publicar contraseñas, claves JWT ni archivos del wallet de Oracle en GitHub.

## 6. Ejecución

### Requisitos

- JDK 21
- Maven Wrapper incluido en el proyecto
- Acceso a Oracle Autonomous AI Database
- Wallet de Oracle configurado
- Variables de entorno configuradas
- Esquema y tablas creados previamente en Oracle

### Iniciar el microservicio

Desde la carpeta `administracion-service`, ejecutar en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

El servicio utiliza el puerto:

`8082`

**URL base:**

`http://localhost:8082`

### Ejecutar pruebas automatizadas

```powershell
.\mvnw.cmd clean test
```

## 7. Endpoints REST

Los endpoints de administración están protegidos y requieren un JWT válido con rol `ADMIN`.

### Gestión de categorías

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/categorias | Listar categorías |
| GET | /api/categorias/{id} | Consultar categoría por ID |
| POST | /api/categorias | Crear categoría |
| PUT | /api/categorias/{id} | Actualizar categoría |
| DELETE | /api/categorias/{id} | Eliminar categoría |

### Gestión de publicaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/publicaciones | Listar publicaciones |
| GET | /api/publicaciones/{id} | Consultar publicación por ID |
| POST | /api/publicaciones | Crear publicación |
| PUT | /api/publicaciones/{id} | Actualizar publicación |
| DELETE | /api/publicaciones/{id} | Eliminar publicación |
| PATCH | /api/publicaciones/{id}/banear | Banear publicación |
| PATCH | /api/publicaciones/{id}/reactivar | Reactivar publicación |

### Gestión de comentarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/comentarios | Listar comentarios |
| GET | /api/comentarios/{id} | Consultar comentario por ID |
| POST | /api/comentarios | Crear comentario |
| PUT | /api/comentarios/{id} | Actualizar comentario |
| DELETE | /api/comentarios/{id} | Eliminar comentario |
| PATCH | /api/comentarios/{id}/banear | Banear comentario |
| PATCH | /api/comentarios/{id}/reactivar | Reactivar comentario |

## 8. Autenticación JWT

El microservicio `administracion-service` valida los tokens JWT generados por `usuarios-service`.

El inicio de sesión se realiza mediante:

**POST**

`http://localhost:8081/api/auth/login`

Ejemplo de solicitud:

```json
{
  "email": "admin@foro.test",
  "password": "DemoForo2026!"
}
```

Las credenciales corresponden únicamente al entorno de demostración.

El token recibido debe enviarse a los endpoints protegidos mediante la cabecera:

`Authorization: Bearer TOKEN`

### Control de acceso

| Operación | Roles permitidos |
|---|---|
| Banear y reactivar publicaciones y comentarios (`PATCH .../banear`, `PATCH .../reactivar`) | ADMIN, MODERADOR |
| Consultar categorías, publicaciones y comentarios (`GET`) | ADMIN, MODERADOR |
| Crear, actualizar y eliminar (`POST`, `PUT`, `DELETE`) | ADMIN |

- Sin token JWT válido: `401 Unauthorized`.
- Con un JWT válido de un rol sin permisos para la operación: `403 Forbidden`.

Ambos microservicios utilizan la misma clave `JWT_SECRET` para firmar y validar los tokens.

## 9. Ejemplos de uso

### Crear categoría

**POST**

`http://localhost:8082/api/categorias`

```json
{
  "nombre": "Desarrollo Backend",
  "descripcion": "Consultas y experiencias sobre desarrollo de aplicaciones backend."
}
```

**Respuesta esperada:** `201 Created`.

### Crear publicación

**POST**

`http://localhost:8082/api/publicaciones`

```json
{
  "titulo": "Aprendiendo Spring Boot",
  "contenido": "Publicacion de ejemplo para comprobar el funcionamiento de la API.",
  "idUsuario": 2,
  "idCategoria": 2
}
```

**Respuesta esperada:** `201 Created`.

### Crear comentario

**POST**

`http://localhost:8082/api/comentarios`

```json
{
  "contenido": "Excelente explicacion sobre Spring Boot.",
  "idPublicacion": 1,
  "idUsuario": 2
}
```

**Respuesta esperada:** `201 Created`.

### Banear publicación

**PATCH**

`http://localhost:8082/api/publicaciones/1/banear`

**Respuesta esperada:** `200 OK`, con estado `BANEADO`.

### Reactivar publicación

**PATCH**

`http://localhost:8082/api/publicaciones/1/reactivar`

**Respuesta esperada:** `200 OK`, con estado `ACTIVO`.

### Banear comentario

**PATCH**

`http://localhost:8082/api/comentarios/1/banear`

**Respuesta esperada:** `200 OK`, con estado `BANEADO`.

### Reactivar comentario

**PATCH**

`http://localhost:8082/api/comentarios/1/reactivar`

**Respuesta esperada:** `200 OK`, con estado `ACTIVO`.

## 10. Pruebas realizadas

Se verificaron los siguientes escenarios mediante Postman:

| Prueba | Resultado |
|--------|-----------|
| Listar categorías con JWT ADMIN | 200 OK |
| Crear categoría | 201 Created |
| Actualizar categoría | 200 OK |
| Eliminar categoría | 204 No Content |
| Listar publicaciones | 200 OK |
| Crear publicación | 201 Created |
| Actualizar publicación | 200 OK |
| Banear publicación | 200 OK |
| Reactivar publicación | 200 OK |
| Eliminar publicación | 204 No Content |
| Listar comentarios | 200 OK |
| Crear comentario | 201 Created |
| Actualizar comentario | 200 OK |
| Banear comentario | 200 OK |
| Reactivar comentario | 200 OK |
| Eliminar comentario | 204 No Content |
| Acceder sin JWT | 401 Unauthorized |
| Acceder con JWT sin rol ADMIN | 403 Forbidden |

También se verificó la persistencia, actualización y eliminación de registros mediante Oracle SQL Developer.

### Pruebas Maven

Se ejecutó:

```powershell
.\mvnw.cmd clean test
```

Resultado:

- Tests ejecutados: 8
- Fallos: 0
- Errores: 0
- Pruebas omitidas: 1 (prueba de contexto que requiere conexión a Oracle)
- BUILD SUCCESS

Las pruebas unitarias cubren los servicios de categorías, publicaciones y comentarios.

Las operaciones REST se comprobaron manualmente mediante Postman.

## 11. Scripts SQL

Los scripts SQL se encuentran en la carpeta `database/` del repositorio.

| Script | Descripción |
|--------|-------------|
| 01_crear_usuario.sql | Creación del usuario y asignación de permisos en Oracle |
| 02_crear_tablas.sql | Creación de las tablas, claves primarias, foráneas y restricciones |
| 03_insertar_datos.sql | Inserción de datos iniciales (roles, usuarios y 5 categorías) |
| 04_consultas_verificacion.sql | Consultas para verificar los datos almacenados |
| 05_insertar_moderador.sql | Inserción del usuario de demostración con rol MODERADOR |

Los scripts deben ejecutarse en Oracle con los permisos correspondientes y respetando el orden indicado.

## 12. Consideraciones de seguridad

- Los endpoints requieren un JWT válido con rol ADMIN o MODERADOR, según la operación.
- La validación JWT utiliza una clave compartida con `usuarios-service`.
- Los datos se almacenan en Oracle Autonomous AI Database.
- Las credenciales se obtienen mediante variables de entorno.
- Los archivos del wallet de Oracle no deben publicarse en el repositorio.
- Las contraseñas y claves utilizadas en las demostraciones deben sustituirse fuera del entorno de pruebas.

## 13. Despliegue en AWS

El microservicio se ejecuta como contenedor Docker en una instancia Amazon EC2, junto con los demás
microservicios y el frontend. Nginx actúa como proxy inverso y expone todo bajo una única URL pública;
las solicitudes a `/api/categorias, /api/publicaciones y /api/comentarios` se envían a este servicio (puerto interno `8082`).

| Componente | Uso |
|---|---|
| Amazon ECR | Repositorio de imágenes `foro-online/administracion-service` |
| Amazon EC2 | Host Docker + Nginx, creado con CloudFormation (`foro-frontend/ec2/foro-online-ec2-stack.yml`) |
| AWS Systems Manager | Despliegue remoto en la instancia, sin SSH |
| Amazon S3 | Bucket privado con el wallet de Oracle |
| AWS Secrets Manager | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET` |

El workflow `.github/workflows/publish-ecr.yml` se ejecuta en cada push a `main` que modifica código
(los cambios solo de documentación se omiten):

1. Ejecuta las pruebas con Maven.
2. Construye la imagen Docker y la publica en ECR con las etiquetas `latest` y el SHA del commit.
3. Despliega en EC2 mediante Systems Manager: descarga el wallet desde S3, lee las credenciales desde
   Secrets Manager, reemplaza el contenedor y verifica `/actuator/health`. Si el servicio no queda
   disponible, restaura la versión anterior.

Configuración del repositorio en GitHub:

- **Secrets:** `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`.
- **Variables:** `AWS_REGION`, `EC2_INSTANCE_ID`, `ORACLE_CREDENTIALS_SECRET_ARN`,
  `ORACLE_WALLET_S3_BUCKET`, `ORACLE_WALLET_S3_KEY`.

En AWS Academy las credenciales del laboratorio son temporales, por lo que los secrets deben
actualizarse al iniciar cada sesión.

## 14. Pruebas con Postman

La colección `postman/Foro-Online.postman_collection.json` cubre los tres microservicios del sistema:
39 solicitudes y 41 validaciones automáticas, incluidos los casos de error 400, 401, 403, 404 y 409.

| Carpeta | Contenido |
|---|---|
| 0. Autenticación | Login de ADMIN, MODERADOR y USUARIO; credenciales incorrectas |
| 1. usuarios-service | Registro, perfil, listado, cambio de rol, estado y eliminación |
| 2. foro-service | Búsqueda, detalle, publicación, edición y comentarios |
| 3. administracion-service | Banear y reactivar publicaciones y comentarios; CRUD de categorías |
| 4. Limpieza | Elimina los datos creados durante la prueba |

Uso:

1. Importar la colección en Postman.
2. Ajustar la variable de colección `baseUrl` con la URL pública de la instancia EC2
   (la IP cambia si la instancia se reinicia).
3. Ejecutar primero los logins de la carpeta `0. Autenticación`: guardan los tokens en variables
   que usan las demás solicitudes. También se puede ejecutar la colección completa con *Run collection*.

Desde la línea de comandos:

```bash
npx newman run postman/Foro-Online.postman_collection.json
```

## 15. Flujo de trabajo Git

- `main` es la rama estable: cada cambio de código integrado en ella se despliega en AWS.
- Los cambios se desarrollan en ramas de corta duración con un prefijo según su tipo:
  `feature/`, `fix/`, `docs/`, `ci/`, `refactor/`.
- Cada rama se integra a `main` mediante un Pull Request (merge commit) y se elimina después del merge.
- Los mensajes de commit siguen la convención *Conventional Commits*: `feat:`, `fix:`, `docs:`,
  `test:`, `ci:`, `refactor:`.
