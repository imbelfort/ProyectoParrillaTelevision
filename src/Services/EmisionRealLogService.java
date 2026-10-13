package Services;

import DAO.EmisionRealLogDAO;
import Models.EmisionRealLog;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class EmisionRealLogService {

    private EmisionRealLogDAO dao;

    public EmisionRealLogService() {
        this.dao = new EmisionRealLogDAO();
    }

    public void conciliarEmision(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new IllegalArgumentException("Uso: emision conciliar id_item_parrilla [operador] [observaciones]");
        }
        int idItem = Integer.parseInt(params.get(0).trim());
        String operador = params.size() > 1 ? params.get(1).trim() : "master_control";
        String obs = params.size() > 2 ? params.get(2).trim() : "Conciliado con parrilla oficial";
        dao.conciliarItem(idItem, operador, "EMITIDO_CORRECTO", 0, obs);
    }

    public void registrarEmisionReal(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new IllegalArgumentException(
                    "Formato: emision registrar id_item [estado] [discrepancia_segundos] [operador]"
            );
        }

        int idItem = Integer.parseInt(params.get(0).trim());

        if (params.size() <= 4) {
            String estado = params.size() > 1 ? params.get(1).trim().toUpperCase() : "EMITIDO_CORRECTO";
            int discrepancia = params.size() > 2 ? Integer.parseInt(params.get(2).trim()) : 0;
            String operador = params.size() > 3 ? params.get(3).trim() : "master_control";
            dao.conciliarItem(idItem, operador, estado, discrepancia, "Registro de emision");
            return;
        }

        EmisionRealLog log = new EmisionRealLog();
        log.setIdItemParrilla(idItem);

        String matStr = params.get(1).trim();
        if (!matStr.isEmpty() && !"null".equalsIgnoreCase(matStr) && !"0".equals(matStr)) {
            log.setIdMaterialVideo(Integer.parseInt(matStr));
        }

        Timestamp inicio = Timestamp.valueOf(params.get(2).trim());
        Timestamp fin = Timestamp.valueOf(params.get(3).trim());
        log.setHoraInicioReal(inicio);
        log.setHoraFinReal(fin);

        long diffSeg = (fin.getTime() - inicio.getTime()) / 1000;
        log.setDuracionRealSegundos((int) diffSeg);
        log.setDiscrepanciaSegundos(0);

        log.setEstadoEmision(params.get(4).trim().toUpperCase());
        log.setOperadorEmision(params.size() > 5 ? params.get(5).trim() : "master_control");
        log.setObservaciones(params.size() > 6 ? params.get(6).trim() : null);

        dao.create(log);
    }

    public List<String[]> listarEmisiones() throws SQLException {
        return dao.listAll();
    }

    public List<String[]> listarDiscrepancias() throws SQLException {
        return dao.listDiscrepancias();
    }

    public String[] consultarPorItem(int idItem) throws SQLException {
        return dao.readByItemParrilla(idItem);
    }

    public String[] consultarAlAire() throws SQLException {
        return dao.readAlAire();
    }

    public String getComandosEmision() {
        return "Comandos CU7 - Bitácora de Emisión Real (As-Run Log):\n" +
                "- emision listar\n" +
                "- emision al_aire\n" +
                "- emision discrepancias\n" +
                "- emision conciliar id_item_parrilla\n" +
                "- emision consultar id_item_parrilla\n" +
                "- emision registrar id_item [estado] [discrepancia_seg] [operador]";
    }
}
