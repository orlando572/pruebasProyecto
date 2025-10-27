# Implementación de JWT en Sumaq Seguros

## 📋 Resumen

Se ha implementado autenticación JWT (JSON Web Token) en el proyecto fullstack Sumaq Seguros para mejorar la seguridad y gestión de sesiones.

## 🔧 Cambios Realizados

### Backend (Spring Boot)

#### 1. Dependencias Agregadas (`pom.xml`)
- `jjwt-api` 0.11.5
- `jjwt-impl` 0.11.5
- `jjwt-jackson` 0.11.5
- `spring-boot-starter-security`

#### 2. Nuevas Clases Creadas

**DTOs:**
- `LoginRequest.java` - DTO para recibir credenciales de login
- `LoginResponse.java` - DTO para respuesta con token y usuario

**Seguridad:**
- `JwtUtil.java` - Utilidad para generar y validar tokens JWT
  - Genera tokens con validez de 24 horas
  - Incluye DNI, ID de usuario e ID de rol en el token
  - Métodos para extraer información del token
  
- `JwtRequestFilter.java` - Filtro para interceptar requests y validar JWT
  - Extrae el token del header `Authorization: Bearer <token>`
  - Agrega información del usuario al request
  
- `SecurityConfig.java` - Configuración de Spring Security
  - Endpoints públicos: `/api/usuario/login`, `/api/usuario/registrarUsuario`, `/api/usuario/roles`, `/api/usuario/afps`
  - Todos los demás endpoints requieren autenticación
  - Configuración CORS para localhost:5173 y localhost:3000

#### 3. Controlador Actualizado

**`UsuarioController.java`:**
- Método `login()` actualizado para generar y retornar JWT
- Respuesta incluye: `{ token, usuario, mensaje }`

### Frontend (React + Vite)

#### 1. Nuevos Servicios

**`authService.js`:**
- Servicio centralizado para autenticación
- Métodos: `login()`, `register()`, `logout()`, `getCurrentUser()`, `getToken()`, `isAuthenticated()`, `isAdmin()`
- Interceptores Axios para:
  - Agregar token JWT a todas las peticiones
  - Manejar errores 401 (token inválido/expirado)

**`axiosConfig.js`:**
- Configuración global de Axios
- Interceptores para agregar JWT automáticamente
- Redirección a login si token expira

#### 2. Context Actualizado

**`AuthContext.jsx`:**
- Maneja estado de autenticación con JWT
- Almacena token y usuario
- Carga automática desde localStorage al iniciar

#### 3. Página de Login Actualizada

**`Login.jsx`:**
- Usa `authService` para login
- Guarda token automáticamente
- Redirección según rol (admin/usuario)

## 🔐 Flujo de Autenticación

### Login
1. Usuario ingresa DNI y clave SOL
2. Frontend envía credenciales a `/api/usuario/login`
3. Backend valida credenciales
4. Backend genera token JWT (válido 24 horas)
5. Backend retorna: `{ token, usuario, mensaje }`
6. Frontend guarda token en localStorage
7. Frontend guarda usuario en context
8. Redirección según rol

### Requests Autenticados
1. Frontend incluye automáticamente header: `Authorization: Bearer <token>`
2. Backend valida token en cada request
3. Si token válido: procesa request
4. Si token inválido/expirado: retorna 401
5. Frontend detecta 401 y redirige a login

## 🚀 Cómo Usar

### Iniciar Backend
```bash
cd backend/financiera
mvn clean install
mvn spring-boot:run
```

### Iniciar Frontend
```bash
cd frontend/sumaq-seguros
npm install
npm run dev
```

## 🔑 Credenciales de Prueba

**Usuario Admin:**
- DNI: `12345678`
- Clave SOL: `1234`
- Rol: Administrador (ID: 1)

**Usuario Regular:**
- DNI: `60967428`
- Clave SOL: `1234`
- Rol: Usuario (ID: 2)

## 📝 Notas Importantes

### Seguridad
- **Clave secreta JWT:** Actualmente en código. En producción debe estar en variables de entorno.
- **Contraseñas:** Almacenadas en texto plano en BD. Se recomienda implementar bcrypt/hash.
- **HTTPS:** En producción, usar HTTPS para todas las comunicaciones.

### Token JWT
- **Validez:** 24 horas
- **Contenido:** DNI, ID usuario, ID rol
- **Algoritmo:** HS256
- **Header:** `Authorization: Bearer <token>`

### Endpoints Públicos (sin autenticación)
- `POST /api/usuario/login`
- `POST /api/usuario/registrarUsuario`
- `GET /api/usuario/roles`
- `GET /api/usuario/afps`

### Endpoints Protegidos (requieren JWT)
- Todos los demás endpoints del sistema

## 🐛 Solución de Problemas

### Error: "Token inválido"
- Verificar que el token esté en localStorage
- Verificar formato del header: `Bearer <token>`
- Token puede haber expirado (24 horas)

### Error: "CORS"
- Verificar que frontend esté en puerto 5173 o 3000
- Verificar configuración CORS en `SecurityConfig.java`

### Error: "401 Unauthorized"
- Token expirado o inválido
- Hacer login nuevamente

## 📊 Estructura de Archivos

### Backend
```
backend/financiera/src/main/java/com/app/financiera/
├── dto/
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtRequestFilter.java
│   └── SecurityConfig.java
└── controller/
    └── UsuarioController.java (actualizado)
```

### Frontend
```
frontend/sumaq-seguros/src/
├── services/
│   └── authService.js
├── config/
│   └── axiosConfig.js
├── context/
│   └── AuthContext.jsx (actualizado)
└── pages/user/
    └── Login.jsx (actualizado)
```

## ✅ Checklist de Implementación

- [x] Dependencias JWT agregadas al pom.xml
- [x] Clase JwtUtil creada
- [x] Filtro JWT creado
- [x] Configuración de seguridad Spring
- [x] DTOs de login creados
- [x] UsuarioController actualizado
- [x] authService creado en frontend
- [x] axiosConfig creado
- [x] AuthContext actualizado
- [x] Login.jsx actualizado
- [x] Interceptores Axios configurados

## 🎯 Próximos Pasos Recomendados

1. **Encriptar contraseñas:** Implementar BCrypt en backend
2. **Refresh tokens:** Implementar sistema de refresh tokens
3. **Variables de entorno:** Mover clave JWT a .env
4. **Rate limiting:** Limitar intentos de login
5. **Logs de auditoría:** Registrar accesos y cambios
6. **Tests:** Crear tests unitarios y de integración

---

**Fecha de implementación:** 26 de Octubre, 2025
**Versión:** 1.0
**Estado:** ✅ Completado y funcional
