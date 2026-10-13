package DAO;

import Database.Conexion;
import Models.AuditoriaSistema;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaSistemaDAO {

    public void create(AuditoriaSistema a) throws SQLException {
        String sql = "INSERT INTO auditoria_sistema (id_usuario, tabla_afectada, accion, detalle) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (a.getIdUsuario() != null && a.getIdUsuario() > 0) {
                ps.setInt(1, a.getIdUsuario());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, a.getTablaAfectada());
            ps.setString(3, a.getAccion());
            ps.setString(4, a.getDetalle());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT a.id, CONCAT(u.nombre, ' ', u.apellidos) AS usuario, " +
                     "a.tabla_afectada, a.accion, a.detalle, a.fecha_evento " +
                     "FROM auditoria_sistema a " +
                     "LEFT JOIN usuario u ON a.id_usuario = u.id_usuario " +
                     "ORDER BY a.id DESC LIMIT 100";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("usuario") != null ? rs.getString("usuario") : "SISTEMA",
                        rs.getString("tabla_afectada"),
                        rs.getString("accion"),
                        rs.getString("detalle"),
                        rs.getTimestamp("fecha_evento") != null ? rs.getTimestamp("fecha_evento").toString() : ""
                });
            }
        }
        return list;
    }

    public List<String[]> listByUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT a.id, CONCAT(u.nombre, ' ', u.apellidos) AS usuario, " +
                     "a.tabla_afectada, a.accion, a.detalle, a.fecha_evento " +
                     "FROM auditoria_sistema a " +
                     "LEFT JOIN usuario u ON a.id_usuario = u.id_usuario " +
                     "WHERE a.id_usuario = ? " +
                     "ORDER BY a.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("usuario") != null ? rs.getString("usuario") : "SISTEMA",
                            rs.getString("tabla_afectada"),
                            rs.getString("accion"),
                            rs.getString("detalle"),
                            rs.getTimestamp("fecha_evento") != null ? rs.getTimestamp("fecha_evento").toString() : ""
                    });
                }
            }
        }
        return list;
    }
}
