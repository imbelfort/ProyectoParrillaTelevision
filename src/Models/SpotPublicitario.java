package Models;

import java.sql.Date;

public class SpotPublicitario {
    private int id;
    private int idCliente;
    private String clienteNombre; // helper join
    private String codigoSpot;
    private String tituloSpot;
    private int duracionSegundos;
    private Date fechaVigenciaInicio;
    private Date fechaVigenciaFin;
    private String estado; // ACTIVO, VENCIDO, SUSPENDIDO

    public SpotPublicitario() {
        this.duracionSegundos = 30;
        this.estado = "ACTIVO";
    }

    public SpotPublicitario(int id, int idCliente, String codigoSpot, String tituloSpot,
                            int duracionSegundos, Date fechaVigenciaInicio, Date fechaVigenciaFin, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.codigoSpot = codigoSpot;
        this.tituloSpot = tituloSpot;
        this.duracionSegundos = duracionSegundos;
        this.fechaVigenciaInicio = fechaVigenciaInicio;
        this.fechaVigenciaFin = fechaVigenciaFin;
        this.estado = estado != null ? estado : "ACTIVO";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getCodigoSpot() { return codigoSpot; }
    public void setCodigoSpot(String codigoSpot) { this.codigoSpot = codigoSpot; }

    public String getTituloSpot() { return tituloSpot; }
    public void setTituloSpot(String tituloSpot) { this.tituloSpot = tituloSpot; }

    public int getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(int duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public Date getFechaVigenciaInicio() { return fechaVigenciaInicio; }
    public void setFechaVigenciaInicio(Date fechaVigenciaInicio) { this.fechaVigenciaInicio = fechaVigenciaInicio; }

    public Date getFechaVigenciaFin() { return fechaVigenciaFin; }
    public void setFechaVigenciaFin(Date fechaVigenciaFin) { this.fechaVigenciaFin = fechaVigenciaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
