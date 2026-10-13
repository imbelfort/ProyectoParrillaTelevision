package Services;

import DAO.SpotPublicitarioDAO;
import Models.SpotPublicitario;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class SpotPublicitarioService {

    private SpotPublicitarioDAO dao;

    public SpotPublicitarioService() {
        this.dao = new SpotPublicitarioDAO();
    }

    public void crearSpot(List<String> params) throws SQLException {
        if (params.size() < 3) {
            throw new IllegalArgumentException(
                    "Formato: spot agregar id_cliente,codigo_spot,titulo_spot [duracion_segundos]"
            );
        }
        SpotPublicitario s = new SpotPublicitario();
        s.setIdCliente(Integer.parseInt(params.get(0).trim()));
        s.setCodigoSpot(params.get(1).trim().toUpperCase());
        s.setTituloSpot(params.get(2).trim());
        s.setDuracionSegundos(params.size() > 3 ? Integer.parseInt(params.get(3).trim()) : 30);

        long hoy = System.currentTimeMillis();
        s.setFechaVigenciaInicio(params.size() > 4 ? Date.valueOf(params.get(4).trim()) : new Date(hoy));
        s.setFechaVigenciaFin(params.size() > 5 ? Date.valueOf(params.get(5).trim()) : new Date(hoy + 365L * 24 * 3600 * 1000));
        s.setEstado("ACTIVO");
        dao.create(s);
    }

    public List<String[]> listarSpots() throws SQLException {
        return dao.listAll();
    }

    public String[] consultarSpot(int id) throws SQLException {
        return dao.readById(id);
    }

    public String getComandosSpot() {
        return "Comandos CU6 - Spots Publicitarios:\n" +
                "- spot listar\n" +
                "- spot consultar id_spot\n" +
                "- spot agregar id_cliente,codigo_spot,titulo_spot,duracion_segundos,fecha_inicio(yyyy-MM-dd),fecha_fin(yyyy-MM-dd)";
    }
}
