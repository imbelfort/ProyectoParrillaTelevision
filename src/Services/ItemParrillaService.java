package Services;

import DAO.ItemParrillaDAO;
import Models.ItemParrilla;

import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class ItemParrillaService {

    private ItemParrillaDAO dao;

    public ItemParrillaService() {
        this.dao = new ItemParrillaDAO();
    }

    public void crearItemParrilla(List<String> params) throws SQLException {
        if (params.size() < 4) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: item agregar id_parrilla,id_programa,hora_inicio,hora_fin [tipo_emision]",
                    getComandosItemParrilla()
            );
        }
        ItemParrilla item = new ItemParrilla();
        int idParrilla = Utils.Validator.validarId(params.get(0), "id_parrilla", getComandosItemParrilla());
        int idPrograma = Utils.Validator.validarId(params.get(1), "id_programa", getComandosItemParrilla());
        Time inicio = Utils.Validator.validarHora(params.get(2), "hora_inicio", getComandosItemParrilla());
        Time fin = Utils.Validator.validarHora(params.get(3), "hora_fin", getComandosItemParrilla());
        Utils.Validator.validarHorarioBloque(inicio, fin, getComandosItemParrilla());

        String tipo = params.size() > 4 ? Utils.Validator.validarEstado(params.get(4), new String[]{"VIVO", "GRABADO", "REPETICION"}, "tipo_emision", getComandosItemParrilla()) : "GRABADO";

        item.setIdParrilla(idParrilla);
        item.setIdPrograma(idPrograma);
        item.setHoraInicio(inicio);
        item.setHoraFin(fin);
        item.setTipoEmision(tipo);
        item.setEstado("PROGRAMADO");
        item.setObservaciones(params.size() > 5 ? Utils.Validator.sanitizar(params.get(5)) : null);
        dao.create(item);
    }

    public void modificarItemParrilla(List<String> params) throws SQLException {
        if (params.size() < 3) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: item modificar id_item,hora_inicio,hora_fin [tipo_emision] [estado] [observaciones]",
                    getComandosItemParrilla()
            );
        }
        ItemParrilla item = new ItemParrilla();
        int id = Utils.Validator.validarId(params.get(0), "id_item", getComandosItemParrilla());
        Time inicio = Utils.Validator.validarHora(params.get(1), "hora_inicio", getComandosItemParrilla());
        Time fin = Utils.Validator.validarHora(params.get(2), "hora_fin", getComandosItemParrilla());
        Utils.Validator.validarHorarioBloque(inicio, fin, getComandosItemParrilla());

        String tipo = params.size() > 3 ? Utils.Validator.validarEstado(params.get(3), new String[]{"VIVO", "GRABADO", "REPETICION"}, "tipo_emision", getComandosItemParrilla()) : "GRABADO";
        String estado = params.size() > 4 ? Utils.Validator.validarEstado(params.get(4), new String[]{"PROGRAMADO", "AL_AIRE", "EMITIDO", "CANCELADO"}, "estado", getComandosItemParrilla()) : "PROGRAMADO";

        item.setId(id);
        item.setHoraInicio(inicio);
        item.setHoraFin(fin);
        item.setTipoEmision(tipo);
        item.setEstado(estado);
        item.setObservaciones(params.size() > 5 ? Utils.Validator.sanitizar(params.get(5)) : null);
        dao.update(item);
    }

    public void eliminarItemParrilla(int id) throws SQLException {
        dao.delete(id);
    }

    public List<String[]> listarPorParrilla(int idParrilla) throws SQLException {
        return dao.listByParrilla(idParrilla);
    }

    public List<String[]> listarTodos() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarItem(int id) throws SQLException {
        return dao.readById(id);
    }

    public String[] consultarAlAire() throws SQLException {
        return dao.readAlAire();
    }

    public List<String[]> listarHoy() throws SQLException {
        return dao.listHoy();
    }

    public String getComandosItemParrilla() {
        return "Comandos CU3 - Bloques / Items de Parrilla (Playlist):\n" +
                "- item_parrilla listar [id_parrilla]\n" +
                "- item_parrilla al_aire\n" +
                "- item_parrilla hoy\n" +
                "- item_parrilla consultar id_item\n" +
                "- item_parrilla agregar id_parrilla,id_programa,hora_inicio(HH:mm),hora_fin(HH:mm),tipo_emision(VIVO|GRABADO|REPETICION)\n" +
                "- item_parrilla modificar id_item,hora_inicio,hora_fin,tipo_emision,estado\n" +
                "- item_parrilla eliminar id_item";
    }

    private Time parseTime(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El horario no puede estar vacío. Use formato HH:mm o HH:mm:ss (ej: 14:00)");
        }
        String v = valor.trim();
        if (v.length() == 1 || v.length() == 2) {
            v = (v.length() == 1 ? "0" + v : v) + ":00:00";
        } else if (v.length() == 5) {
            v += ":00";
        }
        try {
            return Time.valueOf(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de hora inválido para '" + valor + "'. Debe ser HH:mm o HH:mm:ss (ej: 14:30)");
        }
    }
}
