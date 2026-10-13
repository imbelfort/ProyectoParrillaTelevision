package Models;

import java.sql.Time;

public class PautaComercial {
    private int id;
    private int idItemParrilla;
    private String programaTitulo; // helper join
    private int idSpot;
    private String spotTitulo; // helper join
    private String spotCodigo; // helper join
    private String tandaBloque; // CORTE_1, CORTE_2, CORTE_3, CORTE_4, INICIO_PROGRAMA, FIN_PROGRAMA
    private int orden;
    private Time horaEstimada;
    private String estado; // PAUTADO, EMITIDO, CANCELADO, OMITIDO

    public PautaComercial() {
        this.tandaBloque = "CORTE_1";
        this.orden = 1;
        this.estado = "PAUTADO";
    }

    public PautaComercial(int id, int idItemParrilla, int idSpot, String tandaBloque, int orden,
                          Time horaEstimada, String estado) {
        this.id = id;
        this.idItemParrilla = idItemParrilla;
        this.idSpot = idSpot;
        this.tandaBloque = tandaBloque != null ? tandaBloque : "CORTE_1";
        this.orden = orden;
        this.horaEstimada = horaEstimada;
        this.estado = estado != null ? estado : "PAUTADO";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdItemParrilla() { return idItemParrilla; }
    public void setIdItemParrilla(int idItemParrilla) { this.idItemParrilla = idItemParrilla; }

    public String getProgramaTitulo() { return programaTitulo; }
    public void setProgramaTitulo(String programaTitulo) { this.programaTitulo = programaTitulo; }

    public int getIdSpot() { return idSpot; }
    public void setIdSpot(int idSpot) { this.idSpot = idSpot; }

    public String getSpotTitulo() { return spotTitulo; }
    public void setSpotTitulo(String spotTitulo) { this.spotTitulo = spotTitulo; }

    public String getSpotCodigo() { return spotCodigo; }
    public void setSpotCodigo(String spotCodigo) { this.spotCodigo = spotCodigo; }

    public String getTandaBloque() { return tandaBloque; }
    public void setTandaBloque(String tandaBloque) { this.tandaBloque = tandaBloque; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public Time getHoraEstimada() { return horaEstimada; }
    public void setHoraEstimada(Time horaEstimada) { this.horaEstimada = horaEstimada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
