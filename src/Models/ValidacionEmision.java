package Models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ValidacionEmision {
    private int id;
    private int idItemParrilla;
    private Integer idMaterialVideo;
    private String programaTitulo; // helper join
    private String horaProgramada; // helper join
    private int idInspectorQc;
    private String inspectorNombre; // helper join
    private String resultadoQc; // APROBADO_AIRE, RECHAZADO_FALLA_AUDIO, RECHAZADO_FALLA_VIDEO, RECHAZADO_SIN_MATERIAL, OBSERVADO, PENDIENTE
    private String aspectRatio; // 16:9, 4:3
    private BigDecimal nivelAudioDb; // e.g. -24.00
    private String observaciones;
    private Timestamp fechaValidacion;

    public ValidacionEmision() {
        this.resultadoQc = "PENDIENTE";
        this.aspectRatio = "16:9";
        this.nivelAudioDb = new BigDecimal("-24.00");
    }

    public ValidacionEmision(int id, int idItemParrilla, Integer idMaterialVideo, int idInspectorQc,
                             String resultadoQc, String aspectRatio, BigDecimal nivelAudioDb,
                             String observaciones, Timestamp fechaValidacion) {
        this.id = id;
        this.idItemParrilla = idItemParrilla;
        this.idMaterialVideo = idMaterialVideo;
        this.idInspectorQc = idInspectorQc;
        this.resultadoQc = resultadoQc != null ? resultadoQc : "PENDIENTE";
        this.aspectRatio = aspectRatio != null ? aspectRatio : "16:9";
        this.nivelAudioDb = nivelAudioDb != null ? nivelAudioDb : new BigDecimal("-24.00");
        this.observaciones = observaciones;
        this.fechaValidacion = fechaValidacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdItemParrilla() { return idItemParrilla; }
    public void setIdItemParrilla(int idItemParrilla) { this.idItemParrilla = idItemParrilla; }

    public Integer getIdMaterialVideo() { return idMaterialVideo; }
    public void setIdMaterialVideo(Integer idMaterialVideo) { this.idMaterialVideo = idMaterialVideo; }

    public String getProgramaTitulo() { return programaTitulo; }
    public void setProgramaTitulo(String programaTitulo) { this.programaTitulo = programaTitulo; }

    public String getHoraProgramada() { return horaProgramada; }
    public void setHoraProgramada(String horaProgramada) { this.horaProgramada = horaProgramada; }

    public int getIdInspectorQc() { return idInspectorQc; }
    public void setIdInspectorQc(int idInspectorQc) { this.idInspectorQc = idInspectorQc; }

    public String getInspectorNombre() { return inspectorNombre; }
    public void setInspectorNombre(String inspectorNombre) { this.inspectorNombre = inspectorNombre; }

    public String getResultadoQc() { return resultadoQc; }
    public void setResultadoQc(String resultadoQc) { this.resultadoQc = resultadoQc; }

    public String getAspectRatio() { return aspectRatio; }
    public void setAspectRatio(String aspectRatio) { this.aspectRatio = aspectRatio; }

    public BigDecimal getNivelAudioDb() { return nivelAudioDb; }
    public void setNivelAudioDb(BigDecimal nivelAudioDb) { this.nivelAudioDb = nivelAudioDb; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Timestamp getFechaValidacion() { return fechaValidacion; }
    public void setFechaValidacion(Timestamp fechaValidacion) { this.fechaValidacion = fechaValidacion; }
}
