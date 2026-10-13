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
            throw new IllegalArgumentException(
                    "Formato: material agregar id_programa,codigo,titulo,duracion_segundos"
            );
        }
        MaterialVideo m = new MaterialVideo();
        m.setIdPrograma(Integer.parseInt(params.get(0).trim()));
        String codigo = params.get(1).trim().toUpperCase();
        m.setCodigoMaterial(codigo);
        m.setTituloClip(params.get(2).trim());
        m.setDuracionSegundos(Integer.parseInt(params.get(3).trim()));

        m.setFormato(params.size() > 4 ? params.get(4).trim().toUpperCase() : "HD_1080I");
        m.setResolucion(params.size() > 5 ? params.get(5).trim() : "1920x1080");
        m.setRutaServidor(params.size() > 6 ? params.get(6).trim() : "/mam/storage/" + codigo.toLowerCase() + ".mp4");
        m.setIdResponsable(params.size() > 7 ? Integer.parseInt(params.get(7).trim()) : 3);
        m.setEstado("LISTO");
        m.setObservaciones(params.size() > 8 ? params.get(8).trim() : "Material ingestado y listo para broadcast");
        dao.create(m);
    }

    public void actualizarEstado(List<String> params) throws SQLException {
        if (params.size() < 2) {
            throw new IllegalArgumentException("Formato: material_video actualizar_estado id,estado(INGESTADO|TRANSCODIFICADO|LISTO|CUARENTENA|DESCARTADO)");
        }
        int id = Integer.parseInt(params.get(0).trim());
        String estado = params.get(1).trim().toUpperCase();
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
