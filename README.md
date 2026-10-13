# SISTEMA DE GESTIÓN DE PARRILLA DE TELEVISIÓN
**FICCT - UAGRM | Materia: Sistemas de Información II | Grupo 12 SC**

Sistema automatizado de gestión y control de emisión televisiva basado en comandos por correo electrónico (POP3 / SMTP) y arquitectura en capas (Model-DAO-Service-Interpreter).

---

## 📺 Casos de Uso Aprobados

- **CU1. Gestión de Usuarios y Roles:** Administración de usuarios con roles especializados: *Administrador*, *Programador* y *Encargado de Contenidos*.
- **CU2. Gestión de Catálogo de Programas:** Administración de la biblioteca de contenidos televisivos por género/categoría, origen, clasificación de edades y duración.
- **CU3. Gestión de Confeccionar y Controlar la Parrilla de Programación:** Creación de la parrilla de programación por fecha/señal, asignación de bloques horarios (*items de parrilla*), tipos de emisión (Vivo, Grabado, Repetición) y control de solapamientos.
- **CU4. Gestión de Gestionar Materiales de Video:** Ingesta y control de clips maestros audiovisuales (código de material, formato, resolución, duración en segundos, ruta en storage MAM y estado técnico).
- **CU5. Gestión de Validar Estado de Emisión:** Control de Calidad Técnico (QC) previo a la salida al aire. Validación de audio (EBU R128), aspect ratio y disponibilidad de material listo para broadcast.
- **CU6. Gestión de Pauta Publicitaria e Insertar Spots Comerciales:** Administración de clientes/anunciantes, catálogo de spots comerciales e inserción de pautas dentro de cortes comerciales (tandas y orden de reproducción) para playout broadcast.
- **CU7. Gestión de Conciliar Emisión Real, Registrar Auditoría:** Registro de bitácora de transmisión real (*As-Run Log*), detección automática de discrepancias/desplazamientos de tiempo y auditoría de eventos del sistema.
- **CU8. Reportes y Estadísticas:** Generación de estadísticas consolidadas: ocupación de parrilla, cumplimiento de horarios, certificación de pautas comerciales y logs de auditoría.

---

## 🏛 Arquitectura del Sistema

```
[Cliente de Correo (Gmail / Outlook / Webmail)]
                        │
                        ▼ (SMTP / POP3)
         [Servidor de Correo: mail.tecnoweb.org.bo]
                        │
                        ▼
       [MailVerificationThread / Analex / Interpreter]
                        │
                        ▼
            [ITokenCasoUsoEventListener]
                        │
                        ▼
               [Capa de Servicios]
           (Validaciones de negocio y reglas)
                        │
                        ▼
                [Capa de DAO / JDBC]
            (Consultas SQL preparadas)
                        │
                        ▼
          [PostgreSQL: db_grupo12sc]
                        │
                        ▼
       [HtmlBuilder -> SendEmail -> Correo de Respuesta]
```

---

## 🚀 Requisitos y Configuración

- **Java:** JDK 8+
- **Base de Datos:** PostgreSQL 12+ (Host: `mail.tecnoweb.org.bo`, Base: `db_grupo12sc`)
- **Librerías:** JDBC Driver (`postgresql-42.3.0.jar`), JavaMail (`javax.mail.jar`, `activation-1.1.1.jar`), Gson, JSON.
- **Correo del Sistema:** `grupo12sc@tecnoweb.org.bo`

---

## 📋 Lista Rápida de Comandos (Cheat Sheet)

Consulte [flujo_help.md](flujo_help.md) para ver la guía completa con todos los comandos y parámetros.

| Caso de Uso | Comando Más Sencillo | Acción que Realiza |
| :--- | :--- | :--- |
| **CU1** | `usuario listar` | Lista todos los usuarios y sus roles asignados |
| **CU2** | `programa listar` | Muestra el catálogo de programas del canal |
| **CU3** | `parrilla listar` | Despliega las parrillas oficiales de programación |
| **CU4** | `material listar` | Muestra los clips de video ingestados en el MAM |
| **CU5** | `validacion pendientes` | Muestra los bloques que requieren control de calidad |
| **CU6** | `pauta listar` | Muestra los spots pautados en las tandas |
| **CU7** | `emision listar` | Muestra la bitácora de emisión real (As-Run Log) |
| **CU8** | `reporte general` | Muestra el reporte estadístico y balance del canal |
