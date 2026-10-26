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
            throw new Utils.CommandException(Utils.ErrorCode.ERR_INC_002,
                    "Formato: spot agregar id_cliente,codigo_spot,titulo_spot [duracion_segundos]",
                    getComandosSpot()
            );
        }
        SpotPublicitario s = new SpotPublicitario();
        int idCliente = Utils.Validator.validarId(params.get(0), "id_cliente", getComandosSpot());
        String codigoSpot = Utils.Validator.validarCodigo(params.get(1), "codigo_spot", getComandosSpot());
        String tituloSpot = Utils.Validator.validarTextoSeguro(params.get(2), "titulo_spot", 2, 100, getComandosSpot());
        int duracion = params.size() > 3 ? Utils.Validator.validarDuracion(params.get(3), "duracion_segundos", getComandosSpot()) : 30;

        long hoy = System.currentTimeMillis();
        Date inicio = params.size() > 4 ? Utils.Validator.validarFecha(params.get(4), "fecha_vigencia_inicio", getComandosSpot()) : new Date(hoy);
        Date fin = params.size() > 5 ? Utils.Validator.validarFecha(params.get(5), "fecha_vigencia_fin", getComandosSpot()) : new Date(hoy + 365L * 24 * 3600 * 1000);

        s.setIdCliente(idCliente);
        s.setCodigoSpot(codigoSpot);
        s.setTituloSpot(tituloSpot);
        s.setDuracionSegundos(duracion);
        s.setFechaVigenciaInicio(inicio);
        s.setFechaVigenciaFin(fin);
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
