package Services;

import DAO.CategoriaProgramaDAO;
import Models.CategoriaPrograma;

import java.sql.SQLException;
import java.util.List;

public class CategoriaProgramaService {

    private CategoriaProgramaDAO dao;

    public CategoriaProgramaService() {
        this.dao = new CategoriaProgramaDAO();
    }

    public void crearCategoria(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: categoria agregar nombre,descripcion",
                    getComandosCategoria()
            );
        }
        CategoriaPrograma c = new CategoriaPrograma();
        String nombre = Utils.Validator.validarTextoSeguro(params.get(0), "nombre_categoria", 2, 50, getComandosCategoria()).toUpperCase();
        String desc = Utils.Validator.sanitizar(params.get(1));
        c.setNombre(nombre);
        c.setDescripcion(desc);
        c.setEstado("ACTIVO");
        dao.create(c);
    }

    public List<String[]> listarCategorias() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarCategoria(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosCategoria() {
        return "Comandos CU2 - Categorías de Programas:\n" +
                "- categoria listar\n" +
                "- categoria agregar nombre,descripcion\n" +
                "- categoria help";
    }
}
