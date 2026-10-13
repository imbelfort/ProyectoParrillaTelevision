package DAO;

import Database.Conexion;
import Models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {

    public void create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario " +
                "(nombre, apellidos, ci, email, telefono, direccion, password, foto_perfil, fecha_nacimiento, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getCi());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getDireccion());
            ps.setString(7, usuario.getContrasena());
            ps.setString(8, usuario.getFotoPerfil());
            ps.setDate(9, usuario.getFechaNacimiento());
            ps.setString(10, usuario.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT " +
                "id_usuario, nombre, apellidos, ci, email, telefono, direccion, " +
                "foto_perfil, estado, password, fecha_nacimiento, fecha_creacion, fecha_modificacion " +
                "FROM usuario " +
                "WHERE id_usuario = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }

        return null;
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT " +
                "id_usuario, nombre, apellidos, ci, email, telefono, direccion, " +
                "foto_perfil, estado, password, fecha_nacimiento, fecha_creacion, fecha_modificacion " +
                "FROM usuario " +
                "ORDER BY id_usuario";

        List<String[]> usuarios = new ArrayList<>();

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }

        return usuarios;
    }

    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET " +
                "nombre = ?, " +
                "apellidos = ?, " +
                "ci = ?, " +
                "email = ?, " +
                "telefono = ?, " +
                "direccion = ?, " +
                "password = ?, " +
                "foto_perfil = ?, " +
                "fecha_nacimiento = ?, " +
                "estado = ?, " +
                "fecha_modificacion = CURRENT_TIMESTAMP " +
                "WHERE id_usuario = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getCi());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getDireccion());
            ps.setString(7, usuario.getContrasena());
            ps.setString(8, usuario.getFotoPerfil());
            ps.setDate(9, usuario.getFechaNacimiento());
            ps.setString(10, usuario.getEstado());
            ps.setInt(11, usuario.getIdUsuario());

            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE id_usuario = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByCi(String ci) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE ci = ?";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ci);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String[] mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new String[]{
                String.valueOf(rs.getInt("id_usuario")),
                rs.getString("nombre") != null ? rs.getString("nombre") : "",
                rs.getString("apellidos") != null ? rs.getString("apellidos") : "",
                rs.getString("ci") != null ? rs.getString("ci") : "",
                rs.getString("email") != null ? rs.getString("email") : "",
                rs.getString("telefono") != null ? rs.getString("telefono") : "",
                rs.getString("direccion") != null ? rs.getString("direccion") : "",
                rs.getString("foto_perfil") != null ? rs.getString("foto_perfil") : "",
                rs.getString("estado") != null ? rs.getString("estado") : "",
                rs.getString("password") != null ? rs.getString("password") : "",
                rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toString() : "",
                rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toString() : "",
                rs.getTimestamp("fecha_modificacion") != null ? rs.getTimestamp("fecha_modificacion").toString() : ""
        };
    }
}