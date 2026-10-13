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
            throw new IllegalArgumentException(
                    "Formato: item agregar id_parrilla,id_programa,hora_inicio,hora_fin [tipo_emision]"
            );
        }
        ItemParrilla item = new ItemParrilla();
        item.setIdParrilla(Integer.parseInt(params.get(0).trim()));
        item.setIdPrograma(Integer.parseInt(params.get(1).trim()));
        item.setHoraInicio(parseTime(params.get(2).trim()));
        item.setHoraFin(parseTime(params.get(3).trim()));
        item.setTipoEmision(params.size() > 4 ? params.get(4).trim().toUpperCase() : "GRABADO");
        item.setEstado("PROGRAMADO");
        item.setObservaciones(params.size() > 5 ? params.get(5).trim() : null);
        dao.create(item);
    }

    public void modificarItemParrilla(List<String> params) throws SQLException {
        if (params.size() < 5) {
            throw new IllegalArgumentException(
                    "Formato: item_parrilla modificar id_item,hora_inicio,hora_fin,tipo_emision,estado [observaciones]"
            );
        }
        ItemParrilla item = new ItemParrilla();
        item.setId(Integer.parseInt(params.get(0).trim()));
        item.setHoraInicio(parseTime(params.get(1).trim()));
        item.setHoraFin(parseTime(params.get(2).trim()));
        item.setTipoEmision(params.get(3).trim().toUpperCase());
        item.setEstado(params.get(4).trim().toUpperCase());
        item.setObservaciones(params.size() > 5 ? params.get(5).trim() : null);
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
