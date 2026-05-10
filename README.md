# VG-MS-USERS - Microservicio de Gestión de Usuarios

Microservicio REST para la gestión de usuarios implementado con **Arquitectura Hexagonal (Clean Architecture)**, desarrollado con **Spring WebFlux** y **MongoDB Atlas**.

---

## 🏗️ Arquitectura Hexagonal

### Estructura del Proyecto

```
vg-ms-users/
│
├── 🎯 domain/                              # LÓGICA DE NEGOCIO
│   ├── models/
│   │   └── Users.java                      # Entidad de dominio
│   │
│   ├── ports/
│   │   ├── in/                             # Casos de uso (lo que puede hacer)
│   │   │   ├── ICreateUsersUseCase.java
│   │   │   ├── IGetUsersUseCase.java
│   │   │   ├── IUpdateUsersUseCase.java
│   │   │   ├── IDeactivateUsersUseCase.java
│   │   │   └── IRestoreUsersUseCase.java
│   │   │
│   │   └── out/                            # Repositorios (cómo guardar)
│   │       └── IUsersRepository.java
│   │
│   └── exceptions/                         # Errores de negocio
│       ├── DomainException.java
│       └── NotFoundException.java
│
├── 🔄 application/                         # ORQUESTACIÓN
│   ├── usecases/                           # Implementa la lógica
│   │   ├── CreateUsersUseCaseImpl.java
│   │   ├── GetUsersUseCaseImpl.java
│   │   ├── UpdateUsersUseCaseImpl.java
│   │   ├── DeactivateUsersUseCaseImpl.java
│   │   └── RestoreUsersUseCaseImpl.java
│   │
│   ├── dto/                                # Contratos de entrada/salida
│   │   ├── request/
│   │   │   ├── CreateUsersRequest.java
│   │   │   └── UpdateUsersRequest.java
│   │   │
│   │   ├── response/
│   │   │   └── UsersResponse.java
│   │   │
│   │   └── common/
│   │       ├── ApiResponse.java
│   │       └── ErrorResponse.java
│   │
│   └── mappers/
│       └── UsersMapper.java                # Convierte DTO ↔ Entity
│
└── ⚙️ infrastructure/                      # TECNOLOGÍA
    ├── adapters/
    │   ├── in/
    │   │   └── rest/
    │   │       └── UsersController.java    # Endpoints REST
    │   │
    │   └── out/
    │       └── persistence/
    │           ├── UsersDocument.java      # Modelo MongoDB
    │           ├── UsersMongoRepository.java
    │           └── UsersRepositoryAdapter.java
    │
    └── config/
        ├── BeanConfiguration.java
        ├── GlobalExceptionHandler.java
        └── OpenApiConfiguration.java
```

---

## 📐 Capas de la Arquitectura

### 🎯 Domain (Dominio)
- **Responsabilidad:** Lógica de negocio pura
- **Contenido:** Entidades, interfaces de casos de uso, excepciones
- **Dependencias:** Ninguna (independiente de frameworks)

### 🔄 Application (Aplicación)
- **Responsabilidad:** Orquestación de casos de uso
- **Contenido:** Implementación de casos de uso, DTOs, mappers
- **Dependencias:** Solo del dominio

### ⚙️ Infrastructure (Infraestructura)
- **Responsabilidad:** Detalles técnicos y frameworks
- **Contenido:** Controladores REST, repositorios MongoDB, configuración
- **Dependencias:** Del dominio y aplicación

---

## 🎯 Principios de la Arquitectura

- **Independencia de Frameworks:** El dominio no depende de Spring, MongoDB ni ningún framework
- **Inversión de Dependencias:** Las dependencias apuntan hacia el dominio
- **Separación de Responsabilidades:** Cada capa tiene una responsabilidad clara
- **Testabilidad:** Los casos de uso pueden testearse sin base de datos
- **Programación Reactiva:** Uso de `Mono` y `Flux` con Spring WebFlux

---

## 🔄 Flujo de una Petición

```
Cliente HTTP
    ↓
UsersController (Infrastructure/Adapters/In/Rest)
    ↓
CreateUsersUseCase (Application/UseCases)
    ↓
IUsersRepository (Domain/Ports/Out)
    ↓
UsersRepositoryAdapter (Infrastructure/Adapters/Out/Persistence)
    ↓
UsersMongoRepository (Infrastructure/Adapters/Out/Persistence)
    ↓
MongoDB Atlas
```

---

## 📡 Endpoints API

### Base URL
```
http://localhost:8081/api/v1/users
```

### CRUD Principal

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/users` | Listar todos los usuarios |
| `GET` | `/api/v1/users/{id}` | Obtener usuario por ID |
| `GET` | `/api/v1/users/status/{status}` | Filtrar por estado (ACTIVE/INACTIVE) |
| `POST` | `/api/v1/users/create` | Crear un nuevo usuario |
| `PUT` | `/api/v1/users/update/{id}` | Actualizar usuario |
| `PATCH` | `/api/v1/users/deactivate/{id}` | Desactivar usuario (INACTIVE) |
| `PATCH` | `/api/v1/users/restore/{id}` | Restaurar usuario (ACTIVE) |

---

## 📝 Ejemplos de Request/Response

### Crear Usuario
**Request:**
```http
POST /api/v1/users/create
Content-Type: application/json

{
  "firstName": "María",
  "lastName": "López Gómez",
  "documentType": "DNI",
  "documentNumber": "23456789",
  "phone": "987654321",
  "email": "maria.lopez@gmail.com",
  "username": "maria.lopez@caritas.org.pe",
  "password": "123456",
  "role": "COORDINADOR",
  "firebaseId": "kR9mP2xL5nQ8wT4vY7zA",
  "profileImagePath": "uploads/users/profile.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario creado exitosamente",
  "data": {
    "userId": "68fda092d832a694a0c77a88",
    "firebaseId": "kR9mP2xL5nQ8wT4vY7zA",
    "firstName": "María",
    "lastName": "López Gómez",
    "documentType": "DNI",
    "documentNumber": "23456789",
    "phone": "987654321",
    "email": "maria.lopez@gmail.com",
    "username": "maria.lopez@caritas.org.pe",
    "role": "COORDINADOR",
    "profileImagePath": "uploads/users/profile.jpg",
    "status": "ACTIVE",
    "createdAt": "2026-05-10T10:00:00",
    "updatedAt": "2026-05-10T10:00:00"
  }
}
```

### Listar Todos los Usuarios
**Request:**
```http
GET /api/v1/users
```

**Response:**
```json
{
  "success": true,
  "message": "Usuarios obtenidos",
  "data": [
    {
      "userId": "68fda092d832a694a0c77a88",
      "firstName": "María",
      "lastName": "López Gómez",
      "email": "maria.lopez@gmail.com",
      "role": "COORDINADOR",
      "status": "ACTIVE",
      "createdAt": "2026-05-10T10:00:00"
    }
  ]
}
```

### Obtener Usuario por ID
**Request:**
```http
GET /api/v1/users/68fda092d832a694a0c77a88
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario encontrado",
  "data": {
    "userId": "68fda092d832a694a0c77a88",
    "firstName": "María",
    "lastName": "López Gómez",
    "documentType": "DNI",
    "documentNumber": "23456789",
    "phone": "987654321",
    "email": "maria.lopez@gmail.com",
    "username": "maria.lopez@caritas.org.pe",
    "role": "COORDINADOR",
    "profileImagePath": "uploads/users/profile.jpg",
    "status": "ACTIVE",
    "createdAt": "2026-05-10T10:00:00",
    "updatedAt": "2026-05-10T10:00:00"
  }
}
```

### Actualizar Usuario
**Request:**
```http
PUT /api/v1/users/update/68fda092d832a694a0c77a88
Content-Type: application/json

{
  "firstName": "María Editada",
  "phone": "999888777",
  "role": "ADMIN",
  "profileImagePath": "uploads/users/nueva-foto.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario actualizado exitosamente",
  "data": {
    "userId": "68fda092d832a694a0c77a88",
    "firstName": "María Editada",
    "phone": "999888777",
    "role": "ADMIN",
    "profileImagePath": "uploads/users/nueva-foto.jpg",
    "updatedAt": "2026-05-10T11:00:00"
  }
}
```

### Desactivar Usuario
**Request:**
```http
PATCH /api/v1/users/deactivate/68fda092d832a694a0c77a88
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario desactivado",
  "data": null
}
```

### Restaurar Usuario
**Request:**
```http
PATCH /api/v1/users/restore/68fda092d832a694a0c77a88
```

**Response:**
```json
{
  "success": true,
  "message": "Usuario restaurado",
  "data": null
}
```

### Filtrar por Estado
**Request:**
```http
GET /api/v1/users/status/ACTIVE
```

**Response:**
```json
{
  "success": true,
  "message": "Usuarios filtrados por estado",
  "data": [
    {
      "userId": "68fda092d832a694a0c77a88",
      "firstName": "María",
      "status": "ACTIVE"
    }
  ]
}
```

---

## ⚠️ Manejo de Errores

### Usuario no encontrado (404)
```json
{
  "code": "NOT_FOUND",
  "message": "Users con id '68fda092d832a694a0c77a88' no encontrado",
  "timestamp": "2026-05-10T10:00:00"
}
```

### Email o documento duplicado (400)
```json
{
  "code": "EMAIL_EXISTS",
  "message": "El email ya está registrado",
  "timestamp": "2026-05-10T10:00:00"
}
```

### Error de validación (400)
```json
{
  "code": "VALIDATION_ERROR",
  "message": "email: Formato de email inválido, documentNumber: El número de documento es obligatorio",
  "timestamp": "2026-05-10T10:00:00"
}
```

---

## 🗄️ Modelo de Datos MongoDB

### Colección: `users`

```json
{
  "_id": "68fda092d832a694a0c77a88",
  "firebaseId": "kR9mP2xL5nQ8wT4vY7zA",
  "firstName": "María",
  "lastName": "López Gómez",
  "documentType": "DNI",
  "documentNumber": "23456789",
  "phone": "987654321",
  "email": "maria.lopez@gmail.com",
  "username": "maria.lopez@caritas.org.pe",
  "password": "$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
  "role": "COORDINADOR",
  "profileImagePath": "uploads/users/68fda092d832a694a0c77a88/profile.jpg",
  "lastLogin": "2026-05-10T08:30:00",
  "status": "ACTIVE",
  "createdAt": "2025-10-07T10:00:00",
  "updatedAt": "2026-05-10T08:30:00"
}
```

### Índices

```javascript
db.users.createIndex({ "email": 1 }, { unique: true })
db.users.createIndex({ "username": 1 }, { unique: true })
db.users.createIndex({ "documentNumber": 1 }, { unique: true })
db.users.createIndex({ "firebaseId": 1 }, { unique: true, sparse: true })
db.users.createIndex({ "status": 1 })
db.users.createIndex({ "role": 1 })
```

### Roles disponibles

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Administrador del sistema |
| `COORDINADOR` | Coordinador de área |
| `VOLUNTARIO` | Voluntario |

### Estados disponibles

| Estado | Descripción |
|--------|-------------|
| `ACTIVE` | Usuario activo |
| `INACTIVE` | Usuario desactivado |

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.x | Framework base |
| Spring WebFlux | 3.5.x | Programación reactiva |
| Spring Data MongoDB Reactive | 3.5.x | Acceso reactivo a MongoDB |
| MongoDB Atlas | 7.0 | Base de datos en la nube |
| Lombok | Latest | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.8 | Documentación Swagger |
| Maven | 3.x | Gestión de dependencias |

---

## 🚀 Ejecución del Proyecto

### Prerrequisitos
- Java 17+
- Maven 3.x
- Docker Desktop (opcional)
- Cuenta en MongoDB Atlas

### Correr localmente

```bash
# Clonar el repositorio
git clone https://github.com/vallegrande/vg-ms-users.git
cd vg-ms-users

# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run
```

---

## 📖 Documentación Swagger

| Recurso | URL |
|---------|-----|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| OpenAPI JSON | http://localhost:8081/api-docs |

---

## 👥 Equipo

**Institución:** Vallegrande - SIGRC  
**Versión:** 1.0.0  
**Equipo:** Cáritas