package Database;

public class ConfigDB {
    public static final String DRIVER = "org.postgresql.Driver";
    public static final String DB = "db_grupo12sc";
    public static final String USER = "grupo12sc";
    public static final String PASSWORD = "grup012grup012*";

    public static String getHost() {
        String host = System.getenv("DB_HOST");
        if (host != null && !host.trim().isEmpty()) {
            return host.trim();
        }
        return "mail.tecnoweb.org.bo";
    }

    public static String getPort() {
        String port = System.getenv("DB_PORT");
        if (port != null && !port.trim().isEmpty()) {
            return port.trim();
        }
        return "5432";
    }

    public static String getUrl() {
        return "jdbc:postgresql://" + getHost() + ":" + getPort() + "/";
    }

    public static final String URL = "jdbc:postgresql://" + getHost() + ":" + getPort() + "/";
}
