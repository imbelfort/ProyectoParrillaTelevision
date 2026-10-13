package DAO;

import Database.Conexion;
import Models.EmisionRealLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmisionRealLogDAO {

    public void create(EmisionRealLog log) throws SQLException {
        String sql = "INSERT INTO emision_real_log (id_item_parrilla, id_material_video, hora_inicio_real, hora_fin_real, duracion_real_segundos, discrepancia_segundos, estado_emision, operador_emision, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getIdItemParrilla());
            if (log.getIdMaterialVideo() != null && log.getIdMaterialVideo() > 0) {
                ps.setInt(2, log.getIdMaterialVideo());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setTimestamp(3, log.getHoraInicioReal());
            ps.setTimestamp(4, log.getHoraFinReal());
            ps.setInt(5, log.getDuracionRealSegundos());
            ps.setInt(6, log.getDiscrepanciaSegundos());
            ps.setString(7, log.getEstadoEmision());
            ps.setString(8, log.getOperadorEmision());
            ps.setString(9, log.getObservaciones());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    log.setId(rs.getInt(1));
                }
            }
        }
    }

    public void conciliarItem(int idItemParrilla, String operador, String estado, int discrepanciaSeg, String observaciones) throws SQLException {
        String query = "SELECT ip.hora_inicio, ip.hora_fin, p.fecha " +
                       "FROM item_parrilla ip " +
                       "LEFT JOIN parrilla p ON ip.id_parrilla = p.id " +
                       "WHERE ip.id = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idItemParrilla);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    Time hIni = rs.getTime("hora_inicio");
                    Time hFin = rs.getTime("hora_fin");

                    String fStr = (fecha != null) ? fecha.toString() : new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                    String iStr = (hIni != null) ? hIni.toString() : "12:00:00";
                    String fFinStr = (hFin != null) ? hFin.toString() : "13:00:00";

                    Timestamp tIni = Timestamp.valueOf(fStr + " " + iStr);
                    Timestamp tFin = Timestamp.valueOf(fStr + " " + fFinStr);
                    long durSec = (tFin.getTime() - tIni.getTime()) / 1000;
                    if (durSec <= 0) durSec = 3600;

                    EmisionRealLog log = new EmisionRealLog();
                    log.setIdItemParrilla(idItemParrilla);
                    log.setHoraInicioReal(tIni);
                    log.setHoraFinReal(tFin);
                    log.setDuracionRealSegundos((int) durSec);
                    log.setDiscrepanciaSegundos(discrepanciaSeg);
                    log.setEstadoEmision(estado != null && !estado.isEmpty() ? estado : "EMITIDO_CORRECTO");
                    log.setOperadorEmision(operador != null && !operador.isEmpty() ? operador : "master_control");
                    log.setObservaciones(observaciones != null ? observaciones : "Conciliacion automatica con parrilla");
                    create(log);
                } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    Timestamp fin = new Timestamp(System.currentTimeMillis() + 3600000L);
                    EmisionRealLog log = new EmisionRealLog();
                    log.setIdItemParrilla(idItemParrilla);
                    log.setHoraInicioReal(now);
                    log.setHoraFinReal(fin);
                    log.setDuracionRealSegundos(3600);
                    log.setDiscrepanciaSegundos(discrepanciaSeg);
                    log.setEstadoEmision(estado != null && !estado.isEmpty() ? estado : "EMITIDO_CORRECTO");
                    log.setOperadorEmision(operador != null && !operador.isEmpty() ? operador : "master_control");
                    log.setObservaciones(observaciones != null ? observaciones : "Conciliacion manual");
                    create(log);
                }
            }
        }
    }

    public List<String[]> listAll() throws SQLException {
        String sql = "SELECT el.id, pr.titulo AS programa, ip.hora_inicio AS hora_prog, " +
                     "el.hora_inicio_real, el.hora_fin_real, el.duracion_real_segundos, " +
                     "el.discrepancia_segundos, el.estado_emision, el.operador_emision, el.fecha_registro " +
                     "FROM emision_real_log el " +
                     "LEFT JOIN item_parrilla ip ON el.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "ORDER BY el.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getTime("hora_prog") != null ? rs.getTime("hora_prog").toString() : "",
                        rs.getTimestamp("hora_inicio_real") != null ? rs.getTimestamp("hora_inicio_real").toString() : "",
                        rs.getTimestamp("hora_fin_real") != null ? rs.getTimestamp("hora_fin_real").toString() : "",
                        rs.getInt("duracion_real_segundos") + " seg",
                        rs.getInt("discrepancia_segundos") + " seg",
                        rs.getString("estado_emision"),
                        rs.getString("operador_emision"),
                        rs.getTimestamp("fecha_registro") != null ? rs.getTimestamp("fecha_registro").toString() : ""
                });
            }
        }
        return list;
    }

    public List<String[]> listDiscrepancias() throws SQLException {
        String sql = "SELECT el.id, pr.titulo AS programa, ip.hora_inicio AS hora_prog, " +
                     "el.hora_inicio_real, el.discrepancia_segundos, el.estado_emision, el.operador_emision, el.observaciones " +
                     "FROM emision_real_log el " +
                     "LEFT JOIN item_parrilla ip ON el.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE el.discrepancia_segundos != 0 OR el.estado_emision != 'EMITIDO_CORRECTO' " +
                     "ORDER BY el.id DESC";
        List<String[]> list = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getTime("hora_prog") != null ? rs.getTime("hora_prog").toString() : "",
                        rs.getTimestamp("hora_inicio_real") != null ? rs.getTimestamp("hora_inicio_real").toString() : "",
                        rs.getInt("discrepancia_segundos") + " seg",
                        rs.getString("estado_emision"),
                        rs.getString("operador_emision"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                });
            }
        }
        return list;
    }

    public String[] readByItemParrilla(int idItemParrilla) throws SQLException {
        String sql = "SELECT el.id, pr.titulo AS programa, ip.hora_inicio AS hora_prog, " +
                     "el.hora_inicio_real, el.hora_fin_real, el.duracion_real_segundos, " +
                     "el.discrepancia_segundos, el.estado_emision, el.operador_emision, el.observaciones " +
                     "FROM emision_real_log el " +
                     "LEFT JOIN item_parrilla ip ON el.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "WHERE el.id_item_parrilla = ?";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItemParrilla);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            String.valueOf(rs.getInt("id")),
                            rs.getString("programa") != null ? rs.getString("programa") : "",
                            rs.getTime("hora_prog") != null ? rs.getTime("hora_prog").toString() : "",
                            rs.getTimestamp("hora_inicio_real") != null ? rs.getTimestamp("hora_inicio_real").toString() : "",
                            rs.getTimestamp("hora_fin_real") != null ? rs.getTimestamp("hora_fin_real").toString() : "",
                            rs.getInt("duracion_real_segundos") + " seg",
                            rs.getInt("discrepancia_segundos") + " seg",
                            rs.getString("estado_emision"),
                            rs.getString("operador_emision"),
                            rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                    };
                }
            }
        }
        return null;
    }

    public String[] readAlAire() throws SQLException {
        String sql = "SELECT el.id, pr.titulo AS programa, ip.hora_inicio AS hora_prog, " +
                     "el.hora_inicio_real, el.hora_fin_real, el.duracion_real_segundos, " +
                     "el.discrepancia_segundos, el.estado_emision, el.operador_emision, el.observaciones " +
                     "FROM emision_real_log el " +
                     "LEFT JOIN item_parrilla ip ON el.id_item_parrilla = ip.id " +
                     "LEFT JOIN programa pr ON ip.id_programa = pr.id " +
                     "ORDER BY el.id DESC LIMIT 1";
        try (Connection con = Conexion.getInstancia().EstablecerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("programa") != null ? rs.getString("programa") : "",
                        rs.getTime("hora_prog") != null ? rs.getTime("hora_prog").toString() : "",
                        rs.getTimestamp("hora_inicio_real") != null ? rs.getTimestamp("hora_inicio_real").toString() : "",
                        rs.getTimestamp("hora_fin_real") != null ? rs.getTimestamp("hora_fin_real").toString() : "",
                        rs.getInt("duracion_real_segundos") + " seg",
                        rs.getInt("discrepancia_segundos") + " seg",
                        rs.getString("estado_emision"),
                        rs.getString("operador_emision"),
                        rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                };
            }
        }
        return null;
    }
}
