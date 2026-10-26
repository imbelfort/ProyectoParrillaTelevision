package Services;

import DAO.PautaComercialDAO;
import Models.PautaComercial;

import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class PautaComercialService {

    private PautaComercialDAO dao;

    public PautaComercialService() {
        this.dao = new PautaComercialDAO();
    }

    public void insertarPauta(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: pauta agregar id_item_parrilla,id_spot [tanda_bloque] [orden] [hora_estimada]\n" +
                    "Ejemplo: pauta agregar 1,1,CORTE_1,1",
                    getComandosPauta()
            );
        }
        PautaComercial p = new PautaComercial();
        int idItem = Utils.Validator.validarId(params.get(0), "id_item_parrilla", getComandosPauta());
        int idSpot = Utils.Validator.validarId(params.get(1), "id_spot", getComandosPauta());

        p.setIdItemParrilla(idItem);
        p.setIdSpot(idSpot);

        // Tanda / Bloque (ej: CORTE_1, CORTE_2, INICIO_PROGRAMA)
        String tanda = params.size() > 2 ? Utils.Validator.sanitizar(params.get(2)).toUpperCase() : "CORTE_1";
        p.setTandaBloque(tanda);

        // Orden de salida dentro de la tanda
        int orden = params.size() > 3 ? Utils.Validator.validarId(params.get(3), "orden", getComandosPauta()) : 1;
        p.setOrden(orden);

        // Hora estimada de salida al aire
        if (params.size() > 4) {
            Time hora = Utils.Validator.validarHora(params.get(4), "hora_estimada", getComandosPauta());
            p.setHoraEstimada(hora);
        } else {
            p.setHoraEstimada(Time.valueOf("12:15:00"));
        }

        p.setEstado("PAUTADO");
        dao.create(p);
    }

    public void cambiarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: pauta cambiar_estado id_pauta,estado(PAUTADO|EMITIDO|CANCELADO|OMITIDO)",
                    getComandosPauta()
            );
        }
        int idPauta = Utils.Validator.validarId(params.get(0), "id_pauta", getComandosPauta());
        String estado = Utils.Validator.validarEstado(params.get(1), new String[]{"PAUTADO", "EMITIDO", "CANCELADO", "OMITIDO"}, "estado", getComandosPauta());
        dao.updateEstado(idPauta, estado);
    }

    public void eliminarPauta(int id) throws SQLException {
        dao.delete(id);
    }

    public List<String[]> listarPautas() throws SQLException {
        return dao.listAll();
    }

    public List<String[]> listarPorItem(int idItemParrilla) throws SQLException {
        return dao.listByItemParrilla(idItemParrilla);
    }

    public String[] consultarPauta(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosPauta() {
        return "Comandos CU6 - Pautas Comerciales (Cortes de Playout):\n" +
                "- pauta listar\n" +
                "- pauta listar_item id_item_parrilla\n" +
                "- pauta consultar id_pauta\n" +
                "- pauta agregar id_item_parrilla,id_spot,tanda_bloque(CORTE_1|CORTE_2|CORTE_3|INICIO_PROGRAMA|FIN_PROGRAMA),orden,hora_estimada(HH:mm:ss)\n" +
                "- pauta cambiar_estado id_pauta,estado(PAUTADO|EMITIDO|CANCELADO|OMITIDO)\n" +
                "- pauta eliminar id_pauta";
    }
}
