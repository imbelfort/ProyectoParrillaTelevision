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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Faltan parámetros obligatorios. Debe enviar al menos: codigo,titulo,duracion_minutos",
                    getComandosPrograma()
            );
        }
        Programa p = new Programa();
        String codigo = Utils.Validator.validarCodigo(params.get(0), "codigo_programa", getComandosPrograma());
        String titulo = Utils.Validator.validarTextoSeguro(params.get(1), "titulo_programa", 2, 100, getComandosPrograma());
        p.setCodigo(codigo);
        p.setTitulo(titulo);

        int duracion;
        String clasificacion = "ATP";
        String origen = "NACIONAL";
        int idCat = 4; // Entretenimiento

        if (params.size() == 3) {
            p.setDescripcion(titulo);
            p.setIdCategoria(idCat);
            duracion = Utils.Validator.validarDuracion(params.get(2), "duracion_minutos", getComandosPrograma());
        } else if (params.size() == 4) {
            p.setDescripcion(titulo);
            p.setIdCategoria(resolverCategoria(params.get(2).trim()));
            duracion = Utils.Validator.validarDuracion(params.get(3), "duracion_minutos", getComandosPrograma());
        } else if (params.size() == 5) {
            p.setDescripcion(Utils.Validator.sanitizar(params.get(2)));
            p.setIdCategoria(resolverCategoria(params.get(3).trim()));
            duracion = Utils.Validator.validarDuracion(params.get(4), "duracion_minutos", getComandosPrograma());
        } else {
            p.setDescripcion(Utils.Validator.sanitizar(params.get(2)));
            p.setIdCategoria(resolverCategoria(params.get(3).trim()));
            clasificacion = Utils.Validator.validarEstado(params.get(4), new String[]{"ATP", "TEEN_14", "ADULTO_18"}, "clasificacion_edad", getComandosPrograma());
            duracion = params.size() > 5 ? Utils.Validator.validarDuracion(params.get(5), "duracion_minutos", getComandosPrograma()) : 60;
            origen = params.size() > 6 ? Utils.Validator.validarEstado(params.get(6), new String[]{"NACIONAL", "INTERNACIONAL", "COPRODUCCION"}, "origen", getComandosPrograma()) : "NACIONAL";
        }
        p.setClasificacionEdad(clasificacion);
        p.setDuracionEstandarMinutos(duracion);
        p.setOrigen(origen);
        p.setEstado("ACTIVO");
        dao.create(p);
    }

    private int resolverCategoria(String valor) {
        try {
            return Integer.parseInt(valor.trim());
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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: programa modificar id,codigo,titulo,[descripcion],[id_categoria],[clasificacion_edad],[duracion_minutos],[origen],[estado]",
                    getComandosPrograma()
            );
        }
        int id = Utils.Validator.validarId(params.get(0), "id_programa", getComandosPrograma());
        String codigo = Utils.Validator.validarCodigo(params.get(1), "codigo_programa", getComandosPrograma());
        String titulo = Utils.Validator.validarTextoSeguro(params.get(2), "titulo_programa", 2, 100, getComandosPrograma());

        Programa p = new Programa();
        p.setId(id);
        p.setCodigo(codigo);
        p.setTitulo(titulo);

        if (params.size() >= 9) {
            p.setDescripcion(Utils.Validator.sanitizar(params.get(3)));
            p.setIdCategoria(resolverCategoria(params.get(4)));
            p.setClasificacionEdad(Utils.Validator.validarEstado(params.get(5), new String[]{"ATP", "TEEN_14", "ADULTO_18"}, "clasificacion_edad", getComandosPrograma()));
            p.setDuracionEstandarMinutos(Utils.Validator.validarDuracion(params.get(6), "duracion_minutos", getComandosPrograma()));
            p.setOrigen(Utils.Validator.validarEstado(params.get(7), new String[]{"NACIONAL", "INTERNACIONAL", "COPRODUCCION"}, "origen", getComandosPrograma()));
            p.setEstado(Utils.Validator.validarEstado(params.get(8), new String[]{"ACTIVO", "INACTIVO", "ARCHIVADO"}, "estado", getComandosPrograma()));
        } else {
            p.setDescripcion(params.size() > 3 ? Utils.Validator.sanitizar(params.get(3)) : titulo);
            p.setIdCategoria(params.size() > 4 ? resolverCategoria(params.get(4)) : 1);
            p.setClasificacionEdad(params.size() > 5 ? Utils.Validator.validarEstado(params.get(5), new String[]{"ATP", "TEEN_14", "ADULTO_18"}, "clasificacion_edad", getComandosPrograma()) : "ATP");
            p.setDuracionEstandarMinutos(params.size() > 6 ? Utils.Validator.validarDuracion(params.get(6), "duracion_minutos", getComandosPrograma()) : 60);
            p.setOrigen(params.size() > 7 ? Utils.Validator.validarEstado(params.get(7), new String[]{"NACIONAL", "INTERNACIONAL", "COPRODUCCION"}, "origen", getComandosPrograma()) : "NACIONAL");
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
