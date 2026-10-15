package Comunication;

import java.util.List;

public class HtmlBuilder {

    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";
    private static final String BODY_OPEN = "<body style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #0f172a; color: #f8fafc; margin: 0; padding: 24px;'>";
    private static final String BODY_CLOSE = "</body>";

    // ============================================================
    // CABECERAS PARA CASOS DE USO (PARRILLA DE TELEVISIÓN)
    // ============================================================

    // CU1
    public static final String[] HEADERS_USER = {
            "ID", "NOMBRE", "APELLIDOS", "CI", "EMAIL", "TELEFONO", "DIRECCION",
            "FOTO", "ESTADO", "PASSWORD", "FECHA_NAC", "CREADO", "MODIFICADO"
    };

    public static final String[] HEADERS_ROL = {
            "ID", "NOMBRE", "DESCRIPCION", "ESTADO", "FECHA_CREACION"
    };

    // CU2
    public static final String[] HEADERS_CATEGORIA = {
            "ID", "NOMBRE", "DESCRIPCION", "ESTADO"
    };

    public static final String[] HEADERS_PROGRAMA = {
            "ID", "CODIGO", "TITULO", "CATEGORIA", "CLASIFICACION", "DURACION", "ORIGEN", "ESTADO", "FECHA_REGISTRO"
    };

    // CU3
    public static final String[] HEADERS_PARRILLA = {
            "ID", "FECHA", "CANAL / SEÑAL", "DESCRIPCION", "PROGRAMADOR", "ESTADO", "CREACION"
    };

    public static final String[] HEADERS_ITEM_PARRILLA = {
            "ID", "ID_PARRILLA", "COD_PROG", "PROGRAMA", "HORA_INICIO", "HORA_FIN", "TIPO_EMISION", "ESTADO", "OBSERVACIONES"
    };

    // CU4
    public static final String[] HEADERS_MATERIAL_VIDEO = {
            "ID", "PROGRAMA", "COD_MATERIAL", "TITULO_CLIP", "FORMATO", "RESOLUCION", "DURACION", "ESTADO", "RESPONSABLE", "FECHA_INGESTA"
    };

    // CU5
    public static final String[] HEADERS_VALIDACION = {
            "ID", "PROGRAMA", "HORA_PROG", "RESULTADO_QC", "ASPECT_RATIO", "NIVEL_AUDIO", "INSPECTOR", "OBSERVACIONES", "FECHA_QC"
    };

    public static final String[] HEADERS_VALIDACION_PENDIENTES = {
            "ID_ITEM", "FECHA", "PROGRAMA", "HORA_INICIO", "HORA_FIN", "TIPO_EMISION", "ESTADO"
    };

    // CU6
    public static final String[] HEADERS_CLIENTE = {
            "ID", "RAZON_SOCIAL", "NIT", "CONTACTO", "EMAIL", "TELEFONO", "ESTADO"
    };

    public static final String[] HEADERS_SPOT = {
            "ID", "CLIENTE", "COD_SPOT", "TITULO_SPOT", "DURACION", "VIGENCIA_INICIO", "VIGENCIA_FIN", "ESTADO"
    };

    public static final String[] HEADERS_PAUTA = {
            "ID", "ID_ITEM", "PROGRAMA", "COD_SPOT", "TITULO_SPOT", "TANDA_BLOQUE", "ORDEN", "HORA_ESTIMADA", "ESTADO"
    };

    // CU7
    public static final String[] HEADERS_EMISION_REAL = {
            "ID", "PROGRAMA", "HORA_PROG", "INICIO_REAL", "FIN_REAL", "DURACION_REAL", "DISCREPANCIA", "ESTADO_EMISION", "OPERADOR", "FECHA_REGISTRO"
    };

    public static final String[] HEADERS_DISCREPANCIAS = {
            "ID", "PROGRAMA", "HORA_PROG", "INICIO_REAL", "DISCREPANCIA", "ESTADO_EMISION", "OPERADOR", "OBSERVACIONES"
    };

    public static final String[] HEADERS_AUDITORIA = {
            "ID", "USUARIO", "TABLA_AFECTADA", "ACCION", "DETALLE", "FECHA_EVENTO"
    };

    // CU8
    public static final String[] HEADERS_REPORTE_GENERAL = {
            "INDICADOR / METRICA", "VALOR CONSOLIDADO"
    };

    public static final String[] HEADERS_REPORTE_PARRILLA = {
            "FECHA", "CANAL / SEÑAL", "PROGRAMA", "HORA_INICIO", "HORA_FIN", "TIPO_EMISION", "ESTADO"
    };

    public static final String[] HEADERS_REPORTE_PROGRAMAS = {
            "CATEGORIA", "TOTAL_PROGRAMAS", "DURACION_PROMEDIO"
    };

    public static final String[] HEADERS_REPORTE_PAUTAS = {
            "CLIENTE / ANUNCIANTE", "TOTAL_SPOTS_PAUTADOS", "TIEMPO_TOTAL_AIRE"
    };

    public static final String[] HEADERS_REPORTE_CUMPLIMIENTO = {
            "ESTADO_EMISION", "CANTIDAD_TRANSMISIONES", "PROMEDIO_DISCREPANCIA"
    };

    // ============================================================
    // GENERADORES HTML - ESTÉTICA BROADCAST PLAYOUT
    // ============================================================

    public static String generarTabla(String titulo, String[] headers, List<String[]> filas) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_OPEN).append(BODY_OPEN);

        sb.append("<div style='max-width: 980px; margin: 10px auto; background: #0d131f; border-radius: 12px; padding: 24px; box-shadow: 0 16px 36px rgba(0,0,0,0.5); border: 1px solid #1e293b; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif;'>");

        // Top Brand Header
        sb.append("<div style='border-bottom: 2px solid #0284c7; padding-bottom: 16px; margin-bottom: 20px;'>");
        sb.append("<div style='font-size: 11px; font-weight: 700; color: #38bdf8; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 4px;'>📡 FICCT BROADCAST • PLAYOUT AUTOMATION • GRUPO 12 SC</div>");
        sb.append("<div style='display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px;'>");
        sb.append("<h2 style='margin: 0; color: #ffffff; font-size: 22px; font-weight: 800; letter-spacing: -0.5px;'>").append(titulo).append("</h2>");
        sb.append("<span style='background: linear-gradient(135deg, #0284c7 0%, #2563eb 100%); color: #ffffff; padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: 700; letter-spacing: 0.5px;'>")
                .append(filas != null ? filas.size() : 0).append(" REGISTROS</span>");
        sb.append("</div>");
        sb.append("</div>");

        // Table Wrapper
        sb.append("<div style='overflow-x: auto; border-radius: 8px; border: 1px solid #1e293b;'>");
        sb.append("<table style='width: 100%; border-collapse: collapse; font-size: 13px; text-align: left;'>");

        // Table Headers
        sb.append("<thead><tr style='background: #080d1a;'>");
        for (String h : headers) {
            sb.append("<th style='padding: 12px 14px; color: #94a3b8; font-weight: 700; border-bottom: 2px solid #0284c7; text-transform: uppercase; font-size: 11px; letter-spacing: 0.8px;'>")
                    .append(h).append("</th>");
        }
        sb.append("</tr></thead>");

        // Table Rows
        sb.append("<tbody>");
        if (filas == null || filas.isEmpty()) {
            sb.append("<tr><td colspan='").append(headers.length)
                    .append("' style='text-align: center; padding: 36px; color: #64748b; font-size: 14px; font-style: italic;'>No se encontraron registros disponibles en la base de datos.</td></tr>");
        } else {
            boolean alternate = false;
            for (String[] fila : filas) {
                String bg = alternate ? "#111827" : "#172033";
                alternate = !alternate;
                sb.append("<tr style='background-color: ").append(bg).append(";'>");
                for (String celda : fila) {
                    String valor = celda != null ? celda : "";
                    sb.append("<td style='padding: 11px 14px; border-bottom: 1px solid #1e293b; color: #f1f5f9;'>")
                            .append(formatearCelda(valor)).append("</td>");
                }
                sb.append("</tr>");
            }
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");

        // Footer
        sb.append("<div style='margin-top: 20px; padding-top: 12px; border-top: 1px solid #1e293b; font-size: 11px; color: #64748b; display: flex; justify-content: space-between;'>");
        sb.append("<span>FICCT • UAGRM — Cabina Master Control</span>");
        sb.append("<span>Respuesta automática vía Servidor de Correo (POP3/SMTP)</span>");
        sb.append("</div>");

        sb.append("</div>");
        sb.append(BODY_CLOSE).append(HTML_CLOSE);
        return sb.toString();
    }

    public static String generarMensajeExito(String titulo, String mensaje) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_OPEN).append(BODY_OPEN);
        sb.append("<div style='max-width: 650px; margin: 30px auto; background: #0d131f; border-radius: 12px; padding: 24px; border-left: 6px solid #10b981; border: 1px solid #1e293b; border-left-width: 6px; box-shadow: 0 16px 36px rgba(0,0,0,0.5); font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif;'>");
        sb.append("<div style='font-size: 11px; font-weight: 700; color: #34d399; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 6px;'>✔ OPERACIÓN EXITOSA</div>");
        sb.append("<h2 style='margin: 0 0 10px 0; color: #ffffff; font-size: 20px; font-weight: 800;'>").append(titulo).append("</h2>");
        sb.append("<p style='margin: 0; color: #cbd5e1; font-size: 14px; line-height: 1.6;'>").append(mensaje).append("</p>");
        sb.append("<div style='margin-top: 18px; font-size: 11px; color: #64748b; border-top: 1px solid #1e293b; padding-top: 10px;'>Sistema de Parrilla de Televisión — Registro actualizado en el Playout</div>");
        sb.append("</div>");
        sb.append(BODY_CLOSE).append(HTML_CLOSE);
        return sb.toString();
    }

    public static String generarMensajeErrorDetallado(String titulo, String comandoRecibido, String queFallo, String sugerencia) {
        return generarMensajeErrorDetallado("ERR-CMD", titulo, comandoRecibido, queFallo, sugerencia);
    }

    public static String generarMensajeErrorDetallado(String codigoError, String titulo, String comandoRecibido, String queFallo, String sugerencia) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_OPEN).append(BODY_OPEN);
        sb.append("<div style='max-width: 680px; margin: 30px auto; background: #0d131f; border-radius: 12px; padding: 26px; border: 1px solid #1e293b; border-left: 6px solid #ef4444; box-shadow: 0 16px 36px rgba(0,0,0,0.5); font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif;'>");
        
        sb.append("<div style='display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; flex-wrap: wrap; gap: 8px;'>");
        sb.append("<div style='font-size: 11px; font-weight: 700; color: #f87171; letter-spacing: 1.5px; text-transform: uppercase;'>✖ ERROR EN COMANDO DE PLAYOUT</div>");
        if (codigoError != null && !codigoError.trim().isEmpty()) {
            sb.append("<span style='background: #dc2626; color: #ffffff; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 800; letter-spacing: 0.8px; font-family: Consolas, Monaco, monospace;'>[")
              .append(codigoError).append("]</span>");
        }
        sb.append("</div>");

        sb.append("<h2 style='margin: 0 0 14px 0; color: #ffffff; font-size: 20px; font-weight: 800;'>").append(titulo).append("</h2>");

        sb.append("<div style='margin-bottom: 14px; padding: 12px 14px; background: #080d1a; border-radius: 8px; border: 1px solid #1e293b; font-family: Consolas, Monaco, monospace; font-size: 13px; color: #fbbf24;'>");
        sb.append("<span style='color: #94a3b8;'>Comando recibido:</span> <strong>").append(comandoRecibido != null && !comandoRecibido.isEmpty() ? comandoRecibido : "<i>(asunto vacío)</i>").append("</strong>");
        sb.append("</div>");

        sb.append("<div style='background: #1e131d; border: 1px solid #7f1d1d; border-radius: 8px; padding: 14px; margin-bottom: 14px;'>");
        sb.append("<p style='color: #fca5a5; font-size: 14px; line-height: 1.5; margin: 0;'><strong>⚠️ Detalle del Error:</strong> ").append(queFallo).append("</p>");
        sb.append("</div>");

        if (sugerencia != null && !sugerencia.trim().isEmpty()) {
            sb.append("<div style='padding: 14px; background: #0c1b33; border-radius: 8px; border-left: 4px solid #38bdf8; color: #e0f2fe; font-size: 13px; line-height: 1.6;'>");
            sb.append("<strong style='color: #38bdf8;'>💡 Formato correcto esperado:</strong><br>");
            sb.append("<pre style='margin: 8px 0 0 0; font-family: Consolas, Monaco, monospace; font-size: 12px; color: #bae6fd; white-space: pre-wrap;'>").append(sugerencia).append("</pre>");
            sb.append("</div>");
        }

        sb.append("<div style='margin-top: 18px; font-size: 11px; color: #64748b; border-top: 1px solid #1e293b; padding-top: 10px; display: flex; justify-content: space-between;'>");
        sb.append("<span>Envía <b>help</b> en el Asunto del correo para ver el manual completo.</span>");
        sb.append("<span>Código: <b style='color: #f87171;'>").append(codigoError != null ? codigoError : "ERR-GENERAL").append("</b></span>");
        sb.append("</div>");

        sb.append("</div>");
        sb.append(BODY_CLOSE).append(HTML_CLOSE);
        return sb.toString();
    }

    public static String generarMenuAyudaHtml(String contenidoAyuda) {
        StringBuilder sb = new StringBuilder();
        sb.append(HTML_OPEN).append(BODY_OPEN);
        sb.append("<div style='max-width: 920px; margin: 10px auto; background: #0d131f; border-radius: 12px; padding: 26px; border: 1px solid #1e293b; box-shadow: 0 16px 36px rgba(0,0,0,0.5); font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif;'>");
        sb.append("<div style='font-size: 11px; font-weight: 700; color: #38bdf8; letter-spacing: 1.5px; text-transform: uppercase; margin-bottom: 4px;'>📡 SISTEMA DE PARRILLA DE TELEVISIÓN — FICCT UAGRM</div>");
        sb.append("<h2 style='margin: 0 0 8px 0; color: #ffffff; font-size: 22px; font-weight: 800;'>📺 Manual de Comandos de Playout (Grupo 12 SC)</h2>");
        sb.append("<p style='margin: 0 0 16px 0; color: #94a3b8; font-size: 13px;'>Escriba cualquiera de los comandos en el <strong>ASUNTO (Subject)</strong> del correo:</p>");
        sb.append("<pre style='background: #080d1a; color: #e2e8f0; padding: 20px; border-radius: 8px; border: 1px solid #1e293b; font-family: Consolas, Monaco, monospace; font-size: 13px; line-height: 1.6; overflow-x: auto; white-space: pre-wrap;'>")
                .append(contenidoAyuda).append("</pre>");
        sb.append("<div style='margin-top: 18px; font-size: 11px; color: #64748b; text-align: right;'>Ejemplo rápido de prueba: envíe <b>parrilla al_aire</b> o <b>reporte general</b></div>");
        sb.append("</div>");
        sb.append(BODY_CLOSE).append(HTML_CLOSE);
        return sb.toString();
    }

    private static String formatearCelda(String valor) {
        if ("ACTIVO".equalsIgnoreCase(valor) || "APROBADA".equalsIgnoreCase(valor) || "APROBADO_AIRE".equalsIgnoreCase(valor) || "EMITIDO_CORRECTO".equalsIgnoreCase(valor) || "LISTO".equalsIgnoreCase(valor)) {
            return "<span style='background: #059669; color: #ffffff; padding: 3px 9px; border-radius: 6px; font-size: 11px; font-weight: 700;'>✔ " + valor + "</span>";
        }
        if ("BORRADOR".equalsIgnoreCase(valor) || "PENDIENTE".equalsIgnoreCase(valor) || "PROGRAMADO".equalsIgnoreCase(valor) || "PAUTADO".equalsIgnoreCase(valor) || "INGESTADO".equalsIgnoreCase(valor)) {
            return "<span style='background: #d97706; color: #ffffff; padding: 3px 9px; border-radius: 6px; font-size: 11px; font-weight: 700;'>⏳ " + valor + "</span>";
        }
        if ("CANCELADO".equalsIgnoreCase(valor) || "INACTIVO".equalsIgnoreCase(valor) || "FALLA_TECNICA".equalsIgnoreCase(valor) || "CORTE_ENERGIA".equalsIgnoreCase(valor) || valor.startsWith("RECHAZADO")) {
            return "<span style='background: #b91c1c; color: #ffffff; padding: 3px 9px; border-radius: 6px; font-size: 11px; font-weight: 700;'>✖ " + valor + "</span>";
        }
        if ("VIVO".equalsIgnoreCase(valor) || "[EN VIVO]".equalsIgnoreCase(valor)) {
            return "<span style='background: #dc2626; color: #ffffff; padding: 3px 9px; border-radius: 6px; font-size: 11px; font-weight: 800; letter-spacing: 0.5px;'>🔴 EN VIVO</span>";
        }
        if (valor.startsWith("CORTE_")) {
            return "<span style='background: #3730a3; color: #e0e7ff; padding: 3px 8px; border-radius: 6px; font-size: 11px; font-weight: 600;'>🎬 " + valor + "</span>";
        }
        return valor;
    }
}