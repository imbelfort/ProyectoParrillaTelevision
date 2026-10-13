package Models;

import java.sql.Date;
import java.sql.Timestamp;

public class Reporte {
    private int id;
    private int idUsuario;
    private String tipoReporte; 
    private Timestamp fechaGeneracion;
    private Date periodoInicio;
    private Date periodoFin;
    private String generadoPor;
    private String parametros;

    public Reporte() {
    }

    public Reporte(int id, int idUsuario, String tipoReporte, Timestamp fechaGeneracion,
                   Date periodoInicio, Date periodoFin, String generadoPor, String parametros) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.tipoReporte = tipoReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.generadoPor = generadoPor;
        this.parametros = parametros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Timestamp getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Timestamp fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Date getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(Date periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public Date getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(Date periodoFin) {
        this.periodoFin = periodoFin;
    }

    public String getGeneradoPor() {
        return generadoPor;
    }

    public void setGeneradoPor(String generadoPor) {
        this.generadoPor = generadoPor;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", tipoReporte='" + tipoReporte + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                ", periodoInicio=" + periodoInicio +
                ", periodoFin=" + periodoFin +
                ", generadoPor='" + generadoPor + '\'' +
                ", parametros='" + parametros + '\'' +
                '}';
    }
}
