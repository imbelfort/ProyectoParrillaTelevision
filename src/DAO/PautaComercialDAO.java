package DAO;

import Database.Conexion;
import Models.PautaComercial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PautaComercialDAO {

    public void create(PautaComercial p) throws SQLException {
        String sql = "INSERT INTO pauta_comercial (id_item_parrilla, id_spot, tanda_bloque, orden, hora_estimada, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdItemParrilla());
            ps.setInt(2, p.getIdSpot());
            ps.setString(3, p.getTandaBloque());
            ps.setInt(4, p.getOrden());
            ps.setTime(5, p.getHoraEstimada());
            ps.setString(6, p.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateEstado(int idPauta, String estado) throws SQLException {
        String sql = "UPDATE pauta_comercial SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idPauta);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pauta_comercial WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT pc.id, ip.id AS id_item, pr.titulo AS programa, " +
                     "sp.codigo_spot, sp.titulo_spot, pc.tanda_bloque, pc.orden, " +
                     "pc.hora_estimada, pc.estado " +
                     "FROM pauta_comercial pc " +
                     "LEFT JOIN item_parrilla ip ON pc.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN spot_publicitario sp ON pc.id_spot = sp.id " +
                     "ORDER BY pc.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("id_item")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getString("codigo_spot") != null ? rs.getString("codigo_spot") : "",
                        rs.getString("titulo_spot") != null ? rs.getString("titulo_spot") : "",
                        rs.getString("tanda_bloque"),
                        String.valueOf(rs.getInt("orden")),
                        rs.getTime("hora_estimada") != null ? rs.getTime("hora_estimada").toString() : "",
                        rs.getString("estado")
                });
            }
        }
        return list;
    }

    public List<String[]> listByItemParrilla(int idItemParrilla) throws SQLException {
        String sql = "SELECT pc.id, ip.id AS id_item, pr.titulo AS programa, " +
                     "sp.codigo_spot, sp.titulo_spot, pc.tanda_bloque, pc.orden, " +
                     "pc.hora_estimada, pc.estado " +
                     "FROM pauta_comercial pc " +
                     "LEFT JOIN item_parrilla ip ON pc.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN spot_publicitario sp ON pc.id_spot = sp.id " +
                     "WHERE pc.id_item_parrilla = ? " +
                     "ORDER BY pc.tanda_bloque ASC, pc.orden ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItemParrilla);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("id_item")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getString("codigo_spot") != null ? rs.getString("codigo_spot") : "",
                            rs.getString("titulo_spot") != null ? rs.getString("titulo_spot") : "",
                            rs.getString("tanda_bloque"),
                            String.valueOf(rs.getInt("orden")),
                            rs.getTime("hora_estimada") != null ? rs.getTime("hora_estimada").toString() : "",
                            rs.getString("estado")
                    });
                }
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT pc.id, ip.id AS id_item, pr.titulo AS programa, " +
                     "sp.codigo_spot, sp.titulo_spot, pc.tanda_bloque, pc.orden, " +
                     "pc.hora_estimada, pc.estado " +
                     "FROM pauta_comercial pc " +
                     "LEFT JOIN item_parrilla ip ON pc.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "LEFT JOIN spot_publicitario sp ON pc.id_spot = sp.id " +
                     "WHERE pc.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            String.valueOf(rs.getInt("id_item")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getString("codigo_spot") != null ? rs.getString("codigo_spot") : "",
                            rs.getString("titulo_spot") != null ? rs.getString("titulo_spot") : "",
                            rs.getString("tanda_bloque"),
                            String.valueOf(rs.getInt("orden")),
                            rs.getTime("hora_estimada") != null ? rs.getTime("hora_estimada").toString() : "",
                            rs.getString("estado")
                    };
                }
            }
        }
        return null;
    }
}
