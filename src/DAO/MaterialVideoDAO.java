package DAO;

import Database.Conexion;
import Models.MaterialVideo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialVideoDAO {

    public void create(MaterialVideo mat) throws SQLException {
        String sql = "INSERT INTO material_video (id_programa, codigo_material, titulo_clip, formato, resolucion, duracion_segundos, ruta_servidor, estado, id_responsable, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mat.getIdPrograma());
            ps.setString(2, mat.getCodigoMaterial());
            ps.setString(3, mat.getTituloClip());
            ps.setString(4, mat.getFormato());
            ps.setString(5, mat.getResolucion());
            ps.setInt(6, mat.getDuracionSegundos());
            ps.setString(7, mat.getRutaServidor());
            ps.setString(8, mat.getEstado());
            ps.setInt(9, mat.getIdResponsable());
            ps.setString(10, mat.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mat.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE material_video SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM material_video WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT m.id, pr.titulo AS programa, m.codigo_material, m.titulo_clip, " +
                     "m.formato, m.resolucion, m.duracion_segundos, m.estado, " +
                     "CONCAT(u.nombre, ' ', u.apellidos) AS responsable, m.fecha_ingesta " +
                     "FROM material_video m " +
                     "LEFT JOIN programa pr ON m.id_programa = pr.id " +
                     "LEFT JOIN usuario u ON m.id_responsable = u.id_usuario " +
                     "ORDER BY m.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getString("codigo_material"),
                        rs.getString("titulo_clip"),
                        rs.getString("formato"),
                        rs.getString("resolucion"),
                        rs.getInt("duracion_segundos") + " seg",
                        rs.getString("estado"),
                        rs.getString("responsable") != null ? rs.getString("responsable") : "",
                        rs.getTimestamp("fecha_ingesta") != null ? rs.getTimestamp("fecha_ingesta").toString() : ""
                });
            }
        }
        return list;
    }

    public List<String[]> listByPrograma(int idPrograma) throws SQLException {
        String sql = "SELECT m.id, pr.titulo AS programa, m.codigo_material, m.titulo_clip, " +
                     "m.formato, m.resolucion, m.duracion_segundos, m.estado, " +
                     "CONCAT(u.nombre, ' ', u.apellidos) AS responsable, m.fecha_ingesta " +
                     "FROM material_video m " +
                     "LEFT JOIN programa pr ON m.id_programa = pr.id " +
                     "LEFT JOIN usuario u ON m.id_responsable = u.id_usuario " +
                     "WHERE m.id_programa = ? " +
                     "ORDER BY m.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrograma);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getString("codigo_material"),
                            rs.getString("titulo_clip"),
                            rs.getString("formato"),
                            rs.getString("resolucion"),
                            rs.getInt("duracion_segundos") + " seg",
                            rs.getString("estado"),
                            rs.getString("responsable") != null ? rs.getString("responsable") : "",
                            rs.getTimestamp("fecha_ingesta") != null ? rs.getTimestamp("fecha_ingesta").toString() : ""
                    });
                }
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT m.id, pr.titulo AS programa, m.codigo_material, m.titulo_clip, " +
                     "m.formato, m.resolucion, m.duracion_segundos, m.ruta_servidor, m.estado, " +
                     "CONCAT(u.nombre, ' ', u.apellidos) AS responsable, m.fecha_ingesta, m.observaciones " +
                     "FROM material_video m " +
                     "LEFT JOIN programa pr ON m.id_programa = pr.id " +
                     "LEFT JOIN usuario u ON m.id_responsable = u.id_usuario " +
                     "WHERE m.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getString("codigo_material"),
                            rs.getString("titulo_clip"),
                            rs.getString("formato"),
                            rs.getString("resolucion"),
                            rs.getInt("duracion_segundos") + " seg",
                            rs.getString("estado"),
                            rs.getString("responsable") != null ? rs.getString("responsable") : "",
                            rs.getTimestamp("fecha_ingesta") != null ? rs.getTimestamp("fecha_ingesta").toString() : ""
                    };
                }
            }
        }
        return null;
    }
}
