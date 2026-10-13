package Models;

import java.sql.Timestamp;

public class EmisionRealLog {
    private int id;
    private int idItemParrilla;
    private String programaTitulo; // helper join
    private Integer idMaterialVideo;
    private String materialCodigo; // helper join
    private Timestamp horaInicioReal;
    private Timestamp horaFinReal;
    private int duracionRealSegundos;
    private int discrepanciaSegundos;
    private String estadoEmision; // EMITIDO_CORRECTO, DESPLAZADO, CORTE_ENERGIA, FALLA_TECNICA, INTERRUPCION_NOTICIOSA, NO_EMITIDO
    private String operadorEmision;
    private String observaciones;
    private Timestamp fechaRegistro;

    public EmisionRealLog() {
        this.estadoEmision = "EMITIDO_CORRECTO";
        this.discrepanciaSegundos = 0;
    }

    public EmisionRealLog(int id, int idItemParrilla, Integer idMaterialVideo,
                          Timestamp horaInicioReal, Timestamp horaFinReal, int duracionRealSegundos,
                          int discrepanciaSegundos, String estadoEmision, String operadorEmision,
                          String observaciones, Timestamp fechaRegistro) {
        this.id = id;
        this.idItemParrilla = idItemParrilla;
        this.idMaterialVideo = idMaterialVideo;
        this.horaInicioReal = horaInicioReal;
        this.horaFinReal = horaFinReal;
        this.duracionRealSegundos = duracionRealSegundos;
        this.discrepanciaSegundos = discrepanciaSegundos;
        this.estadoEmision = estadoEmision != null ? estadoEmision : "EMITIDO_CORRECTO";
        this.operadorEmision = operadorEmision;
        this.observaciones = observaciones;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdItemParrilla() { return idItemParrilla; }
    public void setIdItemParrilla(int idItemParrilla) { this.idItemParrilla = idItemParrilla; }

    public String getProgramaTitulo() { return programaTitulo; }
    public void setProgramaTitulo(String programaTitulo) { this.programaTitulo = programaTitulo; }

    public Integer getIdMaterialVideo() { return idMaterialVideo; }
    public void setIdMaterialVideo(Integer idMaterialVideo) { this.idMaterialVideo = idMaterialVideo; }

    public String getMaterialCodigo() { return materialCodigo; }
    public void setMaterialCodigo(String materialCodigo) { this.materialCodigo = materialCodigo; }

    public Timestamp getHoraInicioReal() { return horaInicioReal; }
    public void setHoraInicioReal(Timestamp horaInicioReal) { this.horaInicioReal = horaInicioReal; }

    public Timestamp getHoraFinReal() { return horaFinReal; }
    public void setHoraFinReal(Timestamp horaFinReal) { this.horaFinReal = horaFinReal; }

    public int getDuracionRealSegundos() { return duracionRealSegundos; }
    public void setDuracionRealSegundos(int duracionRealSegundos) { this.duracionRealSegundos = duracionRealSegundos; }

    public int getDiscrepanciaSegundos() { return discrepanciaSegundos; }
    public void setDiscrepanciaSegundos(int discrepanciaSegundos) { this.discrepanciaSegundos = discrepanciaSegundos; }

    public String getEstadoEmision() { return estadoEmision; }
    public void setEstadoEmision(String estadoEmision) { this.estadoEmision = estadoEmision; }

    public String getOperadorEmision() { return operadorEmision; }
    public void setOperadorEmision(String operadorEmision) { this.operadorEmision = operadorEmision; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
