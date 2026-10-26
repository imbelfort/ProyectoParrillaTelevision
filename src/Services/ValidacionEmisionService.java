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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Uso: validacion aprobar id_item_parrilla",
                    getComandosValidacion()
            );
        }
        ValidacionEmision v = new ValidacionEmision();
        int idItem = Utils.Validator.validarId(params.get(0), "id_item_parrilla", getComandosValidacion());
        v.setIdItemParrilla(idItem);
        v.setIdInspectorQc(3);
        v.setResultadoQc("APROBADO_AIRE");
        v.setAspectRatio("16:9");
        v.setNivelAudioDb(new BigDecimal("-24.00"));
        v.setObservaciones(params.size() > 1 ? Utils.Validator.sanitizar(params.get(1)) : "Control de calidad aprobado para emision");
        dao.create(v);
    }

    public void rechazarValidacion(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Uso: validacion rechazar id_item_parrilla [motivo]",
                    getComandosValidacion()
            );
        }
        ValidacionEmision v = new ValidacionEmision();
        int idItem = Utils.Validator.validarId(params.get(0), "id_item_parrilla", getComandosValidacion());
        v.setIdItemParrilla(idItem);
        v.setIdInspectorQc(3);
        v.setResultadoQc("RECHAZADO_FALLA_AUDIO");
        v.setAspectRatio("16:9");
        v.setNivelAudioDb(new BigDecimal("-24.00"));
        v.setObservaciones(params.size() > 1 ? Utils.Validator.sanitizar(params.get(1)) : "Rechazado en control tecnico");
        dao.create(v);
    }

    public void registrarValidacion(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: validacion registrar id_item,resultado(APROBADO_AIRE|RECHAZADO_FALLA_AUDIO|RECHAZADO_FALLA_VIDEO|OBSERVADO) [observaciones]",
                    getComandosValidacion()
            );
        }
        ValidacionEmision v = new ValidacionEmision();
        int idItem = Utils.Validator.validarId(params.get(0), "id_item_parrilla", getComandosValidacion());
        v.setIdItemParrilla(idItem);
        
        String[] resultadosPermitidos = {"APROBADO_AIRE", "RECHAZADO_FALLA_AUDIO", "RECHAZADO_FALLA_VIDEO", "RECHAZADO_SIN_MATERIAL", "OBSERVADO"};

        if (params.size() == 2) {
            v.setIdInspectorQc(3);
            v.setResultadoQc(Utils.Validator.validarEstado(params.get(1), resultadosPermitidos, "resultado_qc", getComandosValidacion()));
            v.setObservaciones("Validacion manual");
        } else if (params.size() == 3) {
            v.setIdInspectorQc(3);
            v.setResultadoQc(Utils.Validator.validarEstado(params.get(1), resultadosPermitidos, "resultado_qc", getComandosValidacion()));
            v.setObservaciones(Utils.Validator.sanitizar(params.get(2)));
        } else {
            String matStr = params.get(1).trim();
            if (!matStr.isEmpty() && !"null".equalsIgnoreCase(matStr) && !"0".equals(matStr)) {
                v.setIdMaterialVideo(Utils.Validator.validarId(matStr, "id_material_video", getComandosValidacion()));
            }
            v.setIdInspectorQc(Utils.Validator.validarId(params.get(2), "id_inspector", getComandosValidacion()));
            v.setResultadoQc(Utils.Validator.validarEstado(params.get(3), resultadosPermitidos, "resultado_qc", getComandosValidacion()));
            if (params.size() > 4) v.setAspectRatio(Utils.Validator.sanitizar(params.get(4)));
            if (params.size() > 5) {
                try {
                    v.setNivelAudioDb(new BigDecimal(params.get(5).trim()));
                } catch (Exception e) {
                    v.setNivelAudioDb(new BigDecimal("-24.00"));
                }
            }
            if (params.size() > 6) v.setObservaciones(Utils.Validator.sanitizar(params.get(6)));
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
