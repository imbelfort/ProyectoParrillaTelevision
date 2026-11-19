package Database;

public class ConfigEmailServer {
    public static final String HOST = "mail.tecnoweb.org.bo";
    public static final String PORT_SMTP = "25";
    public static final String PORT_POP = "110";
    public static final String PORT_IMAP = "143";
    public static final String USER = "grupo12sc";
    public static final String PASSWORD = "grup012grup012*";
    public static final String MAIL = "grupo12sc@tecnoweb.org.bo";
    public static final String MAIL_PASSWORD = "grup012grup012*";
    public static final String PROTOCOL = "smtp";

    public static String getHost() {
        return HOST;
    }

    public static String getPortSmtp() {
        return PORT_SMTP;
    }

    public static String getPortPop() {
        return PORT_POP;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static String getMail() {
        return MAIL;
    }
}
