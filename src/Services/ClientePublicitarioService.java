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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: cliente agregar razon_social [nit] [contacto] [email] [telefono]",
                    getComandosCliente()
            );
        }
        ClientePublicitario c = new ClientePublicitario();
        String razonSocial = Utils.Validator.validarTextoSeguro(params.get(0), "razon_social", 2, 100, getComandosCliente());
        String nit = params.size() > 1 ? Utils.Validator.validarDocumento(params.get(1), "nit", getComandosCliente()) : "1020304050";
        String contacto = params.size() > 2 ? Utils.Validator.validarTextoSeguro(params.get(2), "contacto", 2, 50, getComandosCliente()) : "Gerente Comercial";
        String email = params.size() > 3 ? Utils.Validator.validarEmail(params.get(3), getComandosCliente()) : "contacto@empresa.com";
        String telefono = params.size() > 4 ? Utils.Validator.validarTelefono(params.get(4), getComandosCliente()) : "70000000";

        c.setRazonSocial(razonSocial);
        c.setNit(nit);
        c.setContacto(contacto);
        c.setEmail(email);
        c.setTelefono(telefono);
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
