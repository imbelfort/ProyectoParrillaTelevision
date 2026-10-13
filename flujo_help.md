# GUÍA OPERACIONAL Y MANUAL DE COMANDOS POR EMAIL (8 CASOS DE USO)
## SISTEMA DE GESTIÓN DE PARRILLA DE TELEVISIÓN - FICCT UAGRM (GRUPO 12 SC)

**Correo del Sistema:** `grupo12sc@tecnoweb.org.bo`  
*Escriba el comando directamente en el **ASUNTO (Subject)** del correo.*

---

## 🎬 FLUJO LÓGICO DE OPERACIÓN EN TELEVISIÓN (PASO A PASO)
El sistema sigue la secuencia natural de una estación de televisión:

```
[1. Configurar Usuarios/Roles]  ──► [2. Crear Catálogo de Programas]
               │                                      │
               ▼                                      ▼
[4. Confeccionar Parrilla (Playlist)] ◄── [3. Ingestar Videos (MAM)]
               │
               ▼
[5. Control de Calidad (QC)] ──► [6. Insertar Pauta / Comerciales]
               │
               ▼
[7. Emisión al Aire (As-Run Log) y Auditoría] ──► [8. Reportes Finales]
```

---

## 📋 REVISIÓN DEL CRUD COMPLETO POR CASO DE USO

### CU1. Gestión de Usuarios y Roles
*(Define quién opera el sistema: Administrador, Programador, Encargado de Contenidos/Operador)*
- **CREATE:** `usuario agregar Carlos,Perez,carlos@canal.com,123,Programador`
- **READ (Todos):** `usuario listar`
- **READ (Uno):** `usuario consultar 1`
- **UPDATE:** `usuario modificar 1,Carlos,Perez,carlos@canal.com,123,Programador,ACTIVO`
- **DELETE:** `usuario eliminar 4`
- **ROLES:** `rol listar` | `rol agregar MasterControl,Operador de emision`

---

### CU2. Gestión de Catálogo de Programas
*(Registra los programas producidos o adquiridos por el canal)*
- **CREATE:** `programa agregar PROG-10,MasterChef Bolivia,45`
- **READ (Todos):** `programa listar`
- **READ (Uno):** `programa consultar 1`
- **UPDATE:** `programa modificar 1,PROG-01,Noticiero Central,Edicion noche,1,ATP,60,NACIONAL,ACTIVO`
- **DELETE:** `programa eliminar 3`
- **CATEGORÍAS:** `categoria listar` | `categoria agregar Series,Series y unitarios`

---

### CU3. Gestión de Confeccionar y Controlar la Parrilla de Programación
*(El Playlist Principal diario: arma la grilla de emisión y asigna bloques horarios)*
- **CREATE (Parrilla):** `parrilla crear 2026-10-15`
- **READ (Parrillas):** `parrilla listar` | `parrilla consultar 1`
- **READ (En vivo / Al Aire):** `parrilla al_aire` *(o `item al_aire`: qué programa está al aire ahora)*
- **READ (Rundown de Hoy):** `parrilla hoy` *(bloques del día actual)*
- **UPDATE (Estado):** `parrilla estado 1,APROBADA`
- **DELETE (Parrilla):** `parrilla eliminar 2`
- **BLOQUES / PLAYLIST:**
  - **CREATE (Bloque):** `item agregar 1,1,14:00,15:00`
  - **READ (Bloques):** `item listar` *(o `item listar 1` para una parrilla específica)*
  - **READ (Uno):** `item consultar 1`
  - **UPDATE (Horarios):** `item modificar 1,14:00,15:00,VIVO,PROGRAMADO`
  - **DELETE (Bloque):** `item eliminar 4`

---

### CU4. Gestión de Gestionar Materiales de Video (MAM)
*(Ingesta y control de los archivos de video reales con duración en segundos y formato)*
- **CREATE:** `material agregar 1,CLIP-05,Reportaje Especial,1800`
- **READ (Todos):** `material listar`
- **READ (Uno):** `material consultar 1`
- **READ (Por Programa):** `material listar_programa 1`
- **UPDATE (Estado MAM):** `material estado 1,LISTO`
- **DELETE:** `material eliminar 4`

---

### CU5. Gestión de Validar Estado de Emisión (Quality Control - QC)
*(Revisión técnica previa a la salida al aire: valida audio EBU R128 y video)*
- **READ (Aprobaciones):** `validacion listar`
- **READ (Pendientes de QC):** `validacion pendientes`
- **READ (Por Bloque):** `validacion consultar 1`
- **UPDATE (Aprobar Bloque):** `validacion aprobar 1`
- **UPDATE (Rechazar Bloque):** `validacion rechazar 1,Audio desfasado`

---

### CU6. Gestión de Pauta Publicitaria e Insertar Spots Comerciales
*(El Playlist de Cortes Comerciales del Playout: inserta tandas y spots en los bloques de parrilla)*
- **CLIENTES / ANUNCIANTES:** `cliente listar` | `cliente consultar 1` | `cliente agregar Entel,1020304050`
- **SPOTS PUBLICITARIOS:** `spot listar` | `spot consultar 1` | `spot agregar 1,SPOT-03,Promo Verano,30`
- **PAUTAS / INSERCIÓN DE CORTES (Sin costos ni tarifas):**
  - **CREATE (Insertar spot en tanda):** `pauta agregar 1,1,CORTE_1,1` *(bloque 1, spot 1, tanda CORTE_1, orden 1)*
  - **READ (Todas las pautas):** `pauta listar`
  - **READ (Por Bloque de Parrilla):** `pauta listar 1`
  - **READ (Una Pauta Específica):** `pauta consultar 1`
  - **UPDATE (Estado de salida):** `pauta estado 1,EMITIDO`
  - **DELETE:** `pauta eliminar 2`

---

### CU7. Gestión de Conciliar Emisión Real, Registrar Auditoría
*(Bitácora de salida al aire As-Run Log y registro de quién realizó cada acción)*
- **AS-RUN LOG (Emisión Real de Playout):**
  - **READ (Qué se está emitiendo al aire):** `emision al_aire` *(o `emision actual`)*
  - **READ (Bitácora completa):** `emision listar`
  - **READ (Desfases técnicos):** `emision discrepancias`
  - **READ (Uno):** `emision consultar 1`
  - **CREATE / CONCILIAR:** `emision conciliar 1` *(concilia automáticamente con la parrilla oficial)*
- **LOG DE AUDITORÍA:**
  - **READ (Todos los logs):** `auditoria listar` *(o `log listar`)*
  - **READ (Por Usuario):** `auditoria consultar 1`

---

### CU8. Reportes y Estadísticas
*(Consolidación y métricas técnicas de emisión al aire y rendimiento de parrilla)*
- **Reporte Estadístico Consolidado:** `reporte general`
- **Reporte de Programación y Parrilla:** `reporte parrilla`
- **Reporte de Catálogo por Categorías:** `reporte programas`
- **Reporte de Certificación de Pautas:** `reporte pautas` *(tiempo total al aire y spots emitidos por anunciante)*
- **Reporte de Cumplimiento de Emisión:** `reporte cumplimiento`

---

## ⚡ RESUMEN RÁPIDO DE EVALUACIÓN (1 LÍNEA POR CASO)
| Caso de Uso | Comando | Qué Hace |
| :--- | :--- | :--- |
| **CU1** | `usuario listar` | Lista los usuarios del sistema de televisión |
| **CU2** | `programa listar` | Lista el catálogo de programas |
| **CU3** | `parrilla listar` | Lista las parrillas oficiales programadas |
| **CU4** | `material listar` | Lista los clips de video en el storage MAM |
| **CU5** | `validacion pendientes` | Muestra bloques pendientes de control técnico (QC) |
| **CU6** | `pauta listar` | Muestra la pauta de comerciales en tandas |
| **CU7** | `emision listar` | Muestra el As-Run Log de lo emitido al aire |
| **CU8** | `reporte general` | Muestra el balance estadístico consolidado |
