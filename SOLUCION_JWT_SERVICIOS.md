# Solución: JWT Token no encontrado en peticiones

## 🔍 Problema Identificado

El error `DEBUG c.a.f.s.JwtRequestFilter - JWT Token no encontrado o no comienza con Bearer` ocurre porque los servicios del frontend están usando `axios` directamente en lugar de `axiosInstance` que incluye el interceptor JWT.

## ✅ Servicios Ya Actualizados

- ✅ `UsuarioService.jsx`
- ✅ `AdminAfpService.jsx`
- ✅ `DashboardService.jsx`
- ✅ `PensionesService.jsx`

## ⚠️ Servicios Pendientes de Actualizar

Debes actualizar manualmente los siguientes servicios:

### 1. AdminUsuarioService.jsx
**Ubicación:** `src/service/admin/AdminUsuarioService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/admin/usuarios";
return axios.get(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/admin/usuarios";
return axiosInstance.get(...);
```

### 2. PerfilService.jsx
**Ubicación:** `src/service/user/PerfilService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/perfil";
return axios.get(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/perfil";
return axiosInstance.get(...);
```

### 3. SegurosService.jsx
**Ubicación:** `src/service/user/SegurosService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/seguros";
return axios.post(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/seguros";
return axiosInstance.post(...);
```

### 4. FinancieroService.jsx
**Ubicación:** `src/service/user/FinancieroService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/financiero";
return axios.get(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/financiero";
return axiosInstance.get(...);
```

### 5. ComparadorService.jsx
**Ubicación:** `src/service/user/ComparadorService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/comparador";
return axios.post(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/comparador";
return axiosInstance.post(...);
```

### 6. ChatBotService.jsx
**Ubicación:** `src/service/user/ChatBotService.jsx`

**Cambios necesarios:**
```javascript
// ANTES:
import axios from 'axios';
const API_URL = "http://localhost:8090/api/chatbot";
return axios.post(...);

// DESPUÉS:
import axiosInstance from '../../config/axiosConfig';
const API_URL = "/chatbot";
return axiosInstance.post(...);
```

## 📝 Pasos para Actualizar Cada Servicio

1. **Abrir el archivo del servicio**
2. **Cambiar el import:**
   ```javascript
   // Reemplazar esta línea:
   import axios from 'axios';
   
   // Por esta:
   import axiosInstance from '../../config/axiosConfig';
   ```

3. **Cambiar la URL base:**
   ```javascript
   // Reemplazar:
   const API_URL = "http://localhost:8090/api/NOMBRE";
   
   // Por:
   const API_URL = "/NOMBRE";
   ```

4. **Reemplazar todas las llamadas `axios.` por `axiosInstance.`:**
   - Buscar: `axios.get(`
   - Reemplazar por: `axiosInstance.get(`
   - Buscar: `axios.post(`
   - Reemplazar por: `axiosInstance.post(`
   - Buscar: `axios.put(`
   - Reemplazar por: `axiosInstance.put(`
   - Buscar: `axios.delete(`
   - Reemplazar por: `axiosInstance.delete(`
   - Buscar: `axios.patch(`
   - Reemplazar por: `axiosInstance.patch(`

5. **Guardar el archivo**

## 🔧 Ejemplo Completo de Transformación

### ANTES:
```javascript
import axios from 'axios';

const API_URL = "http://localhost:8090/api/perfil";

const PerfilService = {
    obtenerPerfil(idUsuario) {
        return axios.get(`${API_URL}/${idUsuario}`);
    },
    
    actualizarPerfil(idUsuario, datos) {
        return axios.put(`${API_URL}/${idUsuario}`, datos);
    }
};

export default PerfilService;
```

### DESPUÉS:
```javascript
import axiosInstance from '../../config/axiosConfig';

const API_URL = "/perfil";

const PerfilService = {
    obtenerPerfil(idUsuario) {
        return axiosInstance.get(`${API_URL}/${idUsuario}`);
    },
    
    actualizarPerfil(idUsuario, datos) {
        return axiosInstance.put(`${API_URL}/${idUsuario}`, datos);
    }
};

export default PerfilService;
```

## 🎯 ¿Por qué esto soluciona el problema?

1. **axiosInstance** tiene configurado un interceptor que automáticamente:
   - Agrega el header `Authorization: Bearer <token>` a TODAS las peticiones
   - Lee el token desde `localStorage`
   - Maneja errores 401 (token expirado) automáticamente

2. **axios** normal NO tiene estos interceptores, por eso las peticiones llegan sin el token JWT

## 🚀 Después de Actualizar

Una vez actualizados todos los servicios:

1. **Reinicia el frontend:**
   ```bash
   cd frontend/sumaq-seguros
   npm run dev
   ```

2. **Prueba el flujo completo:**
   - Login con DNI: `12345678` y clave: `1234`
   - Navega a diferentes secciones (AFP, Dashboard, Pensiones, etc.)
   - Verifica en la consola del navegador (F12) que las peticiones incluyen el header `Authorization`

3. **Verifica en los logs del backend:**
   - Deberías ver: `DEBUG c.a.f.s.JwtRequestFilter - JWT Token válido para usuario: ...`
   - Ya NO deberías ver: `DEBUG c.a.f.s.JwtRequestFilter - JWT Token no encontrado...`

## 📊 Checklist de Verificación

- [ ] AdminUsuarioService.jsx actualizado
- [ ] PerfilService.jsx actualizado
- [ ] SegurosService.jsx actualizado
- [ ] FinancieroService.jsx actualizado
- [ ] ComparadorService.jsx actualizado
- [ ] ChatBotService.jsx actualizado
- [ ] Frontend reiniciado
- [ ] Login funciona correctamente
- [ ] Navegación entre secciones funciona sin errores 401
- [ ] Logs del backend muestran tokens válidos

---

**Nota:** El archivo `axiosConfig.js` ya está creado y configurado correctamente con los interceptores JWT. Solo necesitas que todos los servicios lo usen en lugar de `axios` directo.
