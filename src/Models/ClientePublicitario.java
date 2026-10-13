package Models;

public class ClientePublicitario {
    private int id;
    private String razonSocial;
    private String nit;
    private String contacto;
    private String email;
    private String telefono;
    private String estado; // ACTIVO, INACTIVO

    public ClientePublicitario() {
        this.estado = "ACTIVO";
    }

    public ClientePublicitario(int id, String razonSocial, String nit, String contacto, String email, String telefono, String estado) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.nit = nit;
        this.contacto = contacto;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado != null ? estado : "ACTIVO";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
