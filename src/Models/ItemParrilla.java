package Models;

import java.sql.Time;

public class ItemParrilla {
    private int id;
    private int idParrilla;
    private int idPrograma;
    private String programaTitulo; // helper join
    private String programaCodigo; // helper join
    private Time horaInicio;
    private Time horaFin;
    private String tipoEmision; // VIVO, GRABADO, REPETICION, CADENA_OFICIAL
    private String estado; // PROGRAMADO, EN_CURSO, EMITIDO, CANCELADO, AJUSTADO
    private String observaciones;

    public ItemParrilla() {
        this.tipoEmision = "GRABADO";
        this.estado = "PROGRAMADO";
    }

    public ItemParrilla(int id, int idParrilla, int idPrograma, Time horaInicio, Time horaFin,
                        String tipoEmision, String estado, String observaciones) {
        this.id = id;
        this.idParrilla = idParrilla;
        this.idPrograma = idPrograma;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipoEmision = tipoEmision != null ? tipoEmision : "GRABADO";
        this.estado = estado != null ? estado : "PROGRAMADO";
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdParrilla() { return idParrilla; }
    public void setIdParrilla(int idParrilla) { this.idParrilla = idParrilla; }

    public int getIdPrograma() { return idPrograma; }
    public void setIdPrograma(int idPrograma) { this.idPrograma = idPrograma; }

    public String getProgramaTitulo() { return programaTitulo; }
    public void setProgramaTitulo(String programaTitulo) { this.programaTitulo = programaTitulo; }

    public String getProgramaCodigo() { return programaCodigo; }
    public void setProgramaCodigo(String programaCodigo) { this.programaCodigo = programaCodigo; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFin() { return horaFin; }
    public void setHoraFin(Time horaFin) { this.horaFin = horaFin; }

    public String getTipoEmision() { return tipoEmision; }
    public void setTipoEmision(String tipoEmision) { this.tipoEmision = tipoEmision; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
