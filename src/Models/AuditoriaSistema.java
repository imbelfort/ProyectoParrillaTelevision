package Models;

import java.sql.Timestamp;

public class AuditoriaSistema {
    private int id;
    private Integer idUsuario;
    private String usuarioNombre; // helper join
    private String tablaAfectada;
    private String accion;
    private String detalle;
    private Timestamp fechaEvento;

    public AuditoriaSistema() {
    }

    public AuditoriaSistema(int id, Integer idUsuario, String tablaAfectada, String accion, String detalle, Timestamp fechaEvento) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.tablaAfectada = tablaAfectada;
        this.accion = accion;
        this.detalle = detalle;
        this.fechaEvento = fechaEvento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public Timestamp getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(Timestamp fechaEvento) { this.fechaEvento = fechaEvento; }
}
