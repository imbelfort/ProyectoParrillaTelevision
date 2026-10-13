package DAO;

import Database.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<String[]> reporteGeneral() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM usuario WHERE estado = 'ACTIVO') AS total_usuarios, " +
                "(SELECT COUNT(*) FROM programa WHERE estado = 'ACTIVO') AS total_programas, " +
                "(SELECT COUNT(*) FROM parrilla) AS total_parrillas, " +
                "(SELECT COUNT(*) FROM item_parrilla) AS total_bloques_programados, " +
                "(SELECT COUNT(*) FROM material_video WHERE estado = 'LISTO') AS materiales_listos, " +
                "(SELECT COUNT(*) FROM spot_publicitario WHERE estado = 'ACTIVO') AS total_spots, " +
                "(SELECT COUNT(*) FROM pauta_comercial) AS total_pautas, " +
                "(SELECT COUNT(*) FROM emision_real_log WHERE estado_emision = 'EMITIDO_CORRECTO') AS emisiones_exitosas";

        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                list.add(new String[]{"Usuarios Activos", String.valueOf(rs.getInt("total_usuarios"))});
                list.add(new String[]{"Catálogo de Programas Activos", String.valueOf(rs.getInt("total_programas"))});
                list.add(new String[]{"Parrillas Creadas", String.valueOf(rs.getInt("total_parrillas"))});
                list.add(new String[]{"Bloques Horarios en Parrilla", String.valueOf(rs.getInt("total_bloques_programados"))});
                list.add(new String[]{"Materiales de Video Listos (MAM)", String.valueOf(rs.getInt("materiales_listos"))});
                list.add(new String[]{"Spots Publicitarios Activos", String.valueOf(rs.getInt("total_spots"))});
                list.add(new String[]{"Pautas Comerciales Programadas", String.valueOf(rs.getInt("total_pautas"))});
                list.add(new String[]{"Emisiones Confirmadas al Aire", String.valueOf(rs.getInt("emisiones_exitosas"))});
            }
        }
        return list;
    }

    public List<String[]> reporteParrilla() throws SQLException {
        String sql = "SELECT pa.fecha, pa.canal_senial, pr.titulo AS programa, " +
                     "ip.hora_inicio, ip.hora_fin, ip.tipo_emision, ip.estado " +
                     "FROM item_parrilla ip " +
                     "JOIN parrilla pa ON ip.id_parrilla = pa.id " +
                     "JOIN programa pr ON ip.id_programa = pr.id " +
                     "ORDER BY pa.fecha DESC, ip.hora_inicio ASC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toString() : "",
                        rs.getString("canal_senial"),
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

    public List<String[]> reporteProgramas() throws SQLException {
        String sql = "SELECT cp.nombre AS categoria, COUNT(pr.id) AS total_programas, " +
                     "AVG(pr.duracion_estandar_minutos)::NUMERIC(5,1) AS duracion_promedio_min " +
                     "FROM categoria_programa cp " +
                     "LEFT JOIN programa pr ON cp.id = pr.id_categoria " +
                     "GROUP BY cp.nombre ORDER BY total_programas DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("categoria"),
                        String.valueOf(rs.getInt("total_programas")),
                        rs.getString("duracion_promedio_min") != null ? rs.getString("duracion_promedio_min") + " min" : "0 min"
                });
            }
        }
        return list;
    }

    public List<String[]> reportePautas() throws SQLException {
        String sql = "SELECT cl.razon_social AS cliente, COUNT(pc.id) AS total_pautas, " +
                     "COALESCE(SUM(sp.duracion_segundos), 0) AS total_segundos_aire " +
                     "FROM cliente_publicitario cl " +
                     "JOIN spot_publicitario sp ON cl.id = sp.id_cliente " +
                     "JOIN pauta_comercial pc ON sp.id = pc.id_spot " +
                     "GROUP BY cl.razon_social " +
                     "ORDER BY total_pautas DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("cliente"),
                        String.valueOf(rs.getInt("total_pautas")),
                        rs.getInt("total_segundos_aire") + " seg"
                });
            }
        }
        return list;
    }

    public List<String[]> reporteCumplimiento() throws SQLException {
        String sql = "SELECT el.estado_emision, COUNT(el.id) AS cantidad, " +
                     "AVG(el.discrepancia_segundos)::NUMERIC(5,1) AS promedio_discrepancia_seg " +
                     "FROM emision_real_log el " +
                     "GROUP BY el.estado_emision";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("estado_emision"),
                        String.valueOf(rs.getInt("cantidad")),
                        rs.getString("promedio_discrepancia_seg") != null ? rs.getString("promedio_discrepancia_seg") + " seg" : "0 seg"
                });
            }
        }
        return list;
    }
}