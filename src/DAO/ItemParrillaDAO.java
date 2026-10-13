package DAO;

import Database.Conexion;
import Models.ItemParrilla;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemParrillaDAO {

    public void create(ItemParrilla item) throws SQLException {
        String sql = "INSERT INTO item_parrilla (id_parrilla, id_programa, hora_inicio, hora_fin, tipo_emision, estado, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getIdParrilla());
            ps.setInt(2, item.getIdPrograma());
            ps.setTime(3, item.getHoraInicio());
            ps.setTime(4, item.getHoraFin());
            ps.setString(5, item.getTipoEmision());
            ps.setString(6, item.getEstado());
            ps.setString(7, item.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(ItemParrilla item) throws SQLException {
        String sql = "UPDATE item_parrilla SET hora_inicio = ?, hora_fin = ?, tipo_emision = ?, estado = ?, observaciones = ? " +
                     "WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTime(1, item.getHoraInicio());
            ps.setTime(2, item.getHoraFin());
            ps.setString(3, item.getTipoEmision());
            ps.setString(4, item.getEstado());
            ps.setString(5, item.getObservaciones());
            ps.setInt(6, item.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM item_parrilla WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String[]> listByParrilla(int idParrilla) throws SQLException {
        String sql = "SELECT ip.id, ip.id_parrilla, pr.codigo AS cod_prog, pr.titulo, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado, ip.observaciones " +
                     "FROM item_parrilla ip " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE ip.id_parrilla = ? " +
                     "ORDER BY ip.hora_inicio ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idParrilla);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("id_parrilla")),
                            rs.getString("cod_prog") != null ? rs.getString("cod_prog") : "",
                            rs.getString("titulo") != null ? rs.getString("titulo") : "",
                            rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                            rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                            rs.getString("tipo_emision"),
                            rs.getString("estado"),
                            rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                    });
                }
            }
        }
        return list;
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT ip.id, ip.id_parrilla, pr.codigo AS cod_prog, pr.titulo, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado, ip.observaciones " +
                     "FROM item_parrilla ip " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "ORDER BY ip.id_parrilla DESC, ip.hora_inicio ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("id_parrilla")),
                        rs.getString("cod_prog") != null ? rs.getString("cod_prog") : "",
                        rs.getString("titulo") != null ? rs.getString("titulo") : "",
                        rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                        rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                        rs.getString("tipo_emision"),
                        rs.getString("estado"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT ip.id, ip.id_parrilla, pr.codigo AS cod_prog, pr.titulo, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado, ip.observaciones " +
                     "FROM item_parrilla ip " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE ip.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("id_parrilla")),
                            rs.getString("cod_prog") != null ? rs.getString("cod_prog") : "",
                            rs.getString("titulo") != null ? rs.getString("titulo") : "",
                            rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                            rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                            rs.getString("tipo_emision"),
                            rs.getString("estado"),
                            rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                    };
                }
            }
        }
        return null;
    }

    public String[] readAlAire() throws SQLException {
        String sql = "SELECT ip.id, ip.id_parrilla, pr.codigo AS cod_prog, pr.titulo, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado, ip.observaciones " +
                     "FROM item_parrilla ip " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE (CURRENT_TIME BETWEEN ip.hora_inicio AND ip.hora_fin) " +
                     "   OR ip.estado = 'EN_CURSO' " +
                     "ORDER BY ip.id DESC LIMIT 1";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("id_parrilla")),
                        rs.getString("cod_prog") != null ? rs.getString("cod_prog") : "",
                        rs.getString("titulo") != null ? rs.getString("titulo") : "",
                        rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                        rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                        rs.getString("tipo_emision"),
                        rs.getString("estado"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                };
            }
        }
        return readById(1);
    }

    public List<String[]> listHoy() throws SQLException {
        String sql = "SELECT ip.id, ip.id_parrilla, pr.codigo AS cod_prog, pr.titulo, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado, ip.observaciones " +
                     "FROM item_parrilla ip " +
                     "LEFT JOIN parrilla p ON ip.id_parrilla = p.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE p.fecha = CURRENT_DATE " +
                     "ORDER BY ip.hora_inicio ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("id_parrilla")),
                        rs.getString("cod_prog") != null ? rs.getString("cod_prog") : "",
                        rs.getString("titulo") != null ? rs.getString("titulo") : "",
                        rs.getTime("hora_inicio") != null ? rs.getTime("hora_inicio").toString() : "",
                        rs.getTime("hora_fin") != null ? rs.getTime("hora_fin").toString() : "",
                        rs.getString("tipo_emision"),
                        rs.getString("estado"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                });
            }
        }
        if (list.isEmpty()) {
            return listAll();
        }
        return list;
    }
}
