hizo # 📡 VALIDACIÓN DE LÓGICA DE NEGOCIO POR CASOS DE USO Y CATÁLOGO DE CÓDIGOS DE ERROR EXPLÍCITOS
## Sistema de Gestión y Automatización de Parrilla de Televisión (Playout Automation)
**Institución:** FICCT - UAGRM | **Grupo:** 12 SC  
**Mecanismo de Interacción:** Comandos vía Asunto (Subject) de Correo Electrónico (POP3/SMTP)

---

## 📋 1. INTRODUCCIÓN Y ARQUITECTURA GENERAL

El sistema modela fielmente la operativa técnica de una estación de televisión moderna bajo la arquitectura **BMS (Broadcast Management System) / Playout Automation**. 

La interacción se realiza íntegramente por correo electrónico:
1. El usuario envía un comando en el **Asunto (Subject)** del correo.
2. El hilo receptor (`MailVerificationThread`) procesa el mensaje a través del analizador léxico (`Analex`) y el motor de sintaxis (`Interpreter`).
3. El sistema valida la **estructura (entidad + acción + parámetros)** y la lógica de negocio del caso de uso.
4. El sistema responde con un correo en formato HTML enriquecido con diseño Broadcast:
   - **En caso de éxito:** Tablas de playout formateadas o confirmaciones de operación (`generarTabla` / `generarMensajeExito`).
   - **En caso de error:** Panel de alerta con badge del **Código de Error Explícito** (`[ERR-xxx-yyy]`), diagnóstico de la falla y la corrección exacta esperada (`generarMensajeErrorDetallado`).

---

## 🎬 2. VALIDACIÓN DE LA LÓGICA DE NEGOCIO POR CASOS DE USO (CU1 A CU8)

El flujo del canal sigue una secuencia lógica estricta dividida en 8 Casos de Uso integrados:

```
[CU1: Usuarios y Roles] ──► [CU2: Catálogo Programas] ──► [CU4: Materiales MAM (Video)]
                                     │                                  │
                                     ▼                                  ▼
                          [CU3: Parrilla y Playlist] ◄── [CU5: Quality Control (QC)]
                                     │
                                     ▼
                          [CU6: Pauta Publicitaria / Spots]
                                     │
                                     ▼
                          [CU7: Emisión Real (As-Run Log) & Auditoría]
                                     │
                                     ▼
                          [CU8: Reportes Estadísticos y Certificación]
```

| Caso de Uso | Módulo / Función Operativa | Entidades del Sistema | Comandos Principales |
| :--- | :--- | :--- | :--- |
| **CU1** | **Gestión de Usuarios y Roles** | `usuario`, `rol` | `usuario listar`, `usuario agregar`, `rol listar` |
| **CU2** | **Catálogo de Programas y Categorías** | `programa`, `categoria` | `programa listar`, `programa agregar`, `categoria listar` |
| **CU3** | **Confeccionar y Controlar Parrilla (Playlist)** | `parrilla`, `item` | `parrilla crear`, `parrilla al_aire`, `item agregar` |
| **CU4** | **Gestión de Materiales de Video (MAM)** | `material` | `material listar`, `material agregar`, `material estado` |
| **CU5** | **Validar Estado de Emisión (Control Calidad QC)**| `validacion` | `validacion pendientes`, `validacion aprobar`, `rechazar` |
| **CU6** | **Playlist Comercial, Pauta y Spots** | `pauta`, `spot`, `cliente` | `cliente listar`, `spot agregar`, `pauta agregar` |
| **CU7** | **Conciliar Emisión Real (As-Run Log) & Auditoría**| `emision`, `auditoria` | `emision al_aire`, `emision conciliar`, `auditoria listar`|
| **CU8** | **Reportes Estadísticos y Métricas de Playout** | `reporte` | `reporte general`, `reporte parrilla`, `cumplimiento` |

---

### 🟢 CU1: Gestión de Usuarios y Roles
- **Propósito:** Control de acceso y trazabilidad de los operadores de la estación (Administrador, Programador de Emisión, Operador de Master Control).
- **Entidades BD:** `usuario`, `roles`, `usuario_roles`.
- **Lógica Validada:**
  - Solo usuarios activos pueden operar y figurar como responsables de parrillas o validaciones.
  - La contraseña, CI y correo electrónico son obligatorios y únicos en el sistema.
- **Comandos Clave:**
  - `usuario listar` | `usuario consultar <id>`
  - `usuario agregar <Nombre,Email,Password,Rol>`
  - `usuario modificar <id,Nombre,Apellidos,CI,Email,Telefono,Direccion,Password,Rol,Estado>`
  - `usuario eliminar <id>`
  - `rol listar` | `rol agregar <Nombre,Descripcion>` | `rol asignar <id_usuario,id_rol>`

---

### 🟢 CU2: Gestión de Catálogo de Programas
- **Propósito:** Registro y clasificación del inventario de programas propios y enlatados del canal.
- **Entidades BD:** `categoria_programa`, `programa`.
- **Lógica Validada:**
  - Cada programa posee un código alfanumérico único (`PROG-01`), duración estándar en minutos, categoría temática (Noticias, Deportes, Entretenimiento, etc.) y clasificación por edades (`ATP`, `TEEN_14`, `ADULTO_18`).
- **Comandos Clave:**
  - `programa listar` | `programa consultar <id>`
  - `programa agregar <codigo,titulo,duracion_min>`
  - `programa modificar <id,codigo,titulo,descripcion,id_cat,clasif,duracion,origen,estado>`
  - `programa eliminar <id>`
  - `categoria listar` | `categoria agregar <Nombre,Descripcion>`

---

### 🟢 CU3: Confección y Control de la Parrilla de Programación (Playout Playlist)
- **Propósito:** Arma la grilla de emisión por fecha (Parrilla) y programa los bloques horarios de transmisión (Items de Parrilla / Escaleta diaria).
- **Entidades BD:** `parrilla`, `item_parrilla`.
- **Lógica Validada:**
  - Una parrilla se crea en estado `BORRADOR` y avanza a `APROBADA` -> `PUBLICADA` -> `CERRADA`.
  - Cada bloque u orden horario (`item_parrilla`) vincula una parrilla con un programa, fijando `hora_inicio` y `hora_fin` (formato 24h `HH:mm`) y el tipo de emisión (`VIVO`, `GRABADO`, `REPETICION`).
  - Monitoreo en tiempo real: comando `al_aire` consulta el programa transmitido en el segundo exacto.
- **Comandos Clave:**
  - `parrilla listar` | `parrilla consultar <id>` | `parrilla al_aire` | `parrilla hoy`
  - `parrilla crear <YYYY-MM-DD>` | `parrilla estado <id_parrilla,ESTADO>` | `parrilla eliminar <id>`
  - `item listar [id_parrilla]` | `item consultar <id_item>` | `item al_aire` | `item hoy`
  - `item agregar <id_parrilla,id_programa,hora_inicio,hora_fin,[tipo_emision]>`
  - `item modificar <id_item,hora_inicio,hora_fin,tipo_emision,estado>` | `item eliminar <id_item>`

---

### 🟢 CU4: Gestión de Materiales de Video (MAM - Ingesta)
- **Propósito:** Ingesta técnica de los archivos digitales de video (clips/capítulos) asociados a cada programa.
- **Entidades BD:** `material_video`.
- **Lógica Validada:**
  - Control de duración exacta en segundos (`duracion_segundos`), formato (`MP4_H264`, `MXF_XDCAM_HD422`), resolución (`1080i59.94`, `720p`) y estado (`INGESTADO`, `EN_REVISION`, `LISTO`, `ARCHIVADO`).
  - Un bloque de playout solo debe emitir archivos que alcancen el estado `LISTO`.
- **Comandos Clave:**
  - `material listar` | `material consultar <id>` | `material listar_programa <id_programa>`
  - `material agregar <id_programa,codigo,titulo,duracion_seg>`
  - `material estado <id_material,ESTADO>` | `material eliminar <id>`

---

### 🟢 CU5: Validación de Estado de Emisión (Quality Control - QC)
- **Propósito:** Inspección técnica obligatoria antes de autorizar la emisión de un bloque al aire.
- **Entidades BD:** `validacion_emision`.
- **Lógica Validada:**
  - Verifica niveles de sonoridad según norma **EBU R128 (-24 LUFS)** y relación de aspecto (16:9).
  - Al aprobar un bloque (`validacion aprobar <id_item>`), el bloque de parrilla pasa a estado técnico `APROBADO_AIRE`.
  - Si se detectan fallas (`validacion rechazar <id_item,motivo>`), el bloque se suspende impidiendo la salida al aire.
- **Comandos Clave:**
  - `validacion listar` | `validacion pendientes` | `validacion consultar <id_item>`
  - `validacion aprobar <id_item>` | `validacion rechazar <id_item,motivo>`

---

### 🟢 CU6: Gestión de Pauta Publicitaria e Inserción de Spots Comerciales
- **Propósito:** Inserción de cortes comerciales y spots de anunciantes dentro de los programas de televisión.
- **Entidades BD:** `cliente_publicitario`, `spot_publicitario`, `pauta_comercial`.
- **Lógica Validada:**
  - Los clientes tienen razón social y NIT.
  - Los spots tienen vigencia temporal y duración exacta en segundos (ej. 30 segundos).
  - La pauta inserta un spot en un bloque horario (`id_item`), asignándole una tanda (`CORTE_1`, `CORTE_2`) y un orden correlativo de pase.
- **Comandos Clave:**
  - `cliente listar` | `cliente consultar <id>` | `cliente agregar <RazonSocial,NIT,[Contacto,Email,Telefono]>`
  - `spot listar` | `spot consultar <id>` | `spot agregar <id_cliente,codigo,titulo,duracion_seg>`
  - `pauta listar [id_item]` | `pauta consultar <id>`
  - `pauta agregar <id_item,id_spot,tanda,orden>` | `pauta estado <id_pauta,ESTADO>` | `pauta eliminar <id>`

---

### 🟢 CU7: Conciliación de Emisión Real (As-Run Log) y Auditoría
- **Propósito:** Registrar la bitácora legal de lo transmitido al aire, contrastarlo con lo programado y auditar eventos.
- **Entidades BD:** `emision_real_log`, `auditoria_sistema`.
- **Lógica Validada:**
  - El playout genera el log de salida real con hora exacta de inicio y fin al aire.
  - Calcula la **discrepancia en segundos** (`hora_real - hora_programada`).
  - La conciliación (`emision conciliar <id_item>`) certifica el cumplimiento contractual de transmisión.
  - Auditoría registra todas las operaciones críticas (INSERT, UPDATE, DELETE).
- **Comandos Clave:**
  - `emision listar` | `emision al_aire` | `emision discrepancias` | `emision conciliar <id_item>`
  - `auditoria listar` | `auditoria consultar <id_usuario>`

---

### 🟢 CU8: Reportes Estadísticos y Métricas de Playout
- **Propósito:** Certificación de transmisión, cumplimiento de pauta para facturación y análisis de parrilla.
- **Lógica Validada:**
  - Consolida indicadores del canal: programas por categoría, horas emitidas, cumplimiento publicitario y promedio de desfase de transmisión.
- **Comandos Clave:**
  - `reporte general` (resumen global de métricas del canal)
  - `reporte parrilla` (ejecución y bloques de programación)
  - `reporte programas` (catálogo y duraciones promedio)
  - `reporte pautas` (certificación de tiempo al aire por anunciante)
  - `reporte cumplimiento` (tasa de éxito de playout y desviaciones)

---

## 🛑 3. CATÁLOGO DE CÓDIGOS DE ERROR EXPLÍCITOS

Para evitar mensajes genéricos o confusos, el sistema implementa un estándar de **Códigos Explícitos** estructurados en 5 categorías fundamentales:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ESTRUCTURA DEL CÓDIGO                           │
│                                                                        │
│       ERR - [CATEGORÍA] - [NÚMERO CORRELATIVO]                         │
│                                                                        │
│       Ejemplos: ERR-CMD-002, ERR-ORD-001, ERR-INC-001, ERR-FMT-001     │
└────────────────────────────────────────────────────────────────────────┘
```

### 📌 Tabla Maestra de Códigos de Error

| Código | Categoría | Denominación Oficial | Causa Raíz | Solución Sugerida al Operador |
| :--- | :--- | :--- | :--- | :--- |
| **`ERR-CMD-001`** | Comando | **Comando Vacío** | Asunto del correo en blanco o solo espacios | Enviar comando en el Asunto del correo (ej. `parrilla hoy`) |
| **`ERR-CMD-002`** | Comando | **Entidad No Reconocida** | Nombre de entidad mal escrito (ej. `usuari`, `parillla`) | El sistema detecta similitud y sugiere la entidad correcta |
| **`ERR-CMD-003`** | Comando | **Acción No Reconocida** | Acción inexistente para la entidad (ej. `usuario bailar`) | Lista las acciones permitidas (`listar`, `agregar`, etc.) |
| **`ERR-CMD-004`** | Comando | **Carácter No Permitido** | Símbolos ilegales o no soportados (`#`, `$`, `&`) | Usar solo caracteres alfanuméricos, comas o guiones |
| **`ERR-ORD-001`** | Secuencia | **Acción antes de Entidad** | Orden invertido (ej. `listar usuario`, `consultar programa 1`) | Estructurar como `<entidad> <accion>` (ej. `usuario listar`) |
| **`ERR-ORD-002`** | Secuencia | **Parámetro antes de Entidad** | Iniciar con ID o número (ej. `1 usuario consultar`) | Colocar la entidad al inicio: `usuario consultar 1` |
| **`ERR-ORD-003`** | Secuencia | **Parámetros Desordenados** | ID antes de la acción (ej. `usuario 1 consultar`) | Orden correcto: `<entidad> <accion> <id>` |
| **`ERR-INC-001`** | Completitud | **Falta la Acción** | Se escribió solo la entidad (ej. `usuario`, `parrilla`) | Indicar la acción: `<entidad> <accion>` |
| **`ERR-INC-002`** | Completitud | **Faltan Parámetros** | La acción requiere ID o datos y no se enviaron | Incluir los parámetros requeridos (ej. `usuario eliminar 1`) |
| **`ERR-INC-003`** | Completitud | **Parámetros Insuficientes** | Se enviaron menos campos de los obligatorios | Completar los campos requeridos según el manual |
| **`ERR-FMT-001`** | Formato | **Formato Numérico Inválido** | Se esperaba un ID entero pero se ingresó texto | Ingresar un número entero válido (ej. `1`) |
| **`ERR-FMT-002`** | Formato | **Formato de Fecha Inválido** | Fecha no cumple estándar ISO `YYYY-MM-DD` | Usar formato: `2026-10-15` |
| **`ERR-FMT-003`** | Formato | **Formato de Hora Inválido** | Hora no cumple estándar militar de 24h `HH:mm` | Usar formato militar: `14:30` |
| **`ERR-FMT-004`** | Formato | **Estado de Dominio Inválido** | Estado no pertenece al enum del modelo | Usar los estados permitidos (ej. `APROBADA`, `LISTO`) |
| **`ERR-NEG-001`** | Negocio | **Registro No Encontrado** | El ID consultado no existe en la BD | Verificar con `<entidad> listar` los IDs existentes |
| **`ERR-BD-002`** | Base Datos | **Dependencia FK No Encontrada**| El ID relacionado foráneo no existe en la otra tabla| Registrar primero el elemento padre |
| **`ERR-BD-003`** | Base Datos | **Registro Duplicado (Unique)** | Código, NIT, CI o Email ya existente | Utilizar un código o valor único |
| **`ERR-BD-004`** | Base Datos | **Registro en Uso (Integridad)** | No se puede eliminar por tener hijos asociados | Eliminar primero las relaciones dependientes |
| **`ERR-SYS-001`** | Sistema | **Error Interno No Controlado**| Falla no prevista durante la ejecución | Contactar a soporte técnico con el detalle |

---

## 🧪 4. BANCO DE PRUEBAS COMPARATIVAS: ENTRADA INCORRECTA VS RESPUESTA DEL SISTEMA

### Caso 1: Comando Mal Introducido (Tipográfico o Léxico)

#### Entrada Errónea 1 (CU1 - Typo en entidad):
```
Asunto: usuari listar
```
- **Código Emitido:** `ERR-CMD-002` (Entidad No Reconocida)
- **Detalle:** `Entidad o comando 'usuari' no reconocido. ¿Quizás quiso decir 'usuario'?`
- **Sugerencia:** `usuario listar`

#### Entrada Errónea 2 (CU4 - Typo en MAM):
```
Asunto: materia listar
```
- **Código Emitido:** `ERR-CMD-002` (Entidad No Reconocida)
- **Detalle:** `Entidad o comando 'materia' no reconocido. ¿Quizás quiso decir 'material'?`
- **Sugerencia:** `material listar`

#### Entrada Errónea 3 (CU5 - Typo en Control de Calidad QC):
```
Asunto: validacio pendientes
```
- **Código Emitido:** `ERR-CMD-002` (Entidad No Reconocida)
- **Detalle:** `Entidad o comando 'validacio' no reconocido. ¿Quizás quiso decir 'validacion'?`
- **Sugerencia:** `validacion pendientes`

#### Entrada Errónea 4 (CU6 - Typo en Spots):
```
Asunto: spott listar
```
- **Código Emitido:** `ERR-CMD-002` (Entidad No Reconocida)
- **Detalle:** `Entidad o comando 'spott' no reconocido. ¿Quizás quiso decir 'spot'?`
- **Sugerencia:** `spot listar`

#### Entrada Errónea 5 (Acción No Reconocida para entidad):
```
Asunto: usuario bailar
```
- **Código Emitido:** `ERR-CMD-003` (Acción No Reconocida)
- **Detalle:** `Acción 'bailar' no reconocida para la entidad 'usuario'.`
- **Sugerencia:** Acciones válidas para usuario: `listar`, `consultar id`, `agregar Nombre,Email,Password,Rol`, `eliminar id`.

#### Entrada Errónea 6 (CU4 - Acción no reconocida en Materiales):
```
Asunto: material cocinar
```
- **Código Emitido:** `ERR-CMD-003` (Acción No Reconocida)
- **Detalle:** `Acción 'cocinar' no reconocida para la entidad 'material'.`
- **Sugerencia:** Acciones válidas para material: `listar`, `consultar id`, `agregar id_prog,cod,tit,seg`, `estado id,ESTADO`, `eliminar id`.

#### Entrada Errónea 7 (Carácter no permitido):
```
Asunto: usuario listar #$
```
- **Código Emitido:** `ERR-CMD-004` (Carácter No Permitido)
- **Detalle:** `Carácter o símbolo inválido detectado en el comando.`
- **Sugerencia:** `Utilice solo caracteres alfanuméricos, comas o guiones. Ejemplo: usuario listar`

---

### Caso 2: Orden Incompleta

#### Entrada Errónea 1 (Solo entidad CU1 - Usuario):
```
Asunto: usuario
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'usuario' pero no indicó la acción a realizar.`
- **Sugerencia:** `usuario listar | usuario consultar id | usuario agregar [...] | usuario eliminar id`

#### Entrada Errónea 2 (Solo entidad CU4 - Material MAM):
```
Asunto: material
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'material' pero no indicó la acción a realizar.`
- **Sugerencia:** `material listar | material consultar id | material agregar [...] | material estado id,ESTADO`

#### Entrada Errónea 3 (Solo entidad CU5 - Validación QC):
```
Asunto: validacion
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'validacion' pero no indicó la acción a realizar.`
- **Sugerencia:** `validacion pendientes | validacion listar | validacion aprobar id_item | validacion rechazar id_item,motivo`

#### Entrada Errónea 4 (Solo entidad CU6 - Pauta Publicitaria):
```
Asunto: pauta
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'pauta' pero no indicó la acción a realizar.`
- **Sugerencia:** `pauta listar | pauta consultar id | pauta agregar id_item,id_spot,tanda,orden`

#### Entrada Errónea 5 (Solo entidad CU7 - Emisión Real):
```
Asunto: emision
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'emision' pero no indicó la acción a realizar.`
- **Sugerencia:** `emision al_aire | emision listar | emision discrepancias | emision conciliar id_item`

#### Entrada Errónea 6 (Solo entidad CU8 - Reportes):
```
Asunto: reporte
```
- **Código Emitido:** `ERR-INC-001` (Orden Incompleta: Falta la Acción)
- **Detalle:** `Orden incompleta: Ingresó la entidad 'reporte' pero no indicó el tipo de reporte.`
- **Sugerencia:** `reporte general | reporte parrilla | reporte programas | reporte pautas | reporte cumplimiento`

---

### Caso 3: Orden Mal Ordenada / Desordenada

#### Entrada Errónea 1 (CU1 Invertido - Acción antes de Entidad):
```
Asunto: listar usuario
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'listar' antes de la entidad 'usuario'.`
- **Sugerencia:** `usuario listar`

#### Entrada Errónea 2 (CU4 Invertido - MAM):
```
Asunto: listar material
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'listar' antes de la entidad 'material'.`
- **Sugerencia:** `material listar`

#### Entrada Errónea 3 (CU5 Invertido - QC):
```
Asunto: aprobar validacion 1
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'aprobar' antes de la entidad 'validacion'.`
- **Sugerencia:** `validacion aprobar 1`

#### Entrada Errónea 4 (CU6 Invertido - Pauta Comercial):
```
Asunto: listar pauta
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'listar' antes de la entidad 'pauta'.`
- **Sugerencia:** `pauta listar`

#### Entrada Errónea 5 (CU7 Invertido - Conciliación de Emisión):
```
Asunto: conciliar emision 1
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'conciliar' antes de la entidad 'emision'.`
- **Sugerencia:** `emision conciliar 1`

#### Entrada Errónea 6 (CU8 Invertido - Reportes):
```
Asunto: general reporte
```
- **Código Emitido:** `ERR-ORD-001` (Orden Invertido: Acción antes de Entidad)
- **Detalle:** `Orden sintáctico invertido: Se escribió la acción 'general' antes de la entidad 'reporte'.`
- **Sugerencia:** `reporte general`

#### Entrada Errónea 7 (Parámetro colocado al inicio):
```
Asunto: 5 material consultar
```
- **Código Emitido:** `ERR-ORD-002` (Orden Invertido: Parámetro antes de Entidad)
- **Detalle:** `Se detectó un parámetro numérico ('5') antes del nombre de la entidad o acción.`
- **Sugerencia:** `material consultar 5`

#### Entrada Errónea 8 (Parámetro colocado antes de la acción):
```
Asunto: validacion 1 aprobar
```
- **Código Emitido:** `ERR-ORD-003` (Parámetros Desordenados)
- **Detalle:** `Parámetros mal ordenados: Se colocó el ID '1' antes de la acción 'aprobar'.`
- **Sugerencia:** `validacion aprobar 1`

---

### Caso 4: Formato de Datos Inválido

#### Entrada Errónea 1 (Letras donde se requiere ID numérico):
```
Asunto: usuario consultar abc
```
- **Código Emitido:** `ERR-FMT-001` (Formato Numérico Inválido)
- **Detalle:** `Se esperaba un número entero (ID numérico o cantidad), pero se ingresó un valor no numérico: For input string: "abc"`
- **Sugerencia:** `usuario consultar 1`

#### Entrada Errónea 2 (Fecha con formato incorrecto):
```
Asunto: parrilla crear 15/10/2026
```
- **Código Emitido:** `ERR-FMT-002` (Formato de Fecha Inválido)
- **Detalle:** `Formato de fecha inválido para '15/10/2026'. Use el formato yyyy-MM-dd (ejemplo: 2026-10-15)`
- **Sugerencia:** `parrilla crear 2026-10-15`

#### Entrada Errónea 3 (Hora con formato no militar):
```
Asunto: item agregar 1,1,2pm,3pm
```
- **Código Emitido:** `ERR-FMT-003` (Formato de Hora Inválido)
- **Detalle:** `Formato de hora inválido para '2pm'. Debe ser HH:mm o HH:mm:ss (ej: 14:30)`
- **Sugerencia:** `item agregar 1,1,14:00,15:00`

---

### Caso 5: Reglas de Base de Datos y Negocio

#### Entrada Errónea 1 (Registro inexistente):
```
Asunto: usuario consultar 999
```
- **Código Emitido:** `ERR-NEG-001` (Registro No Encontrado)
- **Detalle:** `No existe usuario con ID 999`
- **Sugerencia:** `usuario listar`

#### Entrada Errónea 2 (Clave foránea inexistente):
```
Asunto: item agregar 999,1,14:00,15:00
```
- **Código Emitido:** `ERR-BD-002` (Dependencia FK No Encontrada)
- **Detalle:** `Error de referencia: El identificador (ID) relacionado no existe en la base de datos.`
- **Sugerencia:** Verificar que la parrilla `#999` haya sido creada previamente con `parrilla listar`.

#### Entrada Errónea 3 (Registro duplicado):
```
Asunto: programa agregar PROG-01,Nuevo Titulo,60
```
- **Código Emitido:** `ERR-BD-003` (Registro Duplicado)
- **Detalle:** `Error de duplicidad: Ya existe un registro con ese mismo código, identificador o correo electrónico.`
- **Sugerencia:** Utilizar un código único diferente a `PROG-01`.

---

## 🎨 5. VISUALIZACIÓN DE LA RESPUESTA EN CORREO ELECTRÓNICO (HTML BROADCAST)

Cuando el servidor responde al operador de televisión, envía un correo HTML estilizado con el estándar broadcast:

```html
+-------------------------------------------------------------------------+
|  ✖ ERROR EN COMANDO DE PLAYOUT                      [ERR-ORD-001]       |
|                                                                         |
|  Orden Invertido (Acción antes de Entidad)                              |
|                                                                         |
|  Comando recibido: listar material                                      |
|                                                                         |
|  ⚠️ Detalle del Error:                                                  |
|  Orden sintáctico invertido: Se escribió la acción 'listar'             |
|  antes de la entidad 'material'.                                        |
|                                                                         |
|  💡 Formato correcto esperado:                                          |
|  material listar                                                        |
|                                                                         |
|  Envía help en el Asunto para ver el manual completo.   Código: ERR-ORD-001 |
+-------------------------------------------------------------------------+
```

Y en el asunto del correo devuelto por SMTP:
```
Subject: [ERR-ORD-001] ERROR: Orden Invertido (Acción antes de Entidad)
```

---

## 📊 6. RESUMEN DE COMPROBACIÓN AUTOMATIZADA

Se ejecutó la suite de pruebas unitarias y de integración léxico-sintáctica (`test.PruebaCodigosError`):

```
=================================================================
  INICIANDO SUITE DE PRUEBAS DE CÓDIGOS DE ERROR EXPLÍCITOS
=================================================================
✔ [OK] Comando Vacío -> ERR-CMD-001
✔ [OK] CU1 Invertido (listar usuario) -> ERR-ORD-001
✔ [OK] CU2 Invertido (consultar programa 1) -> ERR-ORD-001
✔ [OK] CU3 Invertido (listar parrilla) -> ERR-ORD-001
✔ [OK] CU4 Invertido (listar material) -> ERR-ORD-001
✔ [OK] CU5 Invertido (aprobar validacion 1) -> ERR-ORD-001
✔ [OK] CU6 Invertido (listar pauta) -> ERR-ORD-001
✔ [OK] CU7 Invertido (conciliar emision 1) -> ERR-ORD-001
✔ [OK] CU8 Invertido (general reporte) -> ERR-ORD-001
✔ [OK] CU1 Parámetro al inicio (1 usuario consultar) -> ERR-ORD-002
✔ [OK] CU4 Parámetro al inicio (5 material consultar) -> ERR-ORD-002
✔ [OK] CU1 Incompleto (usuario) -> ERR-INC-001
✔ [OK] CU2 Incompleto (programa) -> ERR-INC-001
✔ [OK] CU3 Incompleto (parrilla) -> ERR-INC-001
✔ [OK] CU4 Incompleto (material) -> ERR-INC-001
✔ [OK] CU5 Incompleto (validacion) -> ERR-INC-001
✔ [OK] CU6 Incompleto (pauta) -> ERR-INC-001
✔ [OK] CU6 Incompleto (spot) -> ERR-INC-001
✔ [OK] CU7 Incompleto (emision) -> ERR-INC-001
✔ [OK] CU8 Incompleto (reporte) -> ERR-INC-001
✔ [OK] CU1 Parámetro antes de acción (usuario 1 consultar) -> ERR-ORD-003
✔ [OK] CU5 Parámetro antes de acción (validacion 1 aprobar) -> ERR-ORD-003
✔ [OK] CU4 Parámetro antes de acción (material 3 consultar) -> ERR-ORD-003
✔ [OK] CU1 Typo (usuari listar) -> ERR-CMD-002
✔ [OK] CU2 Typo (programaa listar) -> ERR-CMD-002
✔ [OK] CU4 Typo (materia listar) -> ERR-CMD-002
✔ [OK] CU5 Typo (validacio pendientes) -> ERR-CMD-002
✔ [OK] CU6 Typo (spott listar) -> ERR-CMD-002
✔ [OK] CU6 Typo (pautaa listar) -> ERR-CMD-002
✔ [OK] CU1 Acción desconocida (usuario bailar) -> ERR-CMD-003
✔ [OK] CU4 Acción desconocida (material cocinar) -> ERR-CMD-003
✔ [OK] CU6 Acción desconocida (pauta bailar) -> ERR-CMD-003
✔ [OK] Carácter no permitido (usuario listar #$) -> ERR-CMD-004
✔ [OK] CU1 Válido (usuario listar) -> Ejecutado correctamente sin errores.
✔ [OK] CU2 Válido (programa listar) -> Ejecutado correctamente sin errores.
✔ [OK] CU3 Válido (parrilla al_aire) -> Ejecutado correctamente sin errores.
✔ [OK] CU4 Válido (material listar) -> Ejecutado correctamente sin errores.
✔ [OK] CU5 Válido (validacion pendientes) -> Ejecutado correctamente sin errores.
✔ [OK] CU6 Válido (pauta listar) -> Ejecutado correctamente sin errores.
✔ [OK] CU7 Válido (emision al_aire) -> Ejecutado correctamente sin errores.
✔ [OK] CU8 Válido (reporte general) -> Ejecutado correctamente sin errores.
=================================================================
  RESULTADO: 41/41 pruebas superadas con éxito (100%)
=================================================================
```

---
*Documento oficial de especificación y auditoría de software — Playout Automation Grupo 12 SC.*

