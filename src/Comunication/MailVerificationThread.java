package Comunication;

import Interfaces.IEmailEventListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.AuthenticationException;
import Utils.Command;
import Utils.Email;
import Utils.Extractor;

public class MailVerificationThread implements Runnable {

    private final static int PORT_POP = 110;
    private final static int PORT_IMAP = 143;
    private final static int PORT_SMTP = 25;

    private final static String HOST = "mail.tecnoweb.org.bo";
    private final static String USER = "grupo12sc";
    private final static String PASSWORD = "grup012grup012*";

    private final static int CONNECT_TIMEOUT_MS = 5000;
    private final static int LOOP_DELAY_MS = 5000;

    /*
     * Cambia a true solo cuando quieras depurar.
     * En false no imprimirÃ¡ conexion establecida, conexion cerrada,
     * cantidad de correos ni estado del hilo a cada rato.
     */
    private final static boolean DEBUG = false;

    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    private Boolean lastImapStatus;
    private Boolean lastPopStatus;
    private Boolean lastSmtpStatus;

    private IEmailEventListener emailEventListener;

    public IEmailEventListener getEmailEventListener() {
        return emailEventListener;
    }

    public void setEmailEventListener(IEmailEventListener emailEventListener) {
        this.emailEventListener = emailEventListener;
    }

    public MailVerificationThread() {
        socket = null;
        input = null;
        output = null;
        lastImapStatus = null;
        lastPopStatus = null;
        lastSmtpStatus = null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean imapAvailable = isPortAvailable(HOST, PORT_IMAP, CONNECT_TIMEOUT_MS);
                notifyStatusIfChanged("IMAP", PORT_IMAP, imapAvailable, 1);

                boolean smtpAvailable = isPortAvailable(HOST, PORT_SMTP, CONNECT_TIMEOUT_MS);
                notifyStatusIfChanged("SMTP", PORT_SMTP, smtpAvailable, 2);

                List<Email> emails = null;

                socket = openSocket(HOST, PORT_POP, CONNECT_TIMEOUT_MS);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());

                debug("Conexion POP3 establecida");

                notifyStatusIfChanged("POP3", PORT_POP, true, 0);

                authUser(USER, PASSWORD);

                int count = getEmailCount();

                if (count > 0) {
                    emails = getEmails(count);
                    debug("Correos encontrados: " + count);
                    deleteEmails(count);
                }

                output.writeBytes(Command.quit());
                input.readLine();

                cerrarConexionPop3();

                debug("Conexion POP3 cerrada");

                if (count > 0 && emailEventListener != null) {
                    emailEventListener.onReceiveEmailEvent(emails);
                }

                Thread.sleep(LOOP_DELAY_MS);

            } catch (IOException ex) {
                notifyStatusIfChanged("POP3", PORT_POP, false, 0);
                Logger.getLogger(MailVerificationThread.class.getName()).log(Level.SEVERE, null, ex);
                cerrarConexionPop3();
                safeSleep(LOOP_DELAY_MS);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                debug("Hilo interrumpido");
                break;
            }
        }
    }

    private Socket openSocket(String host, int port, int timeoutMs) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(host, port), timeoutMs);
        return s;
    }

    private boolean isPortAvailable(String host, int port, int timeoutMs) {
        Socket s = new Socket();

        try {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void notifyStatusIfChanged(String protocol, int port, boolean available, int serverType) {
        Boolean last = null;

        if (serverType == 0) {
            last = lastPopStatus;
        } else if (serverType == 1) {
            last = lastImapStatus;
        } else if (serverType == 2) {
            last = lastSmtpStatus;
        }

        if (last != null && last.booleanValue() == available) {
            return;
        }

        if (serverType == 0) {
            lastPopStatus = available;
        } else if (serverType == 1) {
            lastImapStatus = available;
        } else if (serverType == 2) {
            lastSmtpStatus = available;
        }

        mostrarDashboardServidores();
    }

    private void mostrarDashboardServidores() {
        String smtpColor = lastSmtpStatus != null && lastSmtpStatus ? "#2e7d32" : "#c62828";
        String imapColor = lastImapStatus != null && lastImapStatus ? "#2e7d32" : "#c62828";
        String pop3Color = lastPopStatus != null && lastPopStatus ? "#2e7d32" : "#c62828";

        String smtpStatus = lastSmtpStatus != null && lastSmtpStatus ? "ACTIVO" : "INACTIVO";
        String imapStatus = lastImapStatus != null && lastImapStatus ? "ACTIVO" : "INACTIVO";
        String pop3Status = lastPopStatus != null && lastPopStatus ? "ACTIVO" : "INACTIVO";

        String html = "<html><body style='font-family: Arial; padding: 15px; background-color: #f5f5f5;'>" +
                "<h2 style='text-align: center; color: #1a1a1a; margin: 0 0 20px 0;'>ESTADO DE SERVIDORES DE CORREO</h2>" +
                "<table style='width: 100%; border-collapse: collapse; margin-bottom: 15px;'>" +
                "<tr style='background-color: #6A1B9A;'>" +
                "<th style='color: white; padding: 10px; border: 1px solid #4A148C; text-align: left;'>Protocolo</th>" +
                "<th style='color: white; padding: 10px; border: 1px solid #4A148C; text-align: left;'>Host</th>" +
                "<th style='color: white; padding: 10px; border: 1px solid #4A148C; text-align: left;'>Puerto</th>" +
                "<th style='color: white; padding: 10px; border: 1px solid #4A148C; text-align: center;'>Estado</th>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>SMTP</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + HOST + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + PORT_SMTP + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd; text-align: center; background-color: " + smtpColor + "; color: white; font-weight: bold;'>" + smtpStatus + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>IMAP</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + HOST + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + PORT_IMAP + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd; text-align: center; background-color: " + imapColor + "; color: white; font-weight: bold;'>" + imapStatus + "</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>POP3</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + HOST + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd;'>" + PORT_POP + "</td>" +
                "<td style='padding: 10px; border: 1px solid #ddd; text-align: center; background-color: " + pop3Color + "; color: white; font-weight: bold;'>" + pop3Status + "</td>" +
                "</tr>" +

                "</table>" +
                "<p style='font-size: 12px; color: #666; margin: 0;'>Monitoreando estado cada 5 segundos...</p>" +
                "</body></html>";

        Home.mostraRespuesta(html);
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            input.readLine();

            output.writeBytes(Command.user(email));
            input.readLine();

            output.writeBytes(Command.pass(password));
            String message = input.readLine();

            if (message.contains("-ERR")) {
                throw new AuthenticationException();
            }
        }
    }

    private int getEmailCount() throws IOException {
        output.writeBytes(Command.stat());

        String line = input.readLine();
        String[] data = line.split(" ");

        return Integer.parseInt(data[1]);
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            output.writeBytes(Command.retr(i));

            String text = readMultiline();
            emails.add(Extractor.getEmail(text));
        }

        return emails;
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            output.writeBytes(Command.dele(i));
        }
    }

    private String readMultiline() throws IOException {
        StringBuilder lines = new StringBuilder();

        boolean firstLine = true;

        while (true) {
            String line = input.readLine();

            if (line == null) {
                throw new IOException("Servidor no responde. Ocurrio un error al abrir el correo.");
            }

            if (line.equals(".")) {
                break;
            }

            if (!firstLine) {
                lines.append('\n');
            }

            lines.append(line);
            firstLine = false;
        }

        return lines.toString();
    }

    private void cerrarConexionPop3() {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }

        input = null;
        output = null;
        socket = null;
    }

    private void safeSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private void debug(String mensaje) {
        if (DEBUG) {
            System.out.println(mensaje);
        }
    }
}