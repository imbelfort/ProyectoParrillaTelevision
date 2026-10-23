package DAO;

import Database.Conexion;
import Models.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    private static volatile boolean columnsChecked = false;

    private void ensureColumnsExist() {
        if (columnsChecked) return;
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             Statement st = con.createStatement()) {
            try { st.executeUpdate("ALTER TABLE roles ADD COLUMN IF NOT EXISTS estado VARCHAR(30) DEFAULT 'ACTIVO'"); } catch (Exception ignored) {}
            try { st.executeUpdate("ALTER TABLE roles ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP"); } catch (Exception ignored) {}
            columnsChecked = true;
        } catch (Exception ignored) {}
    }

    public void create(Rol rol) throws SQLException {
        ensureColumnsExist();
        String sql = "INSERT INTO roles (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.setString(3, rol.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rol.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        ensureColumnsExist();
        String sql = "SELECT id, nombre, descripcion, estado, fecha_creacion FROM roles ORDER BY id";
        List<String[]> lista = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre") != null ? rs.getString("nombre") : "",
                        rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                        rs.getString("estado") != null ? rs.getString("estado") : "ACTIVO",
                        rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toString() : ""
                });
            }
        }
        return lista;
    }

    public void asignarRolUsuario(int idUsuario, int idRol) throws SQLException {
        String sql = "INSERT INTO usuario_roles (id_usuario, id_rol, fecha_asignacion, estado) VALUES (?, ?, CURRENT_TIMESTAMP, 'ACTIVO') " +
                     "ON CONFLICT (id_usuario, id_rol) DO UPDATE SET estado = 'ACTIVO'";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            ps.executeUpdate();
        }
    }
}
