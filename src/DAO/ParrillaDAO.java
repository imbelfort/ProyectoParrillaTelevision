package DAO;

import Database.Conexion;
import Models.Parrilla;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParrillaDAO {

    public void create(Parrilla p) throws SQLException {
        String sql = "INSERT INTO parrilla (fecha, canal_senial, descripcion, id_programador, estado) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, p.getFecha());
            ps.setString(2, p.getCanalSenial());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getIdProgramador());
            ps.setString(5, p.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateEstado(int idParrilla, String estado) throws SQLException {
        String sql = "UPDATE parrilla SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idParrilla);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM parrilla WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT pa.id, pa.fecha, pa.canal_senial, pa.descripcion, " +
                     "CONCAT(u.nombre, ' ', u.apellidos) AS programador, pa.estado, pa.fecha_creacion " +
                     "FROM parrilla pa " +
                     "LEFT JOIN usuario u ON pa.id_programador = u.id_usuario " +
                     "ORDER BY pa.fecha DESC, pa.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toString() : "",
                        rs.getString("canal_senial"),
                        rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                        rs.getString("programador") != null ? rs.getString("programador") : "",
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toString() : ""
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT pa.id, pa.fecha, pa.canal_senial, pa.descripcion, " +
                     "CONCAT(u.nombre, ' ', u.apellidos) AS programador, pa.estado, pa.fecha_creacion " +
                     "FROM parrilla pa " +
                     "LEFT JOIN usuario u ON pa.id_programador = u.id_usuario " +
                     "WHERE pa.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getDate("fecha") != null ? rs.getDate("fecha").toString() : "",
                            rs.getString("canal_senial"),
                            rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                            rs.getString("programador") != null ? rs.getString("programador") : "",
                            rs.getString("estado"),
                            rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toString() : ""
                    };
                }
            }
        }
        return null;
    }
}
