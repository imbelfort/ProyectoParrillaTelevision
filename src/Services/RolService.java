package Services;

import DAO.RolDAO;
import Models.Rol;

import java.sql.SQLException;
import java.util.List;

public class RolService {

    private RolDAO rolDAO;

    public RolService() {
        this.rolDAO = new RolDAO();
    }

    public void crearRol(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException("Formato: rol agregar nombre,descripcion");
        }
        Rol r = new Rol();
        r.setNombre(params.get(0).trim().toUpperCase());
        r.setDescripcion(params.get(1).trim());
        r.setEstado("ACTIVO");
        rolDAO.create(r);
    }

    public List<String[]> listarRoles() throws SQLException {
        return rolDAO.listAll();
    }

    public void asignarRol(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException("Formato: rol asignar id_usuario,id_rol");
        }
        int idUsuario = Integer.parseInt(params.get(0).trim());
        int idRol = Integer.parseInt(params.get(1).trim());
        rolDAO.asignarRolUsuario(idUsuario, idRol);
    }

    public String getComandosRol() {
        return "Comandos CU1 - Roles:\n" +
                "- rol listar\n" +
                "- rol agregar nombre,descripcion\n" +
                "- rol asignar id_usuario,id_rol";
    }
}
