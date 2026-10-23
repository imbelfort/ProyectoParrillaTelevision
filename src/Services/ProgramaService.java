package Services;

import DAO.ProgramaDAO;
import Models.Programa;

import java.sql.SQLException;
import java.util.List;

public class ProgramaService {

    private ProgramaDAO dao;

    public ProgramaService() {
        this.dao = new ProgramaDAO();
    }

    public void crearPrograma(List<String> params) throws SQLException {
        if (params.size() < 3) {
            throw new IllegalArgumentException(
                    "Faltan parámetros obligatorios. Debe enviar al menos: codigo,titulo,duracion_minutos"
            );
        }
        Programa p = new Programa();
        p.setCodigo(params.get(0).trim().toUpperCase());
        p.setTitulo(params.get(1).trim());

        if (params.size() == 3) {
            p.setDescripcion(params.get(1).trim());
            p.setIdCategoria(4); // Entretenimiento
            p.setDuracionEstandarMinutos(Integer.parseInt(params.get(2).trim()));
            p.setClasificacionEdad("ATP");
            p.setOrigen("NACIONAL");
        } else if (params.size() == 4) {
            p.setDescripcion(params.get(1).trim());
            p.setIdCategoria(resolverCategoria(params.get(2).trim()));
            p.setDuracionEstandarMinutos(Integer.parseInt(params.get(3).trim()));
            p.setClasificacionEdad("ATP");
            p.setOrigen("NACIONAL");
        } else if (params.size() == 5) {
            p.setDescripcion(params.get(2).trim());
            p.setIdCategoria(resolverCategoria(params.get(3).trim()));
            p.setDuracionEstandarMinutos(Integer.parseInt(params.get(4).trim()));
            p.setClasificacionEdad("ATP");
            p.setOrigen("NACIONAL");
        } else {
            p.setDescripcion(params.get(2).trim());
            p.setIdCategoria(resolverCategoria(params.get(3).trim()));
            p.setClasificacionEdad(params.size() > 4 ? params.get(4).trim().toUpperCase() : "ATP");
            p.setDuracionEstandarMinutos(params.size() > 5 ? Integer.parseInt(params.get(5).trim()) : 60);
            p.setOrigen(params.size() > 6 ? params.get(6).trim().toUpperCase() : "NACIONAL");
        }
        p.setEstado("ACTIVO");
        dao.create(p);
    }

    private int resolverCategoria(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            String v = valor.toUpperCase();
            if (v.contains("NOTICIA")) return 1;
            if (v.contains("DEPORT")) return 2;
            if (v.contains("NOVELA")) return 3;
            if (v.contains("ENTRET")) return 4;
            if (v.contains("DOCU")) return 5;
            if (v.contains("INFANT")) return 6;
            return 1;
        }
    }

    public void modificarPrograma(List<String> params) throws SQLException {
        if (params.size() < 3) {
            throw new IllegalArgumentException(
                    "Formato: programa modificar id,codigo,titulo,[descripcion],[id_categoria],[clasificacion_edad],[duracion_minutos],[origen],[estado]"
            );
        }
        int id = Integer.parseInt(params.get(0).trim());
        Programa p = new Programa();
        p.setId(id);
        p.setCodigo(params.get(1).trim().toUpperCase());
        p.setTitulo(params.get(2).trim());

        if (params.size() >= 9) {
            p.setDescripcion(params.get(3).trim());
            p.setIdCategoria(resolverCategoria(params.get(4).trim()));
            p.setClasificacionEdad(params.get(5).trim().toUpperCase());
            p.setDuracionEstandarMinutos(Integer.parseInt(params.get(6).trim()));
            p.setOrigen(params.get(7).trim().toUpperCase());
            p.setEstado(params.get(8).trim().toUpperCase());
        } else {
            p.setDescripcion(params.size() > 3 ? params.get(3).trim() : params.get(2).trim());
            p.setIdCategoria(params.size() > 4 ? resolverCategoria(params.get(4).trim()) : 1);
            p.setClasificacionEdad(params.size() > 5 ? params.get(5).trim().toUpperCase() : "ATP");
            p.setDuracionEstandarMinutos(params.size() > 6 ? Integer.parseInt(params.get(6).trim()) : 60);
            p.setOrigen(params.size() > 7 ? params.get(7).trim().toUpperCase() : "NACIONAL");
            p.setEstado("ACTIVO");
        }
        dao.update(p);
    }

    public void eliminarPrograma(int id) throws SQLException {
        dao.delete(id);
    }

    public List<String[]> listarProgramas() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarPrograma(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosPrograma() {
        return "Comandos CU2 - Catálogo de Programas:\n" +
                "- programa listar\n" +
                "- programa consultar id\n" +
                "- programa agregar codigo,titulo,descripcion,id_categoria,clasificacion_edad(ATP|TEEN_14|ADULTO_18),duracion_minutos,origen(NACIONAL|INTERNACIONAL|COPRODUCCION)\n" +
                "- programa modificar id,codigo,titulo,descripcion,id_categoria,clasificacion_edad,duracion_minutos,origen,estado(ACTIVO|INACTIVO|ARCHIVADO)\n" +
                "- programa eliminar id";
    }
}
