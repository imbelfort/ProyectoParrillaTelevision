package Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializarDB {

    private static void ejecutarSentencias(Statement stmt, String... sentencias) throws SQLException {
        for (String sentencia : sentencias) {
            stmt.executeUpdate(sentencia);
        }
    }

    public static void eliminarTodasLasTablas() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = Conexion.getInstancia().EstablecerConexion();
            if (con == null) {
                System.err.println("No hay conexion disponible para eliminar tablas.");
                return;
            }

            stmt = con.createStatement();

            ejecutarSentencias(stmt,
                    "DROP TABLE IF EXISTS reporte CASCADE",
                    "DROP TABLE IF EXISTS auditoria_sistema CASCADE",
                    "DROP TABLE IF EXISTS emision_real_log CASCADE",
                    "DROP TABLE IF EXISTS pauta_comercial CASCADE",
                    "DROP TABLE IF EXISTS spot_publicitario CASCADE",
                    "DROP TABLE IF EXISTS cliente_publicitario CASCADE",
                    "DROP TABLE IF EXISTS validacion_emision CASCADE",
                    "DROP TABLE IF EXISTS material_video CASCADE",
                    "DROP TABLE IF EXISTS item_parrilla CASCADE",
                    "DROP TABLE IF EXISTS parrilla CASCADE",
                    "DROP TABLE IF EXISTS programa CASCADE",
                    "DROP TABLE IF EXISTS categoria_programa CASCADE",
                    // Tablas previas (limpieza completa)
                    "DROP TABLE IF EXISTS notificaciones CASCADE",
                    "DROP TABLE IF EXISTS historial_solicitud CASCADE",
                    "DROP TABLE IF EXISTS oficio_respuesta_cpd CASCADE",
                    "DROP TABLE IF EXISTS oficio_cpd CASCADE",
                    "DROP TABLE IF EXISTS bloqueo_grupo CASCADE",
                    "DROP TABLE IF EXISTS evaluacion_solicitud CASCADE",
                    "DROP TABLE IF EXISTS archivo_solicitud CASCADE",
                    "DROP TABLE IF EXISTS solicitud_grupo CASCADE",
                    "DROP TABLE IF EXISTS solicitud CASCADE",
                    "DROP TABLE IF EXISTS horario_grupo CASCADE",
                    "DROP TABLE IF EXISTS grupo CASCADE",
                    "DROP TABLE IF EXISTS materia_carrera CASCADE",
                    "DROP TABLE IF EXISTS materia CASCADE",
                    "DROP TABLE IF EXISTS periodos CASCADE",
                    "DROP TABLE IF EXISTS director_carrera CASCADE",
                    "DROP TABLE IF EXISTS personal_carrera CASCADE",
                    "DROP TABLE IF EXISTS estudiantes CASCADE",
                    "DROP TABLE IF EXISTS carrera CASCADE",
                    "DROP TABLE IF EXISTS usuario_roles CASCADE",
                    "DROP TABLE IF EXISTS roles CASCADE",
                    "DROP TABLE IF EXISTS usuario CASCADE"
            );

            System.out.println("[OK] Tablas del sistema eliminadas correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar tablas: " + e.getMessage());
        } finally {
            cerrarRecursos(stmt, con);
        }
    }

    public static void crearTablasSiNoExisten() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = Conexion.getInstancia().EstablecerConexion();
            if (con == null) {
                System.err.println("No hay conexion disponible para crear tablas.");
                return;
            }

            stmt = con.createStatement();

            // 1. CU1: USUARIOS Y ROLES
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuario (" +
                    "id_usuario SERIAL PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellidos VARCHAR(100) NOT NULL, " +
                    "ci VARCHAR(30) UNIQUE NOT NULL, " +
                    "email VARCHAR(150) UNIQUE NOT NULL, " +
                    "telefono VARCHAR(30), " +
                    "direccion TEXT, " +
                    "foto TEXT, " +
                    "usuario_sistema VARCHAR(100) UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "fecha_nacimiento DATE, " +
                    "fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "fecha_modificacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT chk_usuario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS roles (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nombre VARCHAR(50) NOT NULL UNIQUE, " +
                    "descripcion TEXT, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT chk_roles_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuario_roles (" +
                    "id_usuario INTEGER NOT NULL, " +
                    "id_rol INTEGER NOT NULL, " +
                    "fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "PRIMARY KEY (id_usuario, id_rol), " +
                    "CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol) REFERENCES roles(id) ON DELETE CASCADE, " +
                    "CONSTRAINT chk_ur_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))" +
                    ")"
            );

            // 2. CU2: CATÁLOGO DE PROGRAMAS
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS categoria_programa (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL UNIQUE, " +
                    "descripcion TEXT, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "CONSTRAINT chk_cat_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS programa (" +
                    "id SERIAL PRIMARY KEY, " +
                    "codigo VARCHAR(30) NOT NULL UNIQUE, " +
                    "titulo VARCHAR(150) NOT NULL, " +
                    "descripcion TEXT, " +
                    "id_categoria INTEGER NOT NULL, " +
                    "clasificacion_edad VARCHAR(20) NOT NULL DEFAULT 'ATP', " +
                    "duracion_estandar_minutos INTEGER NOT NULL DEFAULT 60, " +
                    "origen VARCHAR(50) NOT NULL DEFAULT 'NACIONAL', " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_programa_categoria FOREIGN KEY (id_categoria) REFERENCES categoria_programa(id), " +
                    "CONSTRAINT chk_programa_clasificacion CHECK (clasificacion_edad IN ('ATP', 'TEEN_14', 'ADULTO_18')), " +
                    "CONSTRAINT chk_programa_origen CHECK (origen IN ('NACIONAL', 'INTERNACIONAL', 'COPRODUCCION')), " +
                    "CONSTRAINT chk_programa_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'ARCHIVADO'))" +
                    ")"
            );

            // 3. CU3: PARRILLA DE PROGRAMACIÓN
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS parrilla (" +
                    "id SERIAL PRIMARY KEY, " +
                    "fecha DATE NOT NULL, " +
                    "canal_senial VARCHAR(50) NOT NULL DEFAULT 'SENIAL_PRINCIPAL_HD', " +
                    "descripcion VARCHAR(200), " +
                    "id_programador INTEGER NOT NULL, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'BORRADOR', " +
                    "fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_parrilla_programador FOREIGN KEY (id_programador) REFERENCES usuario(id_usuario), " +
                    "CONSTRAINT chk_parrilla_estado CHECK (estado IN ('BORRADOR', 'APROBADA', 'PUBLICADA', 'CERRADA'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS item_parrilla (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_parrilla INTEGER NOT NULL, " +
                    "id_programa INTEGER NOT NULL, " +
                    "hora_inicio TIME NOT NULL, " +
                    "hora_fin TIME NOT NULL, " +
                    "tipo_emision VARCHAR(30) NOT NULL DEFAULT 'GRABADO', " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'PROGRAMADO', " +
                    "observaciones TEXT, " +
                    "CONSTRAINT fk_item_parrilla FOREIGN KEY (id_parrilla) REFERENCES parrilla(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_item_programa FOREIGN KEY (id_programa) REFERENCES programa(id), " +
                    "CONSTRAINT chk_item_tipo CHECK (tipo_emision IN ('VIVO', 'GRABADO', 'REPETICION', 'CADENA_OFICIAL')), " +
                    "CONSTRAINT chk_item_estado CHECK (estado IN ('PROGRAMADO', 'EN_CURSO', 'EMITIDO', 'CANCELADO', 'AJUSTADO'))" +
                    ")"
            );

            // 4. CU4: MATERIALES DE VIDEO
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS material_video (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_programa INTEGER NOT NULL, " +
                    "codigo_material VARCHAR(50) NOT NULL UNIQUE, " +
                    "titulo_clip VARCHAR(150) NOT NULL, " +
                    "formato VARCHAR(30) NOT NULL DEFAULT 'HD_1080I', " +
                    "resolucion VARCHAR(30) NOT NULL DEFAULT '1920x1080', " +
                    "duracion_segundos INTEGER NOT NULL, " +
                    "ruta_servidor TEXT NOT NULL, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'INGESTADO', " +
                    "id_responsable INTEGER NOT NULL, " +
                    "fecha_ingesta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "observaciones TEXT, " +
                    "CONSTRAINT fk_mat_programa FOREIGN KEY (id_programa) REFERENCES programa(id), " +
                    "CONSTRAINT fk_mat_responsable FOREIGN KEY (id_responsable) REFERENCES usuario(id_usuario), " +
                    "CONSTRAINT chk_mat_estado CHECK (estado IN ('INGESTADO', 'TRANSCODIFICADO', 'LISTO', 'CUARENTENA', 'DESCARTADO'))" +
                    ")"
            );

            // 5. CU5: VALIDACIÓN DE ESTADO DE EMISIÓN (QC)
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS validacion_emision (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_item_parrilla INTEGER NOT NULL, " +
                    "id_material_video INTEGER, " +
                    "id_inspector_qc INTEGER NOT NULL, " +
                    "resultado_qc VARCHAR(40) NOT NULL DEFAULT 'PENDIENTE', " +
                    "aspect_ratio VARCHAR(20) DEFAULT '16:9', " +
                    "nivel_audio_db NUMERIC(5,2) DEFAULT -24.00, " +
                    "observaciones TEXT, " +
                    "fecha_validacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_val_item FOREIGN KEY (id_item_parrilla) REFERENCES item_parrilla(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_val_material FOREIGN KEY (id_material_video) REFERENCES material_video(id), " +
                    "CONSTRAINT fk_val_inspector FOREIGN KEY (id_inspector_qc) REFERENCES usuario(id_usuario), " +
                    "CONSTRAINT chk_val_resultado CHECK (resultado_qc IN ('APROBADO_AIRE', 'RECHAZADO_FALLA_AUDIO', 'RECHAZADO_FALLA_VIDEO', 'RECHAZADO_SIN_MATERIAL', 'OBSERVADO', 'PENDIENTE'))" +
                    ")"
            );

            // 6. CU6: PAUTA PUBLICITARIA Y SPOTS COMERCIALES
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS cliente_publicitario (" +
                    "id SERIAL PRIMARY KEY, " +
                    "razon_social VARCHAR(150) NOT NULL, " +
                    "nit VARCHAR(30) UNIQUE NOT NULL, " +
                    "contacto VARCHAR(100), " +
                    "email VARCHAR(150), " +
                    "telefono VARCHAR(30), " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "CONSTRAINT chk_cli_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS spot_publicitario (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_cliente INTEGER NOT NULL, " +
                    "codigo_spot VARCHAR(50) NOT NULL UNIQUE, " +
                    "titulo_spot VARCHAR(150) NOT NULL, " +
                    "duracion_segundos INTEGER NOT NULL DEFAULT 30, " +
                    "fecha_vigencia_inicio DATE NOT NULL, " +
                    "fecha_vigencia_fin DATE NOT NULL, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO', " +
                    "CONSTRAINT fk_spot_cliente FOREIGN KEY (id_cliente) REFERENCES cliente_publicitario(id), " +
                    "CONSTRAINT chk_spot_estado CHECK (estado IN ('ACTIVO', 'VENCIDO', 'SUSPENDIDO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pauta_comercial (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_item_parrilla INTEGER NOT NULL, " +
                    "id_spot INTEGER NOT NULL, " +
                    "tanda_bloque VARCHAR(30) NOT NULL DEFAULT 'CORTE_1', " +
                    "orden INTEGER NOT NULL DEFAULT 1, " +
                    "hora_estimada TIME, " +
                    "estado VARCHAR(30) NOT NULL DEFAULT 'PAUTADO', " +
                    "CONSTRAINT fk_pauta_item FOREIGN KEY (id_item_parrilla) REFERENCES item_parrilla(id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_pauta_spot FOREIGN KEY (id_spot) REFERENCES spot_publicitario(id), " +
                    "CONSTRAINT chk_pauta_tanda CHECK (tanda_bloque IN ('CORTE_1', 'CORTE_2', 'CORTE_3', 'CORTE_4', 'INICIO_PROGRAMA', 'FIN_PROGRAMA')), " +
                    "CONSTRAINT chk_pauta_estado CHECK (estado IN ('PAUTADO', 'EMITIDO', 'CANCELADO', 'OMITIDO'))" +
                    ")"
            );

            // 7. CU7: EMISIÓN REAL Y AUDITORÍA
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS emision_real_log (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_item_parrilla INTEGER NOT NULL, " +
                    "id_material_video INTEGER, " +
                    "hora_inicio_real TIMESTAMP NOT NULL, " +
                    "hora_fin_real TIMESTAMP NOT NULL, " +
                    "duracion_real_segundos INTEGER NOT NULL, " +
                    "discrepancia_segundos INTEGER NOT NULL DEFAULT 0, " +
                    "estado_emision VARCHAR(40) NOT NULL DEFAULT 'EMITIDO_CORRECTO', " +
                    "operador_emision VARCHAR(100) NOT NULL, " +
                    "observaciones TEXT, " +
                    "fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_emision_item FOREIGN KEY (id_item_parrilla) REFERENCES item_parrilla(id), " +
                    "CONSTRAINT fk_emision_material FOREIGN KEY (id_material_video) REFERENCES material_video(id), " +
                    "CONSTRAINT chk_emision_estado CHECK (estado_emision IN ('EMITIDO_CORRECTO', 'DESPLAZADO', 'CORTE_ENERGIA', 'FALLA_TECNICA', 'INTERRUPCION_NOTICIOSA', 'NO_EMITIDO'))" +
                    ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auditoria_sistema (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_usuario INTEGER, " +
                    "tabla_afectada VARCHAR(60) NOT NULL, " +
                    "accion VARCHAR(50) NOT NULL, " +
                    "detalle TEXT NOT NULL, " +
                    "fecha_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)" +
                    ")"
            );

            // 8. CU8: REPORTES
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS reporte (" +
                    "id SERIAL PRIMARY KEY, " +
                    "id_usuario INTEGER NOT NULL, " +
                    "tipo_reporte VARCHAR(100) NOT NULL, " +
                    "parametros TEXT, " +
                    "fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)" +
                    ")"
            );

            System.out.println("[OK] Tablas del Sistema de Parrilla de Television creadas exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        } finally {
            cerrarRecursos(stmt, con);
        }
    }

    public static void insertarDatosIniciales() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = Conexion.getInstancia().EstablecerConexion();
            if (con == null) return;

            stmt = con.createStatement();

            // 1. Roles
            stmt.executeUpdate(
                    "INSERT INTO roles (id, nombre, descripcion) VALUES " +
                    "(1, 'ADMINISTRADOR', 'Administrador general del sistema de television'), " +
                    "(2, 'PROGRAMADOR', 'Responsable de confeccionar la parrilla y pautas horarias'), " +
                    "(3, 'ENCARGADO_CONTENIDOS', 'Responsable de materiales de video y validacion tecnica QC') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 2. Usuarios Iniciales
            stmt.executeUpdate(
                    "INSERT INTO usuario (id_usuario, nombre, apellidos, ci, email, telefono, direccion, usuario_sistema, password, estado, fecha_nacimiento) VALUES " +
                    "(1, 'Carlos', 'Roca Arteaga', '4587123', 'admin@tecnoweb.org.bo', '71023456', 'Av. Las Americas #320', 'admin', 'admin123', 'ACTIVO', '1985-04-12'), " +
                    "(2, 'Mariana', 'Vargas Suarez', '5896321', 'programador@tecnoweb.org.bo', '77098412', 'Calle Beni #140', 'programador', 'prog123', 'ACTIVO', '1990-09-25'), " +
                    "(3, 'Fernando', 'Gutierrez Torrico', '6325147', 'contenidos@tecnoweb.org.bo', '75033221', 'Av. Cristobal de Mendoza', 'contenidos', 'cont123', 'ACTIVO', '1988-12-05') " +
                    "ON CONFLICT (id_usuario) DO NOTHING"
            );

            // 3. Asignación Roles
            stmt.executeUpdate(
                    "INSERT INTO usuario_roles (id_usuario, id_rol) VALUES " +
                    "(1, 1), (2, 2), (3, 3) " +
                    "ON CONFLICT (id_usuario, id_rol) DO NOTHING"
            );

            // 4. Categorías
            stmt.executeUpdate(
                    "INSERT INTO categoria_programa (id, nombre, descripcion) VALUES " +
                    "(1, 'NOTICIAS', 'Noticieros centrales, avances informativos y boletines'), " +
                    "(2, 'DEPORTES', 'Transmisiones deportivas, analisis y debates futbolisticos'), " +
                    "(3, 'TELENOVELA', 'Producciones de ficcion dramatica nacional e internacional'), " +
                    "(4, 'ENTRETENIMIENTO', 'Programas de concursos, variedades, magazines matutinos'), " +
                    "(5, 'DOCUMENTAL', 'Contenidos culturales, naturaleza, historia y reportajes'), " +
                    "(6, 'INFANTIL', 'Dibujos animados y series para publico infantil') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 5. Programas
            stmt.executeUpdate(
                    "INSERT INTO programa (id, codigo, titulo, descripcion, id_categoria, clasificacion_edad, duracion_estandar_minutos, origen, estado) VALUES " +
                    "(1, 'NOT-001', 'TeleNoticias Central', 'Edicion central del noticiero con cobertura nacional', 1, 'ATP', 60, 'NACIONAL', 'ACTIVO'), " +
                    "(2, 'NOT-002', 'Al Despertar', 'Magazine matutino con informacion en vivo y entrevistas', 4, 'ATP', 120, 'NACIONAL', 'ACTIVO'), " +
                    "(3, 'DEP-001', 'Zona de Gol', 'Resumen deportivo y analisis de la fecha liguera', 2, 'ATP', 60, 'NACIONAL', 'ACTIVO'), " +
                    "(4, 'NOV-001', 'Caminos Cruzados', 'Telenovela estelar de produccion dramatica', 3, 'TEEN_14', 60, 'INTERNACIONAL', 'ACTIVO'), " +
                    "(5, 'DOC-001', 'Bolivia Profunda', 'Documental sobre biodiversidad y tradiciones del oriente', 5, 'ATP', 60, 'NACIONAL', 'ACTIVO') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 6. Parrilla
            stmt.executeUpdate(
                    "INSERT INTO parrilla (id, fecha, canal_senial, descripcion, id_programador, estado) VALUES " +
                    "(1, CURRENT_DATE, 'SENIAL_PRINCIPAL_HD', 'Parrilla oficial del dia - Transmision Regular', 2, 'APROBADA') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 7. Items Parrilla
            stmt.executeUpdate(
                    "INSERT INTO item_parrilla (id, id_parrilla, id_programa, hora_inicio, hora_fin, tipo_emision, estado, observaciones) VALUES " +
                    "(1, 1, 2, '06:00:00', '08:00:00', 'VIVO', 'PROGRAMADO', 'Emision matutina en directo desde estudio central'), " +
                    "(2, 1, 5, '08:00:00', '09:00:00', 'GRABADO', 'PROGRAMADO', 'Documental ecologico de archivo'), " +
                    "(3, 1, 1, '12:00:00', '13:00:00', 'VIVO', 'PROGRAMADO', 'Noticiero meridiano en directo'), " +
                    "(4, 1, 4, '13:00:00', '14:00:00', 'GRABADO', 'PROGRAMADO', 'Capitulo de estreno telenovela estelar'), " +
                    "(5, 1, 3, '21:00:00', '22:00:00', 'VIVO', 'PROGRAMADO', 'Debate futbolero nocturno') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 8. Materiales de Video
            stmt.executeUpdate(
                    "INSERT INTO material_video (id, id_programa, codigo_material, titulo_clip, formato, resolucion, duracion_segundos, ruta_servidor, estado, id_responsable, observaciones) VALUES " +
                    "(1, 5, 'MAT-DOC-01', 'Bolivia_Profunda_Ep01_Master.mxf', 'HD_1080I', '1920x1080', 3600, '/mam/storage/doc/bolivia_01.mxf', 'LISTO', 3, 'Control de calidad aprobado'), " +
                    "(2, 4, 'MAT-NOV-01', 'CaminosCruzados_Cap45_HD.mp4', 'HD_1080I', '1920x1080', 3480, '/mam/storage/nov/caminos_45.mp4', 'LISTO', 3, 'Master entregado por distribuidora internacional'), " +
                    "(3, 1, 'MAT-NOT-OPEN', 'Intro_TeleNoticias_2026.mp4', 'HD_1080I', '1920x1080', 45, '/mam/storage/not/open_2026.mp4', 'LISTO', 3, 'Cabecera de entrada noticiero') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 9. Validaciones QC
            stmt.executeUpdate(
                    "INSERT INTO validacion_emision (id, id_item_parrilla, id_material_video, id_inspector_qc, resultado_qc, aspect_ratio, nivel_audio_db, observaciones) VALUES " +
                    "(1, 2, 1, 3, 'APROBADO_AIRE', '16:9', -23.80, 'Audio normalizado EBU R128 y video verificado'), " +
                    "(2, 4, 2, 3, 'APROBADO_AIRE', '16:9', -24.10, 'Sincronizacion de audio validada') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 10. Clientes Publicitarios
            stmt.executeUpdate(
                    "INSERT INTO cliente_publicitario (id, razon_social, nit, contacto, email, telefono, estado) VALUES " +
                    "(1, 'Cerveceria Boliviana Nacional S.A.', '1020304050', 'Lic. Rodrigo Banegas', 'publicidad@cbn.bo', '70012345', 'ACTIVO'), " +
                    "(2, 'Banco Ganadero S.A.', '2030405060', 'Lic. Paola Morales', 'marketing@bg.com.bo', '70098765', 'ACTIVO'), " +
                    "(3, 'Telefonia Tigo Bolivia', '3040506070', 'Ing. Gabriel Antelo', 'pautas@tigo.net.bo', '71055443', 'ACTIVO') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 11. Spots Comerciales
            stmt.executeUpdate(
                    "INSERT INTO spot_publicitario (id, id_cliente, codigo_spot, titulo_spot, duracion_segundos, fecha_vigencia_inicio, fecha_vigencia_fin, estado) VALUES " +
                    "(1, 1, 'SPOT-CBN-01', 'Pacena Tradicion Carnaval 30s', 30, '2026-01-01', '2026-12-31', 'ACTIVO'), " +
                    "(2, 2, 'SPOT-BG-01', 'GanaMovil Tu Banco Seguro 20s', 20, '2026-01-01', '2026-12-31', 'ACTIVO'), " +
                    "(3, 3, 'SPOT-TIG-01', 'Tigo Hogar Fibra Optica 30s', 30, '2026-01-01', '2026-12-31', 'ACTIVO') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 12. Pautas Comerciales (Playout Rundown de Cortes)
            stmt.executeUpdate(
                    "INSERT INTO pauta_comercial (id, id_item_parrilla, id_spot, tanda_bloque, orden, hora_estimada, estado) VALUES " +
                    "(1, 3, 1, 'CORTE_1', 1, '12:15:00', 'PAUTADO'), " +
                    "(2, 3, 2, 'CORTE_1', 2, '12:15:30', 'PAUTADO'), " +
                    "(3, 4, 3, 'CORTE_2', 1, '13:30:00', 'PAUTADO') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 13. Emisión Real Log
            stmt.executeUpdate(
                    "INSERT INTO emision_real_log (id, id_item_parrilla, id_material_video, hora_inicio_real, hora_fin_real, duracion_real_segundos, discrepancia_segundos, estado_emision, operador_emision, observaciones) VALUES " +
                    "(1, 1, NULL, CURRENT_DATE + TIME '06:00:02', CURRENT_DATE + TIME '08:00:00', 7198, -2, 'EMITIDO_CORRECTO', 'Operador Master 01', 'Transmision en vivo finalizada a tiempo'), " +
                    "(2, 2, 1, CURRENT_DATE + TIME '08:00:01', CURRENT_DATE + TIME '09:00:01', 3600, 0, 'EMITIDO_CORRECTO', 'Operador Master 01', 'Archivo emitido sin discrepancias') " +
                    "ON CONFLICT (id) DO NOTHING"
            );

            // 14. Auditoría Inicial
            stmt.executeUpdate(
                    "INSERT INTO auditoria_sistema (id_usuario, tabla_afectada, accion, detalle) VALUES " +
                    "(1, 'parrilla', 'APROBACION', 'Aprobacion de parrilla diaria para emision'), " +
                    "(2, 'pauta_comercial', 'INSERCION', 'Pautado de spots CBN y Banco Ganadero en noticiero') " +
                    "ON CONFLICT DO NOTHING"
            );

            System.out.println("[OK] Datos iniciales de television insertados correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al insertar datos iniciales: " + e.getMessage());
        } finally {
            cerrarRecursos(stmt, con);
        }
    }

    private static void cerrarRecursos(Statement stmt, Connection con) {
        try {
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException ignored) {
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Inicializando Base de Datos de Parrilla de Television ---");
        if (args != null && args.length > 0 && "--reset".equalsIgnoreCase(args[0])) {
            eliminarTodasLasTablas();
        }
        crearTablasSiNoExisten();
        insertarDatosIniciales();
        System.out.println("--- Proceso Finalizado con Exito ---");
    }
}