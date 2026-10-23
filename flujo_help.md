# GUÍA OPERACIONAL Y MANUAL DE COMANDOS POR EMAIL (8 CASOS DE USO)
## SISTEMA DE GESTIÓN DE PARRILLA DE TELEVISIÓN - FICCT UAGRM (GRUPO 12 SC)

**Servidor POP3 / SMTP:** `mail.tecnoweb.org.bo`  
**Correo del Sistema:** `grupo12sc@tecnoweb.org.bo`  
*Escriba el comando directamente en el **ASUNTO (Subject)** del correo electrónico.*

---

## 🎬 FLUJO LÓGICO DE OPERACIÓN EN TELEVISIÓN (PASO A PASO)
El sistema implementa el flujo estándar de un canal de televisión broadcast:

```
[PASO 1: Usuarios y Roles]  ──────► [PASO 2: Catálogo de Programas]
            │                                      │
            ▼                                      ▼
[PASO 4: Confeccionar Parrilla] ◄── [PASO 3: Ingesta de Video (MAM)]
            │
            ▼
[PASO 5: Control Calidad (QC)] ──► [PASO 6: Pauta de Comerciales]
            │
            ▼
[PASO 7: Conciliar Emisión Real (As-Run Log)] ──► [PASO 8: Reportes y Métricas]
```

---

## 🚀 SECUENCIA DE EJECUCIÓN PASO A PASO (PROBADA Y VERIFICADA AL 100%)

Siga esta secuencia cronológica para probar el ciclo de vida completo de un contenido televisivo:

### Paso 1: Configurar Personal Operativo (CU1)
1. **Crear usuario:**
   ```
   usuario agregar Marcos,Gomez,marcos@canal.com,123,Programador
   ```
2. **Consultar usuario:**
   ```
   usuario consultar 1
   ```
3. **Modificar datos del usuario:**
   ```
   usuario modificar 1,Admin,Master,admin@canal.com,admin123,Administrador,ACTIVO
   ```
4. **Ver roles del canal:**
   ```
   rol listar
   ```
5. **Crear nuevo rol operativo:**
   ```
   rol agregar MasterControl,Operador de transmision y emision
   ```
6. **Asignar rol a usuario:**
   ```
   rol asignar 1,1
   ```

---

### Paso 2: Configurar Catálogo y Categorías (CU2)
7. **Ver categorías existentes:**
   ```
   categoria listar
   ```
8. **Crear nueva categoría:**
   ```
   categoria agregar Documentales,Cultura naturaleza y ciencia
   ```
9. **Registrar nuevo programa en catálogo:**
   ```
   programa agregar PROG-99,Planeta Salvaje,45
   ```
   *(Código: PROG-99 | Título: Planeta Salvaje | Duración estándar: 45 min)*
10. **Consultar programa registrado:**
    ```
    programa consultar 1
    ```
11. **Modificar programa:**
    ```
    programa modificar 1,PROG-01,Noticiero Central 2,Edicion Central,1,ATP,60,NACIONAL,ACTIVO
    ```

---

### Paso 3: Ingestar Archivos de Video en MAM (CU4)
12. **Registrar clip de video digital asociado al programa:**
    ```
    material agregar 1,CLIP-99,Reportaje Selva Amazonica,1800
    ```
    *(id_programa: 1 | código: CLIP-99 | título: Reportaje Selva Amazonica | duración: 1800 segundos / 30 min)*
13. **Listar materiales en storage:**
    ```
    material listar
    ```
14. **Listar clips de un programa específico:**
    ```
    material listar_programa 1
    ```
15. **Aprobar estado técnico del material:**
    ```
    material estado 1,LISTO
    ```

---

### Paso 4: Armar Parrilla y Playlist Diario (CU3)
16. **Crear parrilla de programación para una fecha:**
    ```
    parrilla crear 2026-11-20
    ```
17. **Listar parrillas del canal:**
    ```
    parrilla listar
    ```
18. **Consultar detalle de una parrilla:**
    ```
    parrilla consultar 1
    ```
19. **Insertar bloque horario (Item de Parrilla) al playlist:**
    ```
    item agregar 1,1,14:00,15:00
    ```
    *(id_parrilla: 1 | id_programa: 1 | hora_inicio: 14:00 | hora_fin: 15:00)*
20. **Listar bloques del playlist:**
    ```
    item listar
    ```
    *(o `item listar 1` para ver solo los bloques de la parrilla 1)*
21. **Modificar horarios de un bloque:**
    ```
    item modificar 1,14:15,15:15,VIVO,PROGRAMADO
    ```
22. **Aprobar oficialmente la parrilla:**
    ```
    parrilla estado 1,APROBADA
    ```
23. **Consultar rundown del día actual:**
    ```
    parrilla hoy
    ```
24. **Consultar qué programa está al aire en este instante:**
    ```
    parrilla al_aire
    ```

---

### Paso 5: Control de Calidad Previo a Emisión - QC (CU5)
25. **Ver bloques pendientes de inspección técnica:**
    ```
    validacion pendientes
    ```
26. **Aprobar bloque para salida al aire:**
    ```
    validacion aprobar 1
    ```
    *(Valida audio broadcast -24 LKFS/dB y aspect ratio 16:9)*
27. **Consultar resultado de QC de un bloque:**
    ```
    validacion consultar 1
    ```
28. *(Opcional en caso de fallo técnico) Rechazar bloque:*
    ```
    validacion rechazar 1,Desfase en pista de audio secundario
    ```

---

### Paso 6: Comercialización y Pauta Publicitaria (CU6)
29. **Registrar empresa anunciante:**
    ```
    cliente agregar Cerveceria Paceña,1099887766
    ```
30. **Listar clientes anunciantes:**
    ```
    cliente listar
    ```
31. **Registrar spot publicitario del cliente:**
    ```
    spot agregar 1,SPOT-99,Promo Mundial 2026,30
    ```
    *(id_cliente: 1 | código: SPOT-99 | título: Promo Mundial 2026 | duración: 30 seg)*
32. **Listar catálogo de spots:**
    ```
    spot listar
    ```
33. **Insertar spot en la tanda comercial de un bloque:**
    ```
    pauta agregar 1,1,CORTE_1,1
    ```
    *(id_item_parrilla: 1 | id_spot: 1 | tanda: CORTE_1 | orden de salida: 1)*
34. **Listar pautas programadas:**
    ```
    pauta listar
    ```
    *(o `pauta listar 1` para ver solo los comerciales del bloque 1)*
35. **Confirmar salida al aire de la pauta:**
    ```
    pauta estado 1,EMITIDO
    ```

---

### Paso 7: Conciliación de Emisión Real y Auditoría (CU7)
36. **Conciliar bloque emitido con la parrilla oficial:**
    ```
    emision conciliar 1
    ```
37. **Ver bitácora de salida al aire (As-Run Log del playout):**
    ```
    emision listar
    ```
38. **Verificar si hubo desfases horarios respecto a lo planificado:**
    ```
    emision discrepancias
    ```
39. **Consultar emisión en curso / último evento al aire:**
    ```
    emision al_aire
    ```
40. **Ver bitácora de auditoría del sistema (quién hizo qué y cuándo):**
    ```
    auditoria listar
    ```
41. **Ver auditoría por usuario:**
    ```
    auditoria consultar 1
    ```

---

### Paso 8: Reportes Estadísticos y Gerenciales (CU8)
42. **Reporte Estadístico Consolidado:**
    ```
    reporte general
    ```
    *(Totales de programas, horas emitidas, porcentaje de ocupación, spots y tasa de cumplimiento)*
43. **Reporte de Programación y Parrilla:**
    ```
    reporte parrilla
    ```
44. **Reporte de Catálogo por Categorías:**
    ```
    reporte programas
    ```
45. **Reporte de Certificación de Pautas Comerciales:**
    ```
    reporte pautas
    ```
    *(Detalle de anuncios emitidos por anunciante y segundos totales al aire para facturación)*
46. **Reporte de Cumplimiento Técnico:**
    ```
    reporte cumplimiento
    ```

---

## 📋 CATÁLOGO COMPLETO DE COMANDOS CRUD (REFERENCIA RÁPIDA)

### CU1. Gestión de Usuarios y Roles
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CREATE** | `usuario agregar Nombre,Apellidos,email@canal.com,123,Programador` | Crea usuario con rol |
| **READ (Todos)** | `usuario listar` | Lista todos los usuarios |
| **READ (Uno)** | `usuario consultar 1` | Muestra datos del usuario #1 |
| **UPDATE** | `usuario modificar 1,Nombre,Apellidos,email@canal.com,123,Programador,ACTIVO` | Actualiza usuario |
| **DELETE** | `usuario eliminar 4` | Elimina usuario por ID |
| **ROLES LIST** | `rol listar` | Lista roles del sistema |
| **ROL ADD** | `rol agregar MasterControl,Operador de transmision` | Crea nuevo rol |
| **ROL ASIGN** | `rol asignar 1,2` | Asigna rol #2 a usuario #1 |

### CU2. Catálogo de Programas
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CREATE** | `programa agregar PROG-10,MasterChef Bolivia,45` | Código, título y minutos |
| **READ (Todos)** | `programa listar` | Catálogo completo |
| **READ (Uno)** | `programa consultar 1` | Ficha técnica del programa |
| **UPDATE** | `programa modificar 1,PROG-01,Noticiero,Edicion noche,1,ATP,60,NACIONAL,ACTIVO` | Actualización completa |
| **DELETE** | `programa eliminar 3` | Elimina programa por ID |
| **CATEGORÍAS**| `categoria listar` \| `categoria agregar Series,Series y unitarios` | Gestión de géneros |

### CU3. Confección y Control de Parrilla (Playlist)
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CREATE** | `parrilla crear 2026-11-20` | Genera grilla para una fecha |
| **READ** | `parrilla listar` \| `parrilla consultar 1` | Lista y detalle de parrilla |
| **AL AIRE** | `parrilla al_aire` (o `item al_aire`) | Programa emitiéndose ahora |
| **HOY** | `parrilla hoy` (o `item hoy`) | Grilla del día en curso |
| **UPDATE** | `parrilla estado 1,APROBADA` | Cambia estado (BORRADOR, APROBADA, etc.) |
| **DELETE** | `parrilla eliminar 2` | Elimina parrilla |
| **ITEM ADD** | `item agregar 1,1,14:00,15:00` | Asigna programa a bloque de parrilla |
| **ITEM LIST** | `item listar` \| `item listar 1` | Todos o por parrilla |
| **ITEM MOD** | `item modificar 1,14:15,15:15,VIVO,PROGRAMADO` | Cambia horarios y tipo |
| **ITEM DEL** | `item eliminar 4` | Remueve bloque horario |

### CU4. Materiales de Video (Storage MAM)
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CREATE** | `material agregar 1,CLIP-05,Reportaje Especial,1800` | Ingesta clip con duración en seg |
| **READ** | `material listar` \| `material consultar 1` | Clips en storage |
| **POR PROG** | `material listar_programa 1` | Clips del programa #1 |
| **UPDATE** | `material estado 1,LISTO` | Cambia estado (INGESTADO, LISTO, etc.) |
| **DELETE** | `material eliminar 4` | Descarta material |

### CU5. Control de Calidad de Emisión (QC)
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **PENDIENTES** | `validacion pendientes` | Bloques que esperan revisión QC |
| **APROBAR** | `validacion aprobar 1` | Aprueba salida al aire |
| **RECHAZAR** | `validacion rechazar 1,Audio desfasado` | Rechaza con motivo |
| **READ** | `validacion listar` \| `validacion consultar 1` | Historial de inspecciones |

### CU6. Pauta Publicitaria y Comerciales
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CLIENTES** | `cliente listar` \| `cliente agregar Entel,1020304050` | Directorio de anunciantes |
| **SPOTS** | `spot listar` \| `spot agregar 1,SPOT-03,Promo Verano,30` | Spots del anunciante con duración |
| **PAUTA ADD** | `pauta agregar 1,1,CORTE_1,1` | Inserta spot en tanda comercial |
| **PAUTA LIST**| `pauta listar` \| `pauta listar 1` | Pautas activas |
| **PAUTA EST** | `pauta estado 1,EMITIDO` | Certifica emisión del spot |
| **PAUTA DEL** | `pauta eliminar 2` | Cancela pauta |

### CU7. Bitácora de Emisión (As-Run Log) y Auditoría
| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CONCILIAR** | `emision conciliar 1` | Concilia bloque con la parrilla |
| **AS-RUN LOG** | `emision listar` \| `emision al_aire` | Bitácora de emisión real |
| **DESFASES** | `emision discrepancias` | Desviaciones de tiempo al aire |
| **AUDITORÍA** | `auditoria listar` \| `auditoria consultar 1` | Trazabilidad de operaciones |

### CU8. Reportes y Estadísticas
| Comando Exacto | Qué Información Muestra |
| :--- | :--- |
| `reporte general` | Balance general: total programas, minutos al aire, pautas y cumplimiento |
| `reporte parrilla` | Estado de las parrillas oficiales programadas |
| `reporte programas`| Distribución del catálogo por categorías y procedencia |
| `reporte pautas` | Certificación de spots emitidos por anunciante y segundos totales |
| `reporte cumplimiento` | Indicador de puntualidad y exactitud en playout |

---

## 🛠️ SCRIPTS DE PRUEBA Y EJECUCIÓN INCLUIDOS

| Archivo | Función |
| :--- | :--- |
| `run-consola.bat` | Compila y ejecuta el servidor localmente en Windows por consola. |
| `run-tests.bat` | Ejecuta la suite completa de 4 pruebas (errores sintácticos, POP3, comandos rápidos y ciclo de vida E2E). |
| `docker-start.bat` | Levanta el servidor en contenedor Docker oficial (`parrilla-tv-server`). |
| `docker-stop.bat` | Detiene y remueve el contenedor Docker. |
