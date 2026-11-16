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

    private static int getPortPopInt() {
        try {
            return Integer.parseInt(Database.ConfigEmailServer.getPortPop());
        } catch (Exception e) {
            return 110;
        }
    }

    private static int getPortSmtpInt() {
        try {
            return Integer.parseInt(Database.ConfigEmailServer.getPortSmtp());
        } catch (Exception e) {
            return 25;
        }
    }

    private final static int PORT_POP = getPortPopInt();
    private final static int PORT_IMAP = 143;
    private final static int PORT_SMTP = getPortSmtpInt();

    private final static String HOST = Database.ConfigEmailServer.getHost();
    private final static String USER = Database.ConfigEmailServer.USER;
    private final static String PASSWORD = Database.ConfigEmailServer.PASSWORD;

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
                boolean smtpAvailable = isPortAvailable(HOST, PORT_SMTP, CONNECT_TIMEOUT_MS);
                notifyStatusIfChanged("SMTP", PORT_SMTP, smtpAvailable, 2);

                List<Email> emails = null;

                socket = openSocket(HOST, PORT_POP, CONNECT_TIMEOUT_MS);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());

                debug("Conexion POP3 establecida");
                logProtocol("POP3", "S", "conexion establecida con " + HOST + ":" + PORT_POP);

                notifyStatusIfChanged("POP3", PORT_POP, true, 0);

                authUser(USER, PASSWORD);

                int count = getEmailCount();
                if (count > 0) {
                    logProtocol("POP3", "S", "STAT -> " + count + " mensaje(s)");
                    emails = getEmails(count);
                    deleteEmails(count);
                }

                String quit = Command.quit();
                sendCommand("POP3", quit);
                readResponse("POP3");

                cerrarConexionPop3();

                debug("Conexion POP3 cerrada");
                logProtocol("POP3", "I", "sesion POP3 cerrada normalmente; nuevo sondeo en "
                        + (LOOP_DELAY_MS / 1000) + " segundos");

                if (count > 0 && emailEventListener != null) {
                    emailEventListener.onReceiveEmailEvent(emails);
                }

                Thread.sleep(LOOP_DELAY_MS);

            } catch (IOException ex) {
                notifyStatusIfChanged("POP3", PORT_POP, false, 0);
                Logger.getLogger(MailVerificationThread.class.getName()).log(Level.SEVERE, null, ex);
                logProtocol("POP3", "!", "error de comunicacion: " + ex.getMessage());
                cerrarConexionPop3();
                safeSleep(LOOP_DELAY_MS);

            } catch (LinkageError error) {
                notifyStatusIfChanged("POP3", PORT_POP, false, 0);
                logProtocol("POP3", "!", "dependencia de ejecucion faltante: " + error.getMessage());
                cerrarConexionPop3();
                safeSleep(LOOP_DELAY_MS);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                debug("Hilo interrumpido");
                logProtocol("POP3", "!", "hilo interrumpido");
                break;

            } catch (Exception ex) {
                notifyStatusIfChanged("POP3", PORT_POP, false, 0);
                logProtocol("POP3", "!", "error procesando correo: " + ex.getMessage());
                cerrarConexionPop3();
                safeSleep(LOOP_DELAY_MS);
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
        String smtpStatus = lastSmtpStatus != null && lastSmtpStatus ? "ACTIVO" : "INACTIVO";
        String pop3Status = lastPopStatus != null && lastPopStatus ? "ACTIVO" : "INACTIVO";

        System.out.println("[MONITOR SERVIDORES] POP3: " + pop3Status + " | SMTP: " + smtpStatus);
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            readResponse("POP3");

            sendCommand("POP3", Command.user(email));
            readResponse("POP3");

            sendCommand("POP3", Command.pass(password));
            String message = readResponse("POP3");

            if (message.contains("-ERR")) {
                throw new AuthenticationException();
            }
        }
    }

    private int getEmailCount() throws IOException {
        String command = Command.stat();
        sendCommand("POP3", command);
        String line = readResponse("POP3");
        String[] data = line.split(" ");

        return Integer.parseInt(data[1]);
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            sendCommand("POP3", Command.retr(i));
            String status = readResponse("POP3");

            if (status != null && status.startsWith("+OK")) {
                String text = readMultiline();
                Email email = Extractor.getEmail(text);
                emails.add(email);
                logProtocol("POP3", "S", "mensaje " + i + " recibido | From: "
                        + email.getFrom() + " | Subject: " + email.getSubject());
            }
        }

        return emails;
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            sendCommand("POP3", Command.dele(i));
            readResponse("POP3");
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

    private void sendCommand(String protocol, String command) throws IOException {
        String visibleCommand = command.replace("\r", "").replace("\n", "");
        if (visibleCommand.regionMatches(true, 0, "PASS ", 0, 5)) {
            visibleCommand = "PASS ********";
        }
        logProtocol(protocol, "C", visibleCommand);
        output.writeBytes(command);
        output.flush();
    }

    private String readResponse(String protocol) throws IOException {
        String response = input.readLine();
        if (response == null) {
            throw new IOException(protocol + ": el servidor cerró la conexión sin responder");
        }
        logProtocol(protocol, "S", response);
        return response;
    }

    private void logProtocol(String protocol, String direction, String message) {
        System.out.println("[" + protocol + "][" + direction + "] " + message);
    }
}