package DAO;

import Database.Conexion;
import Models.ValidacionEmision;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValidacionEmisionDAO {

    public void create(ValidacionEmision v) throws SQLException {
        String sql = "INSERT INTO validacion_emision (id_item_parrilla, id_material_video, id_inspector_qc, resultado_qc, aspect_ratio, nivel_audio_db, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdItemParrilla());
            if (v.getIdMaterialVideo() != null && v.getIdMaterialVideo() > 0) {
                ps.setInt(2, v.getIdMaterialVideo());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, v.getIdInspectorQc());
            ps.setString(4, v.getResultadoQc());
            ps.setString(5, v.getAspectRatio());
            ps.setBigDecimal(6, v.getNivelAudioDb());
            ps.setString(7, v.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT ve.id, pr.titulo AS programa, ip.hora_inicio, ve.resultado_qc, " +
                     "ve.aspect_ratio, ve.nivel_audio_db, CONCAT(u.nombre, ' ', u.apellidos) AS inspector, " +
                     "ve.observaciones, ve.fecha_validacion " +
                     "FROM validacion_emision ve " +
                     "LEFT JOIN item_parrilla ip ON ve.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN usuario u ON ve.id_inspector_qc = u.id_usuario " +
                     "ORDER BY ve.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                        rs.getString("resultado_qc"),
                        rs.getString("aspect_ratio") != null ? rs.getString("aspect_ratio") : "",
                        rs.getBigDecimal("nivel_audio_db") != null ? rs.getBigDecimal("nivel_audio_db").toString() + " dB" : "",
                        rs.getString("inspector") != null ? rs.getString("inspector") : "",
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : "",
                        rs.getTimestamp("fecha_validacion") != null ? rs.getTimestamp("fecha_validacion").toString() : ""
                });
            }
        }
        return list;
    }

    public List<String[]> listPendientes() throws SQLException {
        String sql = "SELECT ip.id, pa.fecha, pr.titulo AS programa, ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado " +
                     "FROM item_parrilla ip " +
                     "JOIN parrilla pa ON ip.id_parrilla = pa.id " +
                     "JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN validacion_emision ve ON ip.id = ve.id_item_parrilla " +
                     "WHERE ve.id IS NULL OR ve.resultado_qc = 'PENDIENTE' " +
                     "ORDER BY pa.fecha ASC, ip.hora_inicio ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toString() : "",
                        rs.getString("programa"),
                        rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                        rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                        rs.getString("tipo_emision"),
                        rs.getString("estado")
                });
            }
        }
        return list;
    }

    public String[] readByItemParrilla(int idItemParrilla) throws SQLException {
        String sql = "SELECT ve.id, pr.titulo AS programa, ip.hora_inicio, ve.resultado_qc, " +
                     "ve.aspect_ratio, ve.nivel_audio_db, CONCAT(u.nombre, ' ', u.apellidos) AS inspector, " +
                     "ve.observaciones, ve.fecha_validacion " +
                     "FROM validacion_emision ve " +
                     "LEFT JOIN item_parrilla ip ON ve.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN usuario u ON ve.id_inspector_qc = u.id_usuario " +
                     "WHERE ve.id_item_parrilla = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItemParrilla);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                            rs.getString("resultado_qc"),
                            rs.getString("aspect_ratio") != null ? rs.getString("aspect_ratio") : "",
                            rs.getBigDecimal("nivel_audio_db") != null ? rs.getBigDecimal("nivel_audio_db").toString() + " dB" : "",
                            rs.getString("inspector") != null ? rs.getString("inspector") : "",
                            rs.getString("observaciones") != null ? rs.getString("observaciones") : "",
                            rs.getTimestamp("fecha_validacion") != null ? rs.getTimestamp("fecha_validacion").toString() : ""
                    };
                }
            }
        }
        return null;
    }
}
