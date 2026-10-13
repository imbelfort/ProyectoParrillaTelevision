package Models;

import java.sql.Timestamp;

public class Programa {
    private int id;
    private String codigo;
    private String titulo;
    private String descripcion;
    private int idCategoria;
    private String categoriaNombre; // helper para joins
    private String clasificacionEdad; // ATP, TEEN_14, ADULTO_18
    private int duracionEstandarMinutos;
    private String origen; // NACIONAL, INTERNACIONAL, COPRODUCCION
    private String estado; // ACTIVO, INACTIVO, ARCHIVADO
    private Timestamp fechaRegistro;

    public Programa() {
        this.clasificacionEdad = "ATP";
        this.duracionEstandarMinutos = 60;
        this.origen = "NACIONAL";
        this.estado = "ACTIVO";
    }

    public Programa(int id, String codigo, String titulo, String descripcion, int idCategoria,
                    String clasificacionEdad, int duracionEstandarMinutos, String origen,
                    String estado, Timestamp fechaRegistro) {
        this.id = id;
        this.codigo = codigo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.clasificacionEdad = clasificacionEdad != null ? clasificacionEdad : "ATP";
        this.duracionEstandarMinutos = duracionEstandarMinutos;
        this.origen = origen != null ? origen : "NACIONAL";
        this.estado = estado != null ? estado : "ACTIVO";
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public String getClasificacionEdad() { return clasificacionEdad; }
    public void setClasificacionEdad(String clasificacionEdad) { this.clasificacionEdad = clasificacionEdad; }

    public int getDuracionEstandarMinutos() { return duracionEstandarMinutos; }
    public void setDuracionEstandarMinutos(int duracionEstandarMinutos) { this.duracionEstandarMinutos = duracionEstandarMinutos; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
