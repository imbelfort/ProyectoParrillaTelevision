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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: rol agregar nombre,descripcion",
                    getComandosRol()
            );
        }
        Rol r = new Rol();
        String nombre = Utils.Validator.validarTextoSeguro(params.get(0), "nombre_rol", 2, 50, getComandosRol()).toUpperCase();
        String desc = Utils.Validator.sanitizar(params.get(1));
        r.setNombre(nombre);
        r.setDescripcion(desc);
        r.setEstado("ACTIVO");
        rolDAO.create(r);
    }

    public List<String[]> listarRoles() throws SQLException {
        return rolDAO.listAll();
    }

    public void asignarRol(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: rol asignar id_usuario,id_rol",
                    getComandosRol()
            );
        }
        int idUsuario = Utils.Validator.validarId(params.get(0), "id_usuario", getComandosRol());
        int idRol = Utils.Validator.validarId(params.get(1), "id_rol", getComandosRol());
        rolDAO.asignarRolUsuario(idUsuario, idRol);
    }

    public String getComandosRol() {
        return "Comandos CU1 - Roles:\n" +
                "- rol listar\n" +
                "- rol agregar nombre,descripcion\n" +
                "- rol asignar id_usuario,id_rol";
    }
}
