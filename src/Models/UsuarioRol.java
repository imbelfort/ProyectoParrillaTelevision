package Models;

import java.sql.Timestamp;

public class UsuarioRol {
    private int idUsuario;
    private int idRol;
    private Timestamp fechaAsignacion;
    private String estado;

    public UsuarioRol() {
        this.estado = "ACTIVO";
    }

    public UsuarioRol(int idUsuario, int idRol, Timestamp fechaAsignacion, String estado) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public Timestamp getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Timestamp fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
