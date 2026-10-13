package Services;

import DAO.ValidacionEmisionDAO;
import Models.ValidacionEmision;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ValidacionEmisionService {

    private ValidacionEmisionDAO dao;

    public ValidacionEmisionService() {
        this.dao = new ValidacionEmisionDAO();
    }

    public void aprobarValidacion(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Uso: validacion aprobar id_item_parrilla");
        }
        ValidacionEmision v = new ValidacionEmision();
        v.setIdItemParrilla(Integer.parseInt(params.get(0).trim()));
        v.setIdInspectorQc(3);
        v.setResultadoQc("APROBADO_AIRE");
        v.setAspectRatio("16:9");
        v.setNivelAudioDb(new BigDecimal("-24.00"));
        v.setObservaciones(params.size() > 1 ? params.get(1).trim() : "Control de calidad aprobado para emision");
        dao.create(v);
    }

    public void rechazarValidacion(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Uso: validacion rechazar id_item_parrilla [motivo]");
        }
        ValidacionEmision v = new ValidacionEmision();
        v.setIdItemParrilla(Integer.parseInt(params.get(0).trim()));
        v.setIdInspectorQc(3);
        v.setResultadoQc("RECHAZADO_FALLA_AUDIO");
        v.setAspectRatio("16:9");
        v.setNivelAudioDb(new BigDecimal("-24.00"));
        v.setObservaciones(params.size() > 1 ? params.get(1).trim() : "Rechazado en control tecnico");
        dao.create(v);
    }

    public void registrarValidacion(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException(
                    "Formato: validacion registrar id_item,resultado(APROBADO_AIRE|RECHAZADO_FALLA_AUDIO|RECHAZADO_FALLA_VIDEO|OBSERVADO) [observaciones]"
            );
        }
        ValidacionEmision v = new ValidacionEmision();
        v.setIdItemParrilla(Integer.parseInt(params.get(0).trim()));
        
        if (params.size() == 2) {
            v.setIdInspectorQc(3);
            v.setResultadoQc(params.get(1).trim().toUpperCase());
            v.setObservaciones("Validacion manual");
        } else if (params.size() == 3) {
            v.setIdInspectorQc(3);
            v.setResultadoQc(params.get(1).trim().toUpperCase());
            v.setObservaciones(params.get(2).trim());
        } else {
            String matStr = params.get(1).trim();
            if (!matStr.isEmpty() && !"null".equalsIgnoreCase(matStr) && !"0".equals(matStr)) {
                v.setIdMaterialVideo(Integer.parseInt(matStr));
            }
            v.setIdInspectorQc(Integer.parseInt(params.get(2).trim()));
            v.setResultadoQc(params.get(3).trim().toUpperCase());
            if (params.size() > 4) v.setAspectRatio(params.get(4).trim());
            if (params.size() > 5) v.setNivelAudioDb(new BigDecimal(params.get(5).trim()));
            if (params.size() > 6) v.setObservaciones(params.get(6).trim());
        }

        dao.create(v);
    }

    public List<String[]> listarValidaciones() throws SQLException {
        return dao.listAll();
    }

    public List<String[]> listarPendientes() throws SQLException {
        return dao.listPendientes();
    }

    public String[] consultarPorItem(int idItemParrilla) throws SQLException {
        return dao.readByItemParrilla(idItemParrilla);
    }

    public String getComandosValidacion() {
        return "Comandos CU5 - Validación de Emisión (Quality Control):\n" +
                "- validacion listar\n" +
                "- validacion pendientes\n" +
                "- validacion consultar id_item_parrilla\n" +
                "- validacion registrar id_item_parrilla,id_material_video,id_inspector,resultado(APROBADO_AIRE|RECHAZADO_FALLA_AUDIO|RECHAZADO_FALLA_VIDEO|RECHAZADO_SIN_MATERIAL|OBSERVADO),aspect_ratio,nivel_audio_db,observaciones";
    }
}
