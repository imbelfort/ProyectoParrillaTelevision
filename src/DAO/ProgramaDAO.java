package DAO;

import Database.Conexion;
import Models.Programa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramaDAO {

    public void create(Programa p) throws SQLException {
        String sql = "INSERT INTO programa (codigo, titulo, descripcion, id_categoria, clasificacion_edad, duracion_estandar_minutos, origen, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getTitulo());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getIdCategoria());
            ps.setString(5, p.getClasificacionEdad());
            ps.setInt(6, p.getDuracionEstandarMinutos());
            ps.setString(7, p.getOrigen());
            ps.setString(8, p.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Programa p) throws SQLException {
        String sql = "UPDATE programa SET codigo = ?, titulo = ?, descripcion = ?, id_categoria = ?, clasificacion_edad = ?, " +
                     "duracion_estandar_minutos = ?, origen = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getTitulo());
            ps.setString(3, p.getDescripcion());
            ps.setInt(4, p.getIdCategoria());
            ps.setString(5, p.getClasificacionEdad());
            ps.setInt(6, p.getDuracionEstandarMinutos());
            ps.setString(7, p.getOrigen());
            ps.setString(8, p.getEstado());
            ps.setInt(9, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM programa WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT p.id, p.codigo, p.titulo, c.nombre AS categoria, p.clasificacion_edad, " +
                     "p.duracion_estandar_minutos, p.origen, p.estado, p.fecha_registro " +
                     "FROM programa p " +
                     "LEFT JOIN categoria_programa c ON p.id_categoria = c.id " +
                     "ORDER BY p.id";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("categoria") != null ? rs.getString("categoria") : "",
                        rs.getString("clasificacion_edad"),
                        String.valueOf(rs.getInt("duracion_estandar_minutos")) + " min",
                        rs.getString("origen"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_registro") != null ? rs.getTimestamp("fecha_registro").toString() : ""
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT p.id, p.codigo, p.titulo, c.nombre AS categoria, p.clasificacion_edad, " +
                     "p.duracion_estandar_minutos, p.origen, p.estado, p.descripcion, p.fecha_registro " +
                     "FROM programa p " +
                     "LEFT JOIN categoria_programa c ON p.id_categoria = c.id " +
                     "WHERE p.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("codigo"),
                            rs.getString("titulo"),
                            rs.getString("categoria") != null ? rs.getString("categoria") : "",
                            rs.getString("clasificacion_edad"),
                            String.valueOf(rs.getInt("duracion_estandar_minutos")) + " min",
                            rs.getString("origen"),
                            rs.getString("estado"),
                            rs.getTimestamp("fecha_registro") != null ? rs.getTimestamp("fecha_registro").toString() : ""
                    };
                }
            }
        }
        return null;
    }
}
