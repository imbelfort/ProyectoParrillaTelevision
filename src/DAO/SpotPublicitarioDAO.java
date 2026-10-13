package DAO;

import Database.Conexion;
import Models.SpotPublicitario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpotPublicitarioDAO {

    public void create(SpotPublicitario s) throws SQLException {
        String sql = "INSERT INTO spot_publicitario (id_cliente, codigo_spot, titulo_spot, duracion_segundos, fecha_vigencia_inicio, fecha_vigencia_fin, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getIdCliente());
            ps.setString(2, s.getCodigoSpot());
            ps.setString(3, s.getTituloSpot());
            ps.setInt(4, s.getDuracionSegundos());
            ps.setDate(5, s.getFechaVigenciaInicio());
            ps.setDate(6, s.getFechaVigenciaFin());
            ps.setString(7, s.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT s.id, c.razon_social AS cliente, s.codigo_spot, s.titulo_spot, " +
                     "s.duracion_segundos, s.fecha_vigencia_inicio, s.fecha_vigencia_fin, s.estado " +
                     "FROM spot_publicitario s " +
                     "LEFT JOIN cliente_publicitario c ON s.id_cliente = c.id " +
                     "ORDER BY s.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("cliente") != null ? rs.getString("cliente") : "",
                        rs.getString("codigo_spot"),
                        rs.getString("titulo_spot"),
                        rs.getInt("duracion_segundos") + " seg",
                        rs.getDate("fecha_vigencia_inicio") != null ? rs.getDate("fecha_vigencia_inicio").toString() : "",
                        rs.getDate("fecha_vigencia_fin") != null ? rs.getDate("fecha_vigencia_fin").toString() : "",
                        rs.getString("estado")
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT s.id, c.razon_social AS cliente, s.codigo_spot, s.titulo_spot, " +
                     "s.duracion_segundos, s.fecha_vigencia_inicio, s.fecha_vigencia_fin, s.estado " +
                     "FROM spot_publicitario s " +
                     "LEFT JOIN cliente_publicitario c ON s.id_cliente = c.id " +
                     "WHERE s.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("cliente") != null ? rs.getString("cliente") : "",
                            rs.getString("codigo_spot"),
                            rs.getString("titulo_spot"),
                            rs.getInt("duracion_segundos") + " seg",
                            rs.getDate("fecha_vigencia_inicio") != null ? rs.getDate("fecha_vigencia_inicio").toString() : "",
                            rs.getDate("fecha_vigencia_fin") != null ? rs.getDate("fecha_vigencia_fin").toString() : "",
                            rs.getString("estado")
                    };
                }
            }
        }
        return null;
    }
}
