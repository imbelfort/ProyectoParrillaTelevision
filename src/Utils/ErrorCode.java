package Utils;

/**
 * Catálogo centralizado y estandarizado de códigos de error del sistema de Playout.
 * Clasifica los errores en 5 categorías explícitas:
 * 1. ERR-CMD-xxx: Comando Mal Introducido (Sintaxis y Léxico)
 * 2. ERR-ORD-xxx: Orden Mal Ordenada (Secuencia y Estructura)
 * 3. ERR-INC-xxx: Orden Incompleta (Completitud)
 * 4. ERR-FMT-xxx: Formato y Tipo de Datos Inválido (Tipado)
 * 5. ERR-BD-xxx / ERR-NEG-xxx: Reglas de Negocio y Base de Datos (Semántica)
 */
public class ErrorCode {

    // =========================================================================
    // CATEGORÍA 1: COMANDO MAL INTRODUCIDO (SINTAXIS Y LÉXICO)
    // =========================================================================
    public static final String ERR_CMD_001 = "ERR-CMD-001"; // Comando Vacío (Asunto en blanco o espacios)
    public static final String ERR_CMD_002 = "ERR-CMD-002"; // Entidad / Caso de Uso No Reconocido o Mal Escrito
    public static final String ERR_CMD_003 = "ERR-CMD-003"; // Acción No Reconocida para la Entidad
    public static final String ERR_CMD_004 = "ERR-CMD-004"; // Carácter Inválido o Símbolo No Permitido

    // =========================================================================
    // CATEGORÍA 2: ORDEN MAL ORDENADA (SECUENCIA Y ESTRUCTURA)
    // =========================================================================
    public static final String ERR_ORD_001 = "ERR-ORD-001"; // Orden Invertido: Acción antes de Entidad (ej: 'listar usuario')
    public static final String ERR_ORD_002 = "ERR-ORD-002"; // Orden Invertido: Parámetro antes de Entidad (ej: '1 usuario consultar')
    public static final String ERR_ORD_003 = "ERR-ORD-003"; // Parámetros Desordenados o Invertidos

    // =========================================================================
    // CATEGORÍA 3: ORDEN INCOMPLETA (COMPLETITUD)
    // =========================================================================
    public static final String ERR_INC_001 = "ERR-INC-001"; // Orden Incompleta: Falta la Acción (solo se ingresó entidad)
    public static final String ERR_INC_002 = "ERR-INC-002"; // Orden Incompleta: Faltan Parámetros Obligatorios
    public static final String ERR_INC_003 = "ERR-INC-003"; // Orden Incompleta: Parámetros Insuficientes / Faltan Campos

    // =========================================================================
    // CATEGORÍA 4: FORMATO Y TIPO DE DATOS INVÁLIDO
    // =========================================================================
    public static final String ERR_FMT_001 = "ERR-FMT-001"; // Se esperaba un Número Entero (ID numérico)
    public static final String ERR_FMT_002 = "ERR-FMT-002"; // Formato de Fecha Inválido (requiere YYYY-MM-DD)
    public static final String ERR_FMT_003 = "ERR-FMT-003"; // Formato de Hora Inválido (requiere HH:mm militar)
    public static final String ERR_FMT_004 = "ERR-FMT-004"; // Estado / Enum de Dominio Inválido
    public static final String ERR_FMT_005 = "ERR-FMT-005"; // Formato de Correo Electrónico Inválido

    // =========================================================================
    // CATEGORÍA 5: REGLAS DE NEGOCIO Y BASE DE DATOS
    // =========================================================================
    public static final String ERR_NEG_001 = "ERR-NEG-001"; // Registro No Encontrado (ID no existe en BD)
    public static final String ERR_BD_002  = "ERR-BD-002";  // Clave Foránea No Existe (Dependencia faltante)
    public static final String ERR_BD_003  = "ERR-BD-003";  // Clave Duplicada (Código, NIT o Email ya registrado)
    public static final String ERR_BD_004  = "ERR-BD-004";  // Restricción de Integridad / Registro en Uso
    public static final String ERR_BD_005  = "ERR-BD-005";  // Error Interno de Base de Datos / SQL
    public static final String ERR_SYS_001 = "ERR-SYS-001"; // Error Interno No Controlado

    /**
     * Retorna un título corto explicativo del código de error.
     */
    public static String getDescripcionCorta(String codigo) {
        if (codigo == null) return "Error Desconocido";
        switch (codigo) {
            case ERR_CMD_001: return "Comando Vacío";
            case ERR_CMD_002: return "Entidad No Reconocida";
            case ERR_CMD_003: return "Acción No Reconocida";
            case ERR_CMD_004: return "Carácter No Permitido";
            case ERR_ORD_001: return "Orden Invertido (Acción antes de Entidad)";
            case ERR_ORD_002: return "Orden Invertido (Parámetro antes de Entidad)";
            case ERR_ORD_003: return "Parámetros Desordenados";
            case ERR_INC_001: return "Orden Incompleta (Falta la Acción)";
            case ERR_INC_002: return "Orden Incompleta (Faltan Parámetros)";
            case ERR_INC_003: return "Parámetros Insuficientes";
            case ERR_FMT_001: return "Formato Numérico Inválido";
            case ERR_FMT_002: return "Formato de Fecha Inválido";
            case ERR_FMT_003: return "Formato de Hora Inválido";
            case ERR_FMT_004: return "Estado de Dominio Inválido";
            case ERR_FMT_005: return "Formato de Email Inválido";
            case ERR_NEG_001: return "Registro No Encontrado";
            case ERR_BD_002:  return "Dependencia No Encontrada (FK)";
            case ERR_BD_003:  return "Registro Duplicado (Unique)";
            case ERR_BD_004:  return "Registro en Uso (Integridad)";
            case ERR_BD_005:  return "Error de Base de Datos";
            case ERR_SYS_001: return "Error Interno del Sistema";
            default:          return "Error de Operación (" + codigo + ")";
        }
    }
}
