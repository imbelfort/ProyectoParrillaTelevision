package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class Conexion {

    private static Conexion instancia;
    private Connection con;
    
    public Conexion(){
        this.con = null;
    }
    
    public Connection EstablecerConexion(){
        try {
            Class.forName(ConfigDB.DRIVER);
            this.con = DriverManager.getConnection(
                ConfigDB.URL + ConfigDB.DB, 
                ConfigDB.USER, 
                ConfigDB.PASSWORD
            );
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error DB: "+e);
        }
        return this.con;
    }
    
    public void CerrarConexion(){
        try {
            if (this.con != null) {
                this.con.close();
            }
        } catch (SQLException e) {
            System.out.println("Error DB: "+e);
        }
    }
    
    public static Conexion getInstancia(){
        if(instancia == null){
            instancia = new Conexion();
        }
        return instancia;
    }

 
}
