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
            throw new IllegalArgumentException(
                    "Formato: parrilla crear fecha(yyyy-MM-dd)  (o: fecha,canal,descripcion,id_programador)"
            );
        }
        Parrilla p = new Parrilla();
        Date fecha;
        try {
            fecha = Date.valueOf(params.get(0).trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido para '" + params.get(0).trim() + "'. Use el formato yyyy-MM-dd (ejemplo: 2026-10-15)");
        }
        p.setFecha(fecha);
        p.setCanalSenial(params.size() > 1 ? params.get(1).trim().toUpperCase() : "SENIAL_PRINCIPAL_HD");
        p.setDescripcion(params.size() > 2 ? params.get(2).trim() : "Parrilla de transmision " + params.get(0).trim());
        p.setIdProgramador(params.size() > 3 ? Integer.parseInt(params.get(3).trim()) : 2);
        p.setEstado("BORRADOR");
        dao.create(p);
    }

    public void cambiarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException("Formato: parrilla cambiar_estado id_parrilla,estado(BORRADOR|APROBADA|PUBLICADA|CERRADA)");
        }
        int idParrilla = Integer.parseInt(params.get(0).trim());
        String estado = params.get(1).trim().toUpperCase();
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
