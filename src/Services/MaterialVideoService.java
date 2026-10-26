package Services;

import DAO.MaterialVideoDAO;
import Models.MaterialVideo;

import java.sql.SQLException;
import java.util.List;

public class MaterialVideoService {

    private MaterialVideoDAO dao;

    public MaterialVideoService() {
        this.dao = new MaterialVideoDAO();
    }

    public void crearMaterial(List<String> params) throws SQLException {
        if (params.size() < 4) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: material agregar id_programa,codigo,titulo,duracion_segundos",
                    getComandosMaterial()
            );
        }
        MaterialVideo m = new MaterialVideo();
        int idProg = Utils.Validator.validarId(params.get(0), "id_programa", getComandosMaterial());
        String codigo = Utils.Validator.validarCodigo(params.get(1), "codigo_material", getComandosMaterial());
        String titulo = Utils.Validator.validarTextoSeguro(params.get(2), "titulo_clip", 2, 100, getComandosMaterial());
        int duracion = Utils.Validator.validarDuracion(params.get(3), "duracion_segundos", getComandosMaterial());

        m.setIdPrograma(idProg);
        m.setCodigoMaterial(codigo);
        m.setTituloClip(titulo);
        m.setDuracionSegundos(duracion);

        String formato = params.size() > 4 ? Utils.Validator.validarEstado(params.get(4), new String[]{"HD_1080I", "HD_720P", "4K_UHD", "SD_480I"}, "formato", getComandosMaterial()) : "HD_1080I";
        m.setFormato(formato);
        m.setResolucion(params.size() > 5 ? Utils.Validator.sanitizar(params.get(5)) : "1920x1080");
        m.setRutaServidor(params.size() > 6 ? Utils.Validator.sanitizar(params.get(6)) : "/mam/storage/" + codigo.toLowerCase() + ".mp4");
        m.setIdResponsable(params.size() > 7 ? Utils.Validator.validarId(params.get(7), "id_responsable", getComandosMaterial()) : 3);
        m.setEstado("LISTO");
        m.setObservaciones(params.size() > 8 ? Utils.Validator.sanitizar(params.get(8)) : "Material ingestado y listo para broadcast");
        dao.create(m);
    }

    public void actualizarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: material_video actualizar_estado id,estado(INGESTADO|TRANSCODIFICADO|LISTO|CUARENTENA|DESCARTADO)",
                    getComandosMaterial()
            );
        }
        int id = Utils.Validator.validarId(params.get(0), "id_material", getComandosMaterial());
        String estado = Utils.Validator.validarEstado(params.get(1), new String[]{"INGESTADO", "TRANSCODIFICADO", "LISTO", "CUARENTENA", "DESCARTADO"}, "estado", getComandosMaterial());
        dao.updateEstado(id, estado);
    }

    public void eliminarMaterial(int id) throws SQLException {
        dao.delete(id);
    }

    public List<String[]> listarMateriales() throws SQLException {
        return dao.listAll();
    }

    public List<String[]> listarPorPrograma(int idPrograma) throws SQLException {
        return dao.listByPrograma(idPrograma);
    }

    public String[] consultarMaterial(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosMaterial() {
        return "Comandos CU4 - Gestión de Materiales de Video:\n" +
                "- material_video listar\n" +
                "- material_video consultar id\n" +
                "- material_video listar_programa id_programa\n" +
                "- material_video agregar id_programa,codigo_material,titulo_clip,formato,resolucion,duracion_segundos,ruta_servidor,id_responsable\n" +
                "- material_video actualizar_estado id,estado(INGESTADO|TRANSCODIFICADO|LISTO|CUARENTENA|DESCARTADO)\n" +
                "- material_video eliminar id";
    }
}
