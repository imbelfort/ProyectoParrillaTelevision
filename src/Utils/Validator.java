package Utils;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

/**
 * Validador centralizado de datos y parámetros para el sistema de Playout.
 * Asegura la integridad de los datos, previene caracteres especiales peligrosos
 * que rompan el flujo, y emite excepciones CommandException con códigos de error
 * explícitos (ERR-FMT-xxx, ERR-INC-xxx, ERR-CMD-xxx).
 */
public class Validator {

    /**
     * Valida y convierte un ID numérico entero positivo (> 0).
     */
    public static int validarId(String valor, String nombreCampo, String sugerencia) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "Falta el identificador obligatorio '" + nombreCampo + "'.",
                    sugerencia);
        }
        String v = valor.trim();
        try {
            int id = Integer.parseInt(v);
            if (id <= 0) {
                throw new CommandException(ErrorCode.ERR_FMT_001,
                        "El campo '" + nombreCampo + "' debe ser un número entero positivo mayor a 0 (se recibió: '" + v + "').",
                        sugerencia);
            }
            return id;
        } catch (NumberFormatException e) {
            throw new CommandException(ErrorCode.ERR_FMT_001,
                    "El valor '" + v + "' no es un número válido para el campo '" + nombreCampo + "'. Ingrese un número entero positivo.",
                    sugerencia);
        }
    }

    /**
     * Valida códigos alfanuméricos (ej: PROG-01, CLIP-77, SPOT-03).
     * No permite espacios ni caracteres especiales que rompan el flujo.
     */
    public static String validarCodigo(String codigo, String nombreCampo, String sugerencia) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "El campo '" + nombreCampo + "' es obligatorio y no puede estar vacío.",
                    sugerencia);
        }
        String c = codigo.trim().toUpperCase();
        // Solo permite letras, dígitos, guión medio y guión bajo
        if (!c.matches("^[A-Z0-9_-]{2,30}$")) {
            throw new CommandException(ErrorCode.ERR_CMD_004,
                    "El código '" + codigo.trim() + "' contiene caracteres inválidos para '" + nombreCampo + "'. Solo se permiten letras, números y guiones (-) sin espacios ni caracteres especiales.",
                    sugerencia);
        }
        return c;
    }

    /**
     * Valida nombres o textos de entidad (ej: nombre de persona, rol, categoría).
     * No permite caracteres que rompan el flujo (<, >, ;, |, control chars).
     */
    public static String validarTextoSeguro(String texto, String nombreCampo, int minLen, int maxLen, String sugerencia) {
        if (texto == null || texto.trim().isEmpty()) {
            if (minLen > 0) {
                throw new CommandException(ErrorCode.ERR_INC_002,
                        "El campo '" + nombreCampo + "' es obligatorio y no puede estar vacío.",
                        sugerencia);
            }
            return "";
        }
        String t = texto.trim();
        if (t.length() < minLen || t.length() > maxLen) {
            throw new CommandException(ErrorCode.ERR_FMT_004,
                    "La longitud del campo '" + nombreCampo + "' debe estar entre " + minLen + " y " + maxLen + " caracteres.",
                    sugerencia);
        }
        // Rechazar caracteres peligrosos (<, >, ;, |, control chars, etc.)
        if (t.matches(".*[<>;|\\\\^~`\0\r\n].*")) {
            throw new CommandException(ErrorCode.ERR_CMD_004,
                    "El campo '" + nombreCampo + "' contiene caracteres especiales no permitidos (<, >, ;, |, etc.) que pueden afectar el procesamiento.",
                    sugerencia);
        }
        return sanitizar(t);
    }

    /**
     * Valida formato de correo electrónico.
     */
    public static String validarEmail(String email, String sugerencia) {
        if (email == null || email.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "El correo electrónico es obligatorio.",
                    sugerencia);
        }
        String em = email.trim().toLowerCase();
        String patron = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!em.matches(patron)) {
            throw new CommandException(ErrorCode.ERR_FMT_005,
                    "El correo electrónico '" + email.trim() + "' tiene un formato inválido. Debe tener la estructura usuario@dominio.com.",
                    sugerencia);
        }
        return em;
    }

    /**
     * Valida formato de teléfono (solo dígitos y prefijo opcional +).
     */
    public static String validarTelefono(String telefono, String sugerencia) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "70000000";
        }
        String tel = telefono.trim();
        if (!tel.matches("^\\+?[0-9]{7,15}$")) {
            throw new CommandException(ErrorCode.ERR_FMT_001,
                    "El teléfono '" + tel + "' es inválido. Solo debe contener números (7 a 15 dígitos) y opcionalmente el prefijo '+'.",
                    sugerencia);
        }
        return tel;
    }

    /**
     * Valida formato de documento de identidad (CI / NIT).
     */
    public static String validarDocumento(String doc, String nombreCampo, String sugerencia) {
        if (doc == null || doc.trim().isEmpty()) {
            return String.valueOf(System.currentTimeMillis() % 10000000);
        }
        String d = doc.trim().toUpperCase();
        if (!d.matches("^[A-Z0-9-]{4,20}$")) {
            throw new CommandException(ErrorCode.ERR_CMD_004,
                    "El campo '" + nombreCampo + "' ('" + doc.trim() + "') contiene caracteres inválidos. Solo se permiten números, letras y guión medio.",
                    sugerencia);
        }
        return d;
    }

    /**
     * Valida y convierte fechas en formato estricto YYYY-MM-DD.
     */
    public static Date validarFecha(String fechaStr, String nombreCampo, String sugerencia) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "La fecha de '" + nombreCampo + "' es obligatoria.",
                    sugerencia);
        }
        String f = fechaStr.trim();
        if (!f.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new CommandException(ErrorCode.ERR_FMT_002,
                    "Formato de fecha inválido para '" + f + "'. Se requiere el formato YYYY-MM-DD (ejemplo: 2026-11-28).",
                    sugerencia);
        }
        try {
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);
            java.time.LocalDate localDate = java.time.LocalDate.parse(f, dtf);
            return Date.valueOf(localDate);
        } catch (Exception e) {
            throw new CommandException(ErrorCode.ERR_FMT_002,
                    "La fecha '" + f + "' no representa una fecha válida en el calendario (use formato YYYY-MM-DD).",
                    sugerencia);
        }
    }

    /**
     * Valida y convierte horas en formato HH:mm o HH:mm:ss militar (24h).
     */
    public static Time validarHora(String horaStr, String nombreCampo, String sugerencia) {
        if (horaStr == null || horaStr.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "El horario de '" + nombreCampo + "' es obligatorio.",
                    sugerencia);
        }
        String h = horaStr.trim();
        if (h.length() == 1 || h.length() == 2) {
            h = (h.length() == 1 ? "0" + h : h) + ":00:00";
        } else if (h.length() == 5) {
            h += ":00";
        }
        if (!h.matches("^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$")) {
            throw new CommandException(ErrorCode.ERR_FMT_003,
                    "Formato de hora inválido para '" + horaStr.trim() + "'. Debe estar en formato militar de 24 horas HH:mm o HH:mm:ss (ejemplo: 14:30 o 08:00).",
                    sugerencia);
        }
        try {
            return Time.valueOf(h);
        } catch (Exception e) {
            throw new CommandException(ErrorCode.ERR_FMT_003,
                    "La hora '" + horaStr.trim() + "' no es una hora militar válida (HH:mm de 00:00 a 23:59).",
                    sugerencia);
        }
    }

    /**
     * Valida orden cronológico de horarios en un bloque de parrilla.
     */
    public static void validarHorarioBloque(Time inicio, Time fin, String sugerencia) {
        if (inicio != null && fin != null) {
            if (!fin.after(inicio)) {
                throw new CommandException(ErrorCode.ERR_ORD_003,
                        "La hora de fin (" + fin + ") debe ser posterior a la hora de inicio (" + inicio + ").",
                        sugerencia);
            }
        }
    }

    /**
     * Valida duración positiva en minutos o segundos.
     */
    public static int validarDuracion(String valor, String nombreCampo, String sugerencia) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new CommandException(ErrorCode.ERR_INC_002,
                    "La duración en '" + nombreCampo + "' es obligatoria.",
                    sugerencia);
        }
        String v = valor.trim();
        try {
            int dur = Integer.parseInt(v);
            if (dur <= 0) {
                throw new CommandException(ErrorCode.ERR_FMT_001,
                        "La duración (" + nombreCampo + ") debe ser un número entero mayor a 0 (se recibió: " + dur + ").",
                        sugerencia);
            }
            return dur;
        } catch (NumberFormatException e) {
            throw new CommandException(ErrorCode.ERR_FMT_001,
                    "El valor '" + v + "' no es un número entero válido para la duración (" + nombreCampo + ").",
                    sugerencia);
        }
    }

    /**
     * Valida que un valor pertenezca a un conjunto de estados o enumeradores permitidos.
     */
    public static String validarEstado(String valor, String[] permitidos, String nombreCampo, String sugerencia) {
        if (valor == null || valor.trim().isEmpty()) {
            return permitidos[0]; // valor por defecto
        }
        String v = valor.trim().toUpperCase();
        for (String p : permitidos) {
            if (p.equalsIgnoreCase(v)) {
                return p;
            }
        }
        throw new CommandException(ErrorCode.ERR_FMT_004,
                "El valor '" + valor.trim() + "' no es válido para '" + nombreCampo + "'. Opciones válidas: [" + String.join(", ", permitidos) + "].",
                sugerencia);
    }

    /**
     * Sanitiza cadenas generales para evitar inyecciones o caracteres de control.
     */
    public static String sanitizar(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("[\0\r\n\t]", " ")
                .replaceAll("<[^>]*>", "") // Elimina cualquier etiqueta HTML residual
                .replaceAll("\\s+", " ")
                .trim();
    }
}
