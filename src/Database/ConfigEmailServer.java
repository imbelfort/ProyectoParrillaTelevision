package Database;

public class ConfigEmailServer {
    public static String getHost() {
        String envHost = System.getenv("MAIL_HOST");
        if (envHost != null && !envHost.trim().isEmpty()) {
            return envHost.trim();
        }
        return "mail.tecnoweb.org.bo";
    }

    public static String getPortSmtp() {
        String envPort = System.getenv("MAIL_SMTP_PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            return envPort.trim();
        }
        return "25";
    }

    public static String getPortPop() {
        String envPort = System.getenv("MAIL_POP_PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            return envPort.trim();
        }
        return "110";
    }

    public static String PORT_SMTP = getPortSmtp();
    public static String PROTOCOL = "smtp";
    public static String HOST = getHost();
    public static String USER = "grupo12sc";
    public static String PASSWORD = "grup012grup012*";
    public static String MAIL = "grupo12sc@tecnoweb.org.bo";
    public static String MAIL_PASSWORD = "grup012grup012*";
}
