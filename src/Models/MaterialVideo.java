package Models;

import java.sql.Timestamp;

public class MaterialVideo {
    private int id;
    private int idPrograma;
    private String programaTitulo; // helper join
    private String codigoMaterial;
    private String tituloClip;
    private String formato; // HD_1080I, MP4, MXF, MOV
    private String resolucion; // 1920x1080, etc.
    private int duracionSegundos;
    private String rutaServidor;
    private String estado; // INGESTADO, TRANSCODIFICADO, LISTO, CUARENTENA, DESCARTADO
    private int idResponsable;
    private String responsableNombre; // helper join
    private Timestamp fechaIngesta;
    private String observaciones;

    public MaterialVideo() {
        this.formato = "HD_1080I";
        this.resolucion = "1920x1080";
        this.estado = "INGESTADO";
    }

    public MaterialVideo(int id, int idPrograma, String codigoMaterial, String tituloClip,
                         String formato, String resolucion, int duracionSegundos, String rutaServidor,
                         String estado, int idResponsable, Timestamp fechaIngesta, String observaciones) {
        this.id = id;
        this.idPrograma = idPrograma;
        this.codigoMaterial = codigoMaterial;
        this.tituloClip = tituloClip;
        this.formato = formato != null ? formato : "HD_1080I";
        this.resolucion = resolucion != null ? resolucion : "1920x1080";
        this.duracionSegundos = duracionSegundos;
        this.rutaServidor = rutaServidor;
        this.estado = estado != null ? estado : "INGESTADO";
        this.idResponsable = idResponsable;
        this.fechaIngesta = fechaIngesta;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPrograma() { return idPrograma; }
    public void setIdPrograma(int idPrograma) { this.idPrograma = idPrograma; }

    public String getProgramaTitulo() { return programaTitulo; }
    public void setProgramaTitulo(String programaTitulo) { this.programaTitulo = programaTitulo; }

    public String getCodigoMaterial() { return codigoMaterial; }
    public void setCodigoMaterial(String codigoMaterial) { this.codigoMaterial = codigoMaterial; }

    public String getTituloClip() { return tituloClip; }
    public void setTituloClip(String tituloClip) { this.tituloClip = tituloClip; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public String getRutaServidor() { return rutaServidor; }
    public void setRutaServidor(String rutaServidor) { this.rutaServidor = rutaServidor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdResponsable() { return idResponsable; }
    public void setIdResponsable(int idResponsable) { this.idResponsable = idResponsable; }

    public String getResponsableNombre() { return responsableNombre; }
    public void setResponsableNombre(String responsableNombre) { this.responsableNombre = responsableNombre; }

    public Timestamp getFechaIngesta() { return fechaIngesta; }
    public void setFechaIngesta(Timestamp fechaIngesta) { this.fechaIngesta = fechaIngesta; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
