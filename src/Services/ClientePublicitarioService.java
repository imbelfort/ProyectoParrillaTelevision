package Services;

import DAO.ClientePublicitarioDAO;
import Models.ClientePublicitario;

import java.sql.SQLException;
import java.util.List;

public class ClientePublicitarioService {

    private ClientePublicitarioDAO dao;

    public ClientePublicitarioService() {
        this.dao = new ClientePublicitarioDAO();
    }

    public void crearCliente(List<String> params) throws SQLException {
        if (params.isEmpty()) {
            throw new IllegalArgumentException(
                    "Formato: cliente agregar razon_social [nit] [contacto] [email] [telefono]"
            );
        }
        ClientePublicitario c = new ClientePublicitario();
        c.setRazonSocial(params.get(0).trim());
        c.setNit(params.size() > 1 ? params.get(1).trim() : "1020304050");
        c.setContacto(params.size() > 2 ? params.get(2).trim() : "Gerente Comercial");
        c.setEmail(params.size() > 3 ? params.get(3).trim() : "contacto@empresa.com");
        c.setTelefono(params.size() > 4 ? params.get(4).trim() : "70000000");
        c.setEstado("ACTIVO");
        dao.create(c);
    }

    public List<String[]> listarClientes() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarCliente(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosCliente() {
        return "Comandos CU6 - Clientes Publicitarios:\n" +
                "- cliente listar\n" +
                "- cliente consultar id\n" +
                "- cliente agregar razon_social,nit,contacto,email,telefono";
    }
}
