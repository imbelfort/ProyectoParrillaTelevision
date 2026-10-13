package Services;

import DAO.AuditoriaSistemaDAO;
import Models.AuditoriaSistema;

import java.sql.SQLException;
import java.util.List;

public class AuditoriaSistemaService {

    private AuditoriaSistemaDAO dao;

    public AuditoriaSistemaService() {
        this.dao = new AuditoriaSistemaDAO();
    }

    public void registrarAuditoria(Integer idUsuario, String tabla, String accion, String detalle) {
        try {
            AuditoriaSistema a = new AuditoriaSistema();
            a.setIdUsuario(idUsuario);
            a.setTablaAfectada(tabla);
            a.setAccion(accion);
            a.setDetalle(detalle);
            dao.create(a);
        } catch (SQLException e) {
            System.err.println("Error al registrar auditoria: " + e.getMessage());
        }
    }

    public List<String[]> listarAuditoria() throws SQLException {
        return dao.listAll();
    }

    public List<String[]> listarPorUsuario(int idUsuario) throws SQLException {
        return dao.listByUsuario(idUsuario);
    }

    public String getComandosAuditoria() {
        return "Comandos CU7 - Auditoría del Sistema:\n" +
                "- auditoria listar\n" +
                "- auditoria consultar id_usuario";
    }
}
