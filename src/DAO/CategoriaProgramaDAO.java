package DAO;

import Database.Conexion;
import Models.CategoriaPrograma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaProgramaDAO {

    public void create(CategoriaPrograma categoria) throws SQLException {
        String sql = "INSERT INTO categoria_programa (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setString(3, categoria.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT id, nombre, descripcion, estado FROM categoria_programa ORDER BY id";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre") != null ? rs.getString("nombre") : "",
                        rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                        rs.getString("estado") != null ? rs.getString("estado") : ""
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, estado FROM categoria_programa WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("nombre") != null ? rs.getString("nombre") : "",
                            rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                            rs.getString("estado") != null ? rs.getString("estado") : ""
                    };
                }
            }
        }
        return null;
    }
}
