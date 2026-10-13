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
            throw new IllegalArgumentException(
                    "Formato: pauta agregar id_item_parrilla,id_spot [tanda_bloque] [orden] [hora_estimada]\n" +
                    "Ejemplo: pauta agregar 1,1,CORTE_1,1"
            );
        }
        PautaComercial p = new PautaComercial();
        p.setIdItemParrilla(Integer.parseInt(params.get(0).trim()));
        p.setIdSpot(Integer.parseInt(params.get(1).trim()));

        // Tanda / Bloque (ej: CORTE_1, CORTE_2, INICIO_PROGRAMA)
        String tanda = params.size() > 2 ? params.get(2).trim().toUpperCase() : "CORTE_1";
        p.setTandaBloque(tanda);

        // Orden de salida dentro de la tanda
        int orden = params.size() > 3 ? Integer.parseInt(params.get(3).trim()) : 1;
        p.setOrden(orden);

        // Hora estimada de salida al aire
        if (params.size() > 4) {
            String horaStr = params.get(4).trim();
            if (horaStr.length() == 5) horaStr += ":00";
            p.setHoraEstimada(Time.valueOf(horaStr));
        } else {
            p.setHoraEstimada(Time.valueOf("12:15:00"));
        }

        p.setEstado("PAUTADO");
        dao.create(p);
    }

    public void cambiarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException("Formato: pauta cambiar_estado id_pauta,estado(PAUTADO|EMITIDO|CANCELADO|OMITIDO)");
        }
        int idPauta = Integer.parseInt(params.get(0).trim());
        String estado = params.get(1).trim().toUpperCase();
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
