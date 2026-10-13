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
            throw new IllegalArgumentException("Formato: categoria agregar nombre,descripcion");
        }
        CategoriaPrograma c = new CategoriaPrograma();
        c.setNombre(params.get(0).trim().toUpperCase());
        c.setDescripcion(params.get(1).trim());
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
