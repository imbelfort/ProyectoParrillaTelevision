package Database;

public class ConfigDB {
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String DB = "db_grupo12sc";
    public static final String USER = "grupo12sc";
    public static final String PASSWORD = "grup012grup012*";
    public static final String HOST = "mail.tecnoweb.org.bo";
    public static final String PORT = "5432";
    public static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/";

    public static String getDb() {
        return DB;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getHost() {
        return HOST;
    }

    public static String getPort() {
        return PORT;
    }

    public static String getUrl() {
        return URL;
    }
}
