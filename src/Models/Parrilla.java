package Models;

import java.sql.Date;
import java.sql.Timestamp;

public class Parrilla {
    private int id;
    private Date fecha;
    private String canalSenial;
    private String descripcion;
    private int idProgramador;
    private String programadorNombre; // helper join
    private String estado; // BORRADOR, APROBADA, PUBLICADA, CERRADA
    private Timestamp fechaCreacion;

    public Parrilla() {
        this.canalSenial = "SENIAL_PRINCIPAL_HD";
        this.estado = "BORRADOR";
    }

    public Parrilla(int id, Date fecha, String canalSenial, String descripcion, int idProgramador, String estado, Timestamp fechaCreacion) {
        this.id = id;
        this.fecha = fecha;
        this.canalSenial = canalSenial != null ? canalSenial : "SENIAL_PRINCIPAL_HD";
        this.descripcion = descripcion;
        this.idProgramador = idProgramador;
        this.estado = estado != null ? estado : "BORRADOR";
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getCanalSenial() { return canalSenial; }
    public void setCanalSenial(String canalSenial) { this.canalSenial = canalSenial; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIdProgramador() { return idProgramador; }
    public void setIdProgramador(int idProgramador) { this.idProgramador = idProgramador; }

    public String getProgramadorNombre() { return programadorNombre; }
    public void setProgramadorNombre(String programadorNombre) { this.programadorNombre = programadorNombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
