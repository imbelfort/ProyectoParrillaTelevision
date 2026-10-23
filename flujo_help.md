# GUÍA OPERACIONAL PASO A PASO EN CONSOLA Y CORREO POR ROLES
## SISTEMA DE GESTIÓN DE PARRILLA DE TELEVISIÓN - FICCT UAGRM (GRUPO 12 SC)

Este sistema funciona tanto por **Consola interactiva (CLI)** como por **Correo Electrónico (POP3 / SMTP)**.  
En consola, las respuestas se renderizan inmediatamente como tablas ASCII en pantalla y además se envían por correo electrónico.

---

## 💻 CÓMO INICIAR EL MODO CONSOLA

Haga doble clic en el archivo:
```bat
run-consola.bat
```
O ejecute manualmente desde la terminal:
```cmd
java -cp "build/classes;lib/*" proyectomailgrupo12sc.Consola
```

El prompt mostrará el rol activo y el correo emisor:
```
[ADMINISTRADOR | admin@tecnoweb.org.bo] tv>
```

---

## 👥 COMANDOS DE SESIÓN Y CAMBIO DE ROLES EN CONSOLA

| Comando | Acción | Rol / Correo Asignado |
| :--- | :--- | :--- |
| `as admin` | Cambia la sesión al Administrador | Rol: **ADMINISTRADOR** (`admin@tecnoweb.org.bo`) |
| `as programador` | Cambia la sesión a Programación | Rol: **PROGRAMADOR** (`programador@tecnoweb.org.bo`) |
| `as contenidos` *(o `as qc`)* | Cambia a Ingesta y Calidad | Rol: **ENCARGADO_CONTENIDOS** (`contenidos@tecnoweb.org.bo`) |
| `as <correo>` | Asigna cualquier correo personalizado | Rol: **USUARIO_EXTERNO** |
| `whoami` | Muestra el usuario, correo y rol actual | - |
| `roles` | Muestra los roles disponibles y sus funciones | - |
| `exit` *(o `salir`)* | Cierra la consola del sistema | - |

---

## 🎬 GUÍA DE EJECUCIÓN PASO A PASO POR ROLES (PROBADA Y VERIFICADA)

Siga esta secuencia cronológica en la consola para recorrer todo el flujo de trabajo de la estación de televisión:

```
[1. ROL ADMINISTRADOR]  ──► Configura personal, roles, auditoría y reportes gerenciales.
          │
          ▼
[2. ROL PROGRAMADOR]    ──► Crea catálogo, arma la parrilla de emisión y asigna comerciales.
          │
          ▼
[3. ROL CONTENIDOS/QC]  ──► Ingesta videos MAM, aprueba control de calidad y concilia salida al aire.
```

---

### FASE 1: ROL ADMINISTRADOR (`as admin`)
*Responsable de la administración del personal, asignación de permisos, fiscalización y reportes consolidados.*

1. **Seleccionar el rol en consola:**
   ```
   as admin
   ```
2. **Listar todos los usuarios del canal:**
   ```
   usuario listar
   ```
3. **Crear un nuevo usuario:**
   ```
   usuario agregar Marcos,Gomez,marcos@canal.com,123,Programador
   ```
4. **Consultar la ficha técnica de un usuario:**
   ```
   usuario consultar 1
   ```
5. **Modificar datos del usuario:**
   ```
   usuario modificar 1,Carlos,Roca Arteaga,admin@tecnoweb.org.bo,admin123,Administrador,ACTIVO
   ```
6. **Ver los roles del sistema:**
   ```
   rol listar
   ```
7. **Crear un nuevo rol operativo:**
   ```
   rol agregar MasterControl,Operador de transmision y emision
   ```
8. **Asignar rol a un usuario:**
   ```
   rol asignar 1,1
   ```
9. **Ver bitácora de auditoría (quién hizo qué en el sistema):**
   ```
   auditoria listar
   ```
10. **Generar reporte estadístico general del canal:**
    ```
    reporte general
    ```

---

### FASE 2: ROL PROGRAMADOR (`as programador`)
*Responsable del catálogo de programas, grilla de programación diaria (rundown) y colocación de tandas publicitarias.*

11. **Cambiar al rol de Programador en consola:**
    ```
    as programador
    ```
12. **Ver categorías o géneros televisivos:**
    ```
    categoria listar
    ```
13. **Agregar nueva categoría:**
    ```
    categoria agregar Magazines,Variedades y entrevistas matutinas
    ```
14. **Registrar un nuevo programa en el catálogo:**
    ```
    programa agregar PROG-77,En Familia Magazine,60
    ```
    *(Código: PROG-77 | Título: En Familia Magazine | Duración estándar: 60 min)*
15. **Listar el catálogo completo de programas:**
    ```
    programa listar
    ```
16. **Consultar detalle del programa:**
    ```
    programa consultar 1
    ```
17. **Modificar programa del catálogo:**
    ```
    programa modificar 1,NOT-001,TeleNoticias Central,Edicion Estelar,1,ATP,60,NACIONAL,ACTIVO
    ```
18. **Crear la parrilla de programación para una fecha:**
    ```
    parrilla crear 2026-11-28
    ```
19. **Listar las parrillas oficiales:**
    ```
    parrilla listar
    ```
20. **Consultar una parrilla específica:**
    ```
    parrilla consultar 1
    ```
21. **Asignar un programa a un bloque horario (Item de Parrilla):**
    ```
    item agregar 1,1,17:00,18:00
    ```
    *(id_parrilla: 1 | id_programa: 1 | hora_inicio: 17:00 | hora_fin: 18:00)*
22. **Listar los bloques del playlist:**
    ```
    item listar
    ```
23. **Modificar horario de un bloque:**
    ```
    item modificar 1,17:15,18:15,VIVO,PROGRAMADO
    ```
24. **Aprobar oficialmente la parrilla:**
    ```
    parrilla estado 1,APROBADA
    ```
25. **Consultar rundown del día:**
    ```
    parrilla hoy
    ```
26. **Consultar qué programa está al aire en este momento:**
    ```
    parrilla al_aire
    ```
27. **Listar clientes anunciantes:**
    ```
    cliente listar
    ```
28. **Consultar detalle de un cliente:**
    ```
    cliente consultar 1
    ```
29. **Registrar nuevo cliente anunciante:**
    ```
    cliente agregar Cerveceria Paceña,1099887766,Lic. Banegas,contacto@cbn.bo,70012345
    ```
30. **Listar spots comerciales disponibles:**
    ```
    spot listar
    ```
31. **Consultar detalle de un spot:**
    ```
    spot consultar 1
    ```
32. **Registrar nuevo spot comercial asociado al cliente:**
    ```
    spot agregar 1,SPOT-99,Promo Fin de Año 30s,30
    ```
    *(id_cliente: 1 | código: SPOT-99 | título: Promo Fin de Año 30s | duración: 30 seg)*
33. **Insertar spot publicitario en tanda de un bloque:**
    ```
    pauta agregar 1,1,CORTE_1,1
    ```
    *(id_item: 1 | id_spot: 1 | tanda: CORTE_1 | orden: 1)*
34. **Listar pautas programadas:**
    ```
    pauta listar
    ```
35. **Consultar una pauta específica:**
    ```
    pauta consultar 1
    ```
36. **Generar reporte de parrilla:**
    ```
    reporte parrilla
    ```
37. **Generar reporte de programas:**
    ```
    reporte programas
    ```

---

### FASE 3: ROL ENCARGADO DE CONTENIDOS Y CONTROL DE CALIDAD (`as contenidos`)
*Responsable del almacenamiento de video (MAM), inspección técnica QC previa al aire, certificación de comerciales y conciliación de emisión real.*

38. **Cambiar al rol de Contenidos / QC en consola:**
    ```
    as contenidos
    ```
39. **Ingestar clip de video asociado a un programa:**
    ```
    material agregar 1,CLIP-77,Cabecera En Familia,60
    ```
    *(id_programa: 1 | código: CLIP-77 | título: Cabecera En Familia | duración: 60 seg)*
40. **Listar materiales en el storage MAM:**
    ```
    material listar
    ```
41. **Consultar detalle del material de video:**
    ```
    material consultar 1
    ```
42. **Ver materiales de un programa específico:**
    ```
    material listar_programa 1
    ```
43. **Aprobar estado técnico del material:**
    ```
    material estado 1,LISTO
    ```
44. **Consultar bloques de parrilla pendientes de revisión técnica (QC):**
    ```
    validacion pendientes
    ```
45. **Aprobar bloque para su salida al aire:**
    ```
    validacion aprobar 1
    ```
    *(Aprobado audio broadcast -24 LKFS/dB y video 16:9)*
46. **Consultar resultado de control de calidad del bloque:**
    ```
    validacion consultar 1
    ```
47. **Certificar que el comercial publicitario salió al aire:**
    ```
    pauta estado 1,EMITIDO
    ```
48. **Conciliar la emisión del bloque con la parrilla oficial:**
    ```
    emision conciliar 1
    ```
49. **Ver bitácora de emisión real (As-Run Log):**
    ```
    emision listar
    ```
50. **Revisar si hubo desfases o discrepancias de tiempo:**
    ```
    emision discrepancias
    ```
51. **Consultar el evento o programa al aire:**
    ```
    emision al_aire
    ```
52. **Generar reporte de certificación y cobro de pautas:**
    ```
    reporte pautas
    ```
53. **Generar reporte de cumplimiento de emisión:**
    ```
    reporte cumplimiento
    ```

---

## 📋 COMANDOS RÁPIDOS DE CLIENTES, SPOTS Y PUBLICIDAD (CU6)

| Operación | Comando Exacto | Descripción |
| :--- | :--- | :--- |
| **CLIENTES LIST** | `cliente listar` | Muestra todos los clientes anunciantes |
| **CLIENTE VIEW** | `cliente consultar 1` | Ficha técnica y contacto del cliente #1 |
| **CLIENTE ADD** | `cliente agregar Entel,1020304050,Gerente,ventas@entel.bo,70001122` | Registra nueva empresa anunciante |
| **SPOTS LIST** | `spot listar` | Catálogo de spots comerciales |
| **SPOT VIEW** | `spot consultar 1` | Detalle del spot y anunciante asociado |
| **SPOT ADD** | `spot agregar 1,SPOT-03,Promo Verano 2026,30` | Ingesta spot con duración en segundos |
| **PAUTA LIST** | `pauta listar` *(o `pauta listar 1` por bloque)* | Playlist de tandas comerciales |
| **PAUTA VIEW** | `pauta consultar 1` | Detalle de inserción en tanda |
| **PAUTA ADD** | `pauta agregar 1,1,CORTE_1,1` | Inserta spot en bloque y tanda |
| **PAUTA ESTADO**| `pauta estado 1,EMITIDO` | Certifica la salida al aire del anuncio |
| **PAUTA DEL** | `pauta eliminar 2` | Cancela la pauta comercial |

---

## 📊 MATRIZ DE RESPONSABILIDADES Y PERMISOS POR ROL

| Caso de Uso | Operación | Administrador (`as admin`) | Programador (`as programador`) | Encargado Contenidos / QC (`as contenidos`) |
| :--- | :--- | :---: | :---: | :---: |
| **CU1** | Gestión de Usuarios y Roles | ✅ **Principal** | ❌ | ❌ |
| **CU2** | Catálogo de Programas y Categorías | 👁️ Lectura | ✅ **Principal** | 👁️ Lectura |
| **CU3** | Confección de Parrilla y Rundown | 👁️ Lectura | ✅ **Principal** | 👁️ Lectura |
| **CU4** | Ingesta de Video MAM | 👁️ Lectura | 👁️ Lectura | ✅ **Principal** |
| **CU5** | Control de Calidad QC | 👁️ Lectura | 👁️ Lectura | ✅ **Principal** |
| **CU6** | Clientes, Spots y Pauta Comercial | 👁️ Lectura | ✅ Inserción | ✅ Certificación (`EMITIDO`) |
| **CU7** | Conciliación As-Run y Auditoría | ✅ Auditoría | 👁️ Lectura | ✅ Conciliación |
| **CU8** | Reportes y Estadísticas | ✅ General / Auditoría | ✅ Parrilla / Catálogo | ✅ Pautas / Playout |

---

## 🧪 SUITE DE PRUEBAS AUTOMATIZADAS

Para validar todos los módulos automáticamente sin intervención manual, ejecute:
```bat
run-tests.bat
```

Esta suite ejecuta 5 bancos de pruebas consecutivas:
1. `PruebaCodigosError`: 41 casos de error sintáctico, completitud y orden.
2. `PruebaConexionPOP`: Valida conexión a `mail.tecnoweb.org.bo:110`.
3. `PruebaFlujoCompleto`: Comandos rápidos de los 8 casos de uso con entrega SMTP.
4. `PruebaCicloVidaCompleto`: Ciclo completo de creación, modificación y consulta paso a paso.
5. `PruebaConsolaPorRoles`: Simulación interactiva recorriendo los 3 roles operativos.
