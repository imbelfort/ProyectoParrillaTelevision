package Services;

import DAO.UsuarioDAO;
import Models.Usuario;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void crearUsuario(List<String> params) throws SQLException {
        crearUsuario(params, null);
    }

    public void crearUsuario(List<String> params, String fotoPerfil) throws SQLException {
        if (params.size() < 4) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Faltan parámetros obligatorios. Debe enviar al menos: nombre,email,password,rol  (ejemplo: usuario agregar Carlos,carlos@canal.com,123,Programador)",
                    getComandosUsuario()
            );
        }

        String nombre = params.get(0).trim();
        String apellidos = "General";
        String ci = "";
        String email = "";
        String telefono = "70000000";
        String direccion = "Santa Cruz";
        String password = "123";
        Date fechaNacimiento = null;
        String rol = null;

        if (params.size() == 4) {
            email = params.get(1).trim();
            password = params.get(2).trim();
            rol = params.get(3).trim();
            ci = String.valueOf(System.currentTimeMillis() % 10000000);
        } else if (params.size() == 5) {
            apellidos = params.get(1).trim();
            email = params.get(2).trim();
            password = params.get(3).trim();
            rol = params.get(4).trim();
            ci = String.valueOf(System.currentTimeMillis() % 10000000);
        } else if (params.size() == 6) {
            apellidos = params.get(1).trim();
            ci = params.get(2).trim();
            email = params.get(3).trim();
            password = params.get(4).trim();
            rol = params.get(5).trim();
        } else if (params.size() == 7) {
            apellidos = params.get(1).trim();
            ci = params.get(2).trim();
            email = params.get(3).trim();
            telefono = params.get(4).trim();
            direccion = params.get(5).trim();
            password = params.get(6).trim();
        } else {
            apellidos = params.get(1).trim();
            ci = params.get(2).trim();
            email = params.get(3).trim();
            telefono = params.get(4).trim();
            direccion = params.get(5).trim();
            fechaNacimiento = parseFechaNacimiento(params.get(6));
            password = params.get(7).trim();
            if (params.size() > 8) rol = params.get(8).trim();
        }

        nombre = Utils.Validator.validarTextoSeguro(nombre, "nombre", 2, 50, getComandosUsuario());
        apellidos = Utils.Validator.validarTextoSeguro(apellidos, "apellidos", 2, 50, getComandosUsuario());
        email = Utils.Validator.validarEmail(email, getComandosUsuario());
        telefono = Utils.Validator.validarTelefono(telefono, getComandosUsuario());
        ci = Utils.Validator.validarDocumento(ci, "ci", getComandosUsuario());
        direccion = Utils.Validator.sanitizar(direccion);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCi(ci);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setContrasena(password != null && !password.isEmpty() ? password : "123");
        usuario.setFotoPerfil(fotoPerfil != null && !fotoPerfil.trim().isEmpty() ? fotoPerfil : null);
        usuario.setEstado("ACTIVO");

        usuarioDAO.create(usuario);

        // Asignación de rol si se especificó
        if (rol != null && !rol.trim().isEmpty()) {
            int idRol = 2; // default PROGRAMADOR
            String rUp = rol.trim().toUpperCase();
            if (rUp.contains("ADMIN")) idRol = 1;
            else if (rUp.contains("PROG")) idRol = 2;
            else if (rUp.contains("CONTENID") || rUp.contains("ENCARGAD")) idRol = 3;
            else {
                try { idRol = Integer.parseInt(rUp); } catch (Exception ignored) {}
            }
            new DAO.RolDAO().asignarRolUsuario(usuario.getIdUsuario(), idRol);
        }
    }

    public String[] obtenerUsuarioPorId(int id) throws SQLException {
        return usuarioDAO.readById(id);
    }

    public String[] consultarUsuario(int id) throws SQLException {
        return obtenerUsuarioPorId(id);
    }

    public List<String[]> listarUsuarios() throws SQLException {
        return usuarioDAO.listAll();
    }

    public void actualizarUsuario(List<String> params) throws SQLException {
        actualizarUsuario(params, null);
    }

    public void actualizarUsuario(List<String> params, String fotoPerfil) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Parámetros insuficientes. Formato: usuario modificar id,nombre,apellidos,email,password,rol,estado",
                    getComandosUsuario()
            );
        }

        int idUsuario = Utils.Validator.validarId(params.get(0), "id_usuario", getComandosUsuario());
        String[] actual = usuarioDAO.readById(idUsuario);
        if (actual == null) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_NEG_001,
                    "No existe usuario con ID " + idUsuario + ".",
                    "usuario listar");
        }

        String nombre = actual[1];
        String apellidos = actual[2];
        String ci = actual[3];
        String email = actual[4];
        String telefono = actual[5];
        String direccion = actual[6];
        String foto = (fotoPerfil != null && !fotoPerfil.trim().isEmpty()) ? fotoPerfil : actual[7];
        String estado = actual[8];
        String password = actual[9];
        Date fechaNacimiento = null;
        if (actual[10] != null && !actual[10].trim().isEmpty()) {
            try { fechaNacimiento = Date.valueOf(actual[10].trim()); } catch (Exception ignored) {}
        }
        String rol = null;

        if (params.size() >= 10) {
            nombre = params.get(1).trim();
            apellidos = params.get(2).trim();
            ci = params.get(3) != null ? params.get(3).trim().toUpperCase() : ci;
            email = params.get(4) != null ? params.get(4).trim() : email;
            telefono = params.get(5).trim();
            direccion = params.get(6).trim();
            fechaNacimiento = parseFechaNacimiento(params.get(7));
            password = params.get(8).trim();
            estado = params.get(9).trim().toUpperCase();
        } else if (params.size() == 7) {
            nombre = params.get(1).trim();
            apellidos = params.get(2).trim();
            email = params.get(3).trim();
            password = params.get(4).trim();
            rol = params.get(5).trim();
            estado = params.get(6).trim().toUpperCase();
        } else if (params.size() == 6) {
            nombre = params.get(1).trim();
            apellidos = params.get(2).trim();
            email = params.get(3).trim();
            password = params.get(4).trim();
            estado = params.get(5).trim().toUpperCase();
        } else if (params.size() == 5) {
            nombre = params.get(1).trim();
            apellidos = params.get(2).trim();
            email = params.get(3).trim();
            estado = params.get(4).trim().toUpperCase();
        } else {
            nombre = params.get(1).trim();
            if (params.size() > 2) apellidos = params.get(2).trim();
            if (params.size() > 3) email = params.get(3).trim();
        }

        nombre = Utils.Validator.validarTextoSeguro(nombre, "nombre", 2, 50, getComandosUsuario());
        apellidos = Utils.Validator.validarTextoSeguro(apellidos, "apellidos", 2, 50, getComandosUsuario());
        email = Utils.Validator.validarEmail(email, getComandosUsuario());
        telefono = Utils.Validator.validarTelefono(telefono, getComandosUsuario());
        estado = Utils.Validator.validarEstado(estado, new String[]{"ACTIVO", "INACTIVO"}, "estado", getComandosUsuario());

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCi(ci);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setContrasena(password);
        usuario.setFotoPerfil(foto);
        usuario.setEstado(estado);

        usuarioDAO.update(usuario);

        if (rol != null && !rol.trim().isEmpty()) {
            int idRol = 2;
            String rUp = rol.trim().toUpperCase();
            if (rUp.contains("ADMIN")) idRol = 1;
            else if (rUp.contains("PROG")) idRol = 2;
            else if (rUp.contains("CONTENID") || rUp.contains("ENCARGAD")) idRol = 3;
            else {
                try { idRol = Integer.parseInt(rUp); } catch (Exception ignored) {}
            }
            new DAO.RolDAO().asignarRolUsuario(idUsuario, idRol);
        }
    }

    public void eliminarUsuario(int id) throws SQLException {
        usuarioDAO.delete(id);
    }

    public String getComandosUsuario() {
        return "Comandos CU1 - Gestión de Usuarios:\n" +
                "- usuario listar\n" +
                "- usuario agregar nombre,apellidos,ci,email,telefono,direccion,fecha_nacimiento,password [foto adjunta]\n" +
                "- usuario modificar id,nombre,apellidos,ci,email,telefono,direccion,fecha_nacimiento,password,estado [foto adjunta]\n" +
                "- usuario eliminar id";
    }

    private Date parseFechaNacimiento(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return Utils.Validator.validarFecha(valor, "fecha_nacimiento", getComandosUsuario());
    }
}