package Services;

import DAO.ParrillaDAO;
import Models.Parrilla;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ParrillaService {

    private ParrillaDAO dao;

    public ParrillaService() {
        this.dao = new ParrillaDAO();
    }

    public void crearParrilla(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: parrilla crear fecha(yyyy-MM-dd)  (o: fecha,canal,descripcion,id_programador)",
                    getComandosParrilla()
            );
        }
        Parrilla p = new Parrilla();
        Date fecha = Utils.Validator.validarFecha(params.get(0), "fecha_parrilla", getComandosParrilla());
        p.setFecha(fecha);
        p.setCanalSenial(params.size() > 1 ? Utils.Validator.validarTextoSeguro(params.get(1), "canal_senial", 2, 50, getComandosParrilla()).toUpperCase() : "SENIAL_PRINCIPAL_HD");
        p.setDescripcion(params.size() > 2 ? Utils.Validator.sanitizar(params.get(2)) : "Parrilla de transmision " + params.get(0).trim());
        p.setIdProgramador(params.size() > 3 ? Utils.Validator.validarId(params.get(3), "id_programador", getComandosParrilla()) : 2);
        p.setEstado("BORRADOR");
        dao.create(p);
    }

    public void cambiarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: parrilla cambiar_estado id_parrilla,estado(BORRADOR|APROBADA|PUBLICADA|CERRADA)",
                    getComandosParrilla()
            );
        }
        int idParrilla = Utils.Validator.validarId(params.get(0), "id_parrilla", getComandosParrilla());
        String estado = Utils.Validator.validarEstado(params.get(1), new String[]{"BORRADOR", "APROBADA", "PUBLICADA", "CERRADA"}, "estado", getComandosParrilla());
        dao.updateEstado(idParrilla, estado);
    }

    public void eliminarParrilla(int id) throws SQLException {
        dao.delete(id);
    }

    public List<String[]> listarParrillas() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarParrilla(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosParrilla() {
        return "Comandos CU3 - Parrilla de Programación:\n" +
                "- parrilla listar\n" +
                "- parrilla consultar id_parrilla\n" +
                "- parrilla agregar fecha(yyyy-MM-dd),canal_senial,descripcion,id_programador\n" +
                "- parrilla cambiar_estado id_parrilla,estado(BORRADOR|APROBADA|PUBLICADA|CERRADA)\n" +
                "- parrilla eliminar id_parrilla";
    }
}
