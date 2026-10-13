package Models;

import java.sql.Date;
import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String ci;
    private String email;
    private String telefono;
    private String direccion;
    private String fotoPerfil;
    private String usuarioSistema;
    private String contrasena;
    private String estado; // ACTIVO, INACTIVO, BLOQUEADO
    private Date fechaNacimiento;
    private Timestamp fechaCreacion;
    private Timestamp fechaModificacion;

    public Usuario() {
        this.estado = "ACTIVO";
    }

    public Usuario(int idUsuario, String nombre, String apellidos, String ci, String email, String telefono,
                   String direccion, String fotoPerfil, String usuarioSistema, String contrasena, String estado,
                   Date fechaNacimiento, Timestamp fechaCreacion, Timestamp fechaModificacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.ci = ci;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fotoPerfil = fotoPerfil;
        this.usuarioSistema = usuarioSistema;
        this.contrasena = contrasena;
        this.estado = estado != null ? estado : "ACTIVO";
        this.fechaNacimiento = fechaNacimiento;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", ci='" + ci + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", estado='" + estado + '\'' +
                ", usuarioSistema='" + usuarioSistema + '\'' +
                '}';
    }
}
