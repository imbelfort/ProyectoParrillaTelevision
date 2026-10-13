package Services;

import DAO.ReporteDAO;

import java.sql.SQLException;
import java.util.List;

public class ReporteService {

    private ReporteDAO dao;

    public ReporteService() {
        this.dao = new ReporteDAO();
    }

    public List<String[]> generarReporteGeneral() throws SQLException {
        return dao.reporteGeneral();
    }

    public List<String[]> generarReporteParrilla() throws SQLException {
        return dao.reporteParrilla();
    }

    public List<String[]> generarReporteProgramas() throws SQLException {
        return dao.reporteProgramas();
    }

    public List<String[]> generarReportePautas() throws SQLException {
        return dao.reportePautas();
    }

    public List<String[]> generarReporteCumplimiento() throws SQLException {
        return dao.reporteCumplimiento();
    }

    public String getComandosReporte() {
        return "Comandos CU8 - Reportes y Estadísticas:\n" +
                "- reporte general\n" +
                "- reporte parrilla\n" +
                "- reporte programas\n" +
                "- reporte pautas\n" +
                "- reporte cumplimiento";
    }
}