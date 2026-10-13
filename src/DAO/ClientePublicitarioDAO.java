package DAO;

import Database.Conexion;
import Models.ClientePublicitario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientePublicitarioDAO {

    public void create(ClientePublicitario c) throws SQLException {
        String sql = "INSERT INTO cliente_publicitario (razon_social, nit, contacto, email, telefono, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getRazonSocial());
            ps.setString(2, c.getNit());
            ps.setString(3, c.getContacto());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT id, razon_social, nit, contacto, email, telefono, estado FROM cliente_publicitario ORDER BY id";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("razon_social"),
                        rs.getString("nit"),
                        rs.getString("contacto") != null ? rs.getString("contacto") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getString("telefono") != null ? rs.getString("telefono") : "",
                        rs.getString("estado")
                });
            }
        }
        return list;
    }

    public String[] readById(int id) throws SQLException {
        String sql = "SELECT id, razon_social, nit, contacto, email, telefono, estado FROM cliente_publicitario WHERE id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("razon_social"),
                            rs.getString("nit"),
                            rs.getString("contacto") != null ? rs.getString("contacto") : "",
                            rs.getString("email") != null ? rs.getString("email") : "",
                            rs.getString("telefono") != null ? rs.getString("telefono") : "",
                            rs.getString("estado")
                    };
                }
            }
        }
        return null;
    }
}
