/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comunication;

//import com.juanvladimir13.template.MaryTemplate;
import Database.ConfigEmailServer;
import Utils.Email;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class SendEmail {

    private static int getPortSmtpInt() {
        try {
            return Integer.parseInt(ConfigEmailServer.getPortSmtp());
        } catch (Exception e) {
            return 25;
        }
    }

    private final static int PORT_SMTP = getPortSmtpInt();
    private final static String SERVER = ConfigEmailServer.getHost();
    private final static String user_emisor = ConfigEmailServer.MAIL;
    private static String user_receptor;
    private static Socket socket;
    private static BufferedReader entrada;
    private static DataOutputStream salida;

    public SendEmail() {
        this.user_receptor = "";

    }

    public String getUser_receptor() {
        return user_receptor;
    }

    public void setUser_receptor(String user_receptor) {
        this.user_receptor = user_receptor;
    }

    public void responseUser(Email email, String mensaje) {
        // Estableciendo variables
        BufferedReader reader;
        DataOutputStream writer;
        String command;
        System.out.println("Enviando correo de respuesta a: " + email.getFrom());

        try {
            // Estableciendo Conexion Socket
            Socket socket = new Socket(SERVER, PORT_SMTP);
            socket.setSoTimeout(15000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new DataOutputStream(socket.getOutputStream());

            if (socket != null && reader != null && writer != null) {
                logSmtp("S", reader.readLine());

                // Saludar al servidor
                command = "HELO " + SERVER + "\r\n";
                sendSmtpCommand(writer, command);
                logSmtp("S", reader.readLine());

                command = "MAIL FROM:<" + user_emisor + ">\r\n";
                sendSmtpCommand(writer, command);
                logSmtp("S", reader.readLine());

                String origen = email.getFrom();
                if (esEmailValido(origen)) {
                    command = "RCPT TO:<" + origen.trim() + ">\r\n";
                    sendSmtpCommand(writer, command);
                    String rcptRes = reader.readLine();
                    logSmtp("S", rcptRes);
                    if (rcptRes == null || !rcptRes.startsWith("250")) {
                        logSmtp("!", "Correo de respuesta no enviado: destinatario no aceptado por SMTP: " + rcptRes);
                        try {
                            command = "QUIT\r\n";
                            sendSmtpCommand(writer, command);
                            logSmtp("S", reader.readLine());
                        } catch (Exception ignored) {}
                        writer.close();
                        reader.close();
                        socket.close();
                        return;
                    }
                } else {
                    logSmtp("!", "Correo de respuesta no enviado: remitente invalido (" + origen + ")");
                    writer.close();
                    reader.close();
                    socket.close();
                    return;
                }

                // Escribir Mensaje
                // Comenzar DATA
                command = "DATA\r\n";
                sendSmtpCommand(writer, command);
                logSmtp("S", reader.readLine());

                // Construir cabeceras minimas validas (From, To, Subject, MIME)
                StringBuilder data = new StringBuilder();
                data.append("From: <").append(user_emisor).append(">\r\n");

                List<String> toList = new ArrayList<>();
                if (esEmailValido(email.getFrom())) {
                    toList.add(email.getFrom().trim());
                }

                if (!toList.isEmpty()) {
                    data.append("To: ");
                    for (int i = 0; i < toList.size(); i++) {
                        if (i > 0) data.append(", ");
                        data.append("<").append(toList.get(i)).append(">");
                    }
                    data.append("\r\n");
                }

                // Sanitize subject: remove or replace any internal newlines to avoid header injection
                // Subject con codificacion MIME RFC 2047 si contiene caracteres no ASCII (tildes, eñes)
                String subject = "";
                if (email.getSubject() != null) {
                    subject = email.getSubject().toString().replaceAll("\\R+", " ").trim();
                    try {
                        subject = MimeUtility.encodeText(subject, "UTF-8", "B");
                    } catch (Exception ignored) {}
                }
                data.append("Subject: ").append(subject).append("\r\n");

                // Cuerpo: normalizar saltos de linea a CRLF y aplicar dot-stuffing solo al cuerpo
                InlineEmailContent inlineContent = prepararContenidoInline(mensaje);

                String cuerpoSeguridad = normalizeForSmtp(inlineContent.htmlSanitizado)
                        .replace("\r\n.", "\r\n..");
                data.append("MIME-Version: 1.0\r\n");

                if (inlineContent.hasImages()) {
                    data.append("Content-Type: multipart/related; boundary=related-boundary-1\r\n");
                } else {
                    data.append("Content-Type: text/html; charset=UTF-8\r\n");
                    data.append("Content-Transfer-Encoding: 8bit\r\n");
                }

                data.append("\r\n"); 

                if (inlineContent.hasImages()) {
                    data.append("--related-boundary-1\r\n");
                    data.append("Content-Type: text/html; charset=UTF-8\r\n");
                    data.append("Content-Transfer-Encoding: 8bit\r\n\r\n");
                    data.append(cuerpoSeguridad);
                    data.append("\r\n");

                    for (InlineImage inlineImage : inlineContent.imagenes) {
                        data.append("--related-boundary-1\r\n");
                        data.append("Content-Type: ").append(inlineImage.contentType).append("\r\n");
                        data.append("Content-Transfer-Encoding: base64\r\n");
                        data.append("Content-ID: <").append(inlineImage.cid).append(">\r\n");
                        data.append("Content-Disposition: inline; filename=\"").append(inlineImage.cid).append("\"\r\n\r\n");
                        data.append(inlineImage.base64).append("\r\n");
                    }
                    data.append("--related-boundary-1--\r\n");
                } else {
                    data.append(cuerpoSeguridad);
                    if (!cuerpoSeguridad.endsWith("\r\n")) data.append("\r\n");
                }

                data.append(".\r\n");

                byte[] rawBytes = data.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                writer.write(rawBytes);
                writer.flush();
                String dataResponse = reader.readLine();
                if (dataResponse != null) {
                    logSmtp("S", dataResponse);
                } else {
                    logSmtp("!", "Correo de respuesta no enviado: el servidor no respondió después del cuerpo");
                }

                command = "QUIT\r\n";
                sendSmtpCommand(writer, command);
                String quitResponse = reader.readLine();
                if (quitResponse != null) {
                    logSmtp("S", quitResponse);
                }

                System.out.println("Correo de respuesta enviado exitosamente a: " + email.getFrom());

            }

            // Cerrar Conexion
            writer.close();
            reader.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            logSmtp("!", "Correo de respuesta no enviado: no se pudo conectar con el servidor indicado");
        } catch (IOException ex) {
            logSmtp("!", "Correo de respuesta no enviado: " + ex.getMessage());
        } catch (Exception ex) {
            logSmtp("!", "Correo de respuesta no enviado: " + ex.getMessage());
        }
    }

    private static void sendSmtpCommand(DataOutputStream writer, String command) throws IOException {
        System.out.println("[SMTP][C] " + command.replace("\r", "").replace("\n", ""));
        writer.writeBytes(command);
        writer.flush();
    }

    private static void logSmtp(String direction, String message) {
        if (message != null) {
            System.out.println("[SMTP][" + direction + "] " + message);
        }
    }

    static protected String getMultiline(BufferedReader in) throws IOException {
        String lines = "";
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.charAt(3) == ' ') {
                lines = lines + "\n" + line;
                // No more lines in the server response
                break;
            }
            // Add read line to the list of lines
            lines = lines + "\n" + line;
        }
        return lines;
    }
    private boolean esEmailValido(String email) {
        if (email == null) return false;
        String clean = email.trim();
        if (clean.startsWith("<") && clean.endsWith(">")) {
            clean = clean.substring(1, clean.length() - 1).trim();
        }
        // Acepta correos de internet estandar (con TLD) o correos locales/Docker de intranet (ej: admin@mailserver)
        return clean.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+$");
    }

    ///////////////////////////////////////////////////////////////
    public void responseEmail(String emailReceptor, String codigoHTML) {
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.put("mail.smtp.starttls.enable", "true");
            p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            p.put("mail.smtp.port", "587");
            p.put("mail.smtp.auth", "true");
            Session s = Session.getDefaultInstance(p);

            MimeMessage mensaje = new MimeMessage(s);
            mensaje.setFrom(new InternetAddress("medinytalimber.123@gmail.com"));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceptor));
            mensaje.setSubject("NOTIFICACION");
            //mensaje.setText("informacion");
            mensaje.setContent(codigoHTML, "text/html");//
            Transport t = s.getTransport("smtp");
            t.connect("medinytalimber.123@gmail.com", "xgyxhcqqwxjkmydj");
            System.out.println("enviando...");
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            System.out.println("mensaje enviado");
        } catch (MessagingException ex) {
            Logger.getLogger(SendEmail.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("correo no enviado");
        }
    }

    public void sendmail(String receptor, String subject, String html) {
        if (!esEmailValido(receptor)) {
            System.err.println("[SMTP] Email invalido: " + receptor);
            return;
        }
        try {
            // Intentar primero con el servidor oficial Tecnoweb (Socket SMTP puerto 25)
            Email email = new Email(receptor, subject != null ? subject : "Respuesta Sistema TV");
            responseUser(email, html != null ? html : "");
        } catch (Exception ex) {
            System.err.println("[SMTP ERROR] Error enviando correo via socket SMTP: " + ex.getMessage());
            // Fallback a JavaMail si fuera necesario
            try {
                Properties p = new Properties();
                p.put("mail.smtp.host", SERVER);
                p.put("mail.smtp.port", String.valueOf(PORT_SMTP));
                p.put("mail.smtp.auth", "false");
                Session s = Session.getInstance(p);
                MimeMessage mensaje = new MimeMessage(s);
                mensaje.setFrom(new InternetAddress(user_emisor));
                mensaje.setSubject(subject != null ? subject : "Respuesta Sistema TV", "UTF-8");
                mensaje.setContent(html != null ? html : "", "text/html; charset=UTF-8");
                mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(receptor.trim()));
                Transport.send(mensaje);
                System.out.println("[SMTP] Correo enviado exitosamente via JavaMail a: " + receptor);
            } catch (Exception e2) {
                System.err.println("[SMTP FALLBACK ERROR] " + e2.getMessage());
            }
        }
    }

    public void sendmail(String receptor, String subject) {
        sendmail(receptor, subject, "<h2>Notificacion del Sistema</h2><p>" + subject + "</p>");
    }

    public String mensajeHtml(String mensaje) {
        String html = "<h2>%s</h2>";
        return String.format(html, mensaje);
    }

    public String cargarHtml(String s) {
        String html = "<center>\n"
                + "  <br>\n" + s + "  <br>\n"
                + "</center>";
        return html;
    }

    public String[] getEmailsReceptores() {
        return new String[0];
    }

        // Normaliza saltos de lÃ­nea a CRLF y aplica dot-stuffing para DATA SMTP
        private String normalizeForSmtp(String s) {
            if (s == null) return "";
            // Normalize various line endings to LF first
            String tmp = s.replace("\r\n", "\n").replace("\r", "\n");
            // Convert LF to CRLF
            String crlf = tmp.replace("\n", "\r\n");
            // Dot-stuffing: lines that start with a dot must be prefixed with another dot
            if (crlf.startsWith(".")) crlf = "." + crlf;
            crlf = crlf.replace("\r\n.", "\r\n..");
            return crlf;
        }

        private InlineEmailContent prepararContenidoInline(String html) {
            InlineEmailContent content = new InlineEmailContent();

            if (html == null || html.trim().isEmpty()) {
                content.htmlSanitizado = "";
                return content;
            }

            Pattern pattern = Pattern.compile(
                    "<img([^>]*)src=[\"']data:(image/[^;]+);base64,([^\"']+)[\"']([^>]*)>",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = pattern.matcher(html);
            StringBuffer sanitized = new StringBuffer();

            int contador = 0;

            while (matcher.find()) {
                String antesSrc = matcher.group(1);
                String contentType = matcher.group(2);
                String base64 = matcher.group(3);
                String despuesSrc = matcher.group(4);

                base64 = limpiarBase64(base64);

                String cid = "imagen_inline_" + contador;

                content.imagenes.add(new InlineImage(cid, contentType, cortarBase64(base64)));

                String replacement =
                        "<img" + antesSrc +
                        "src=\"cid:" + cid + "\"" +
                        despuesSrc +
                        ">";

                matcher.appendReplacement(sanitized, Matcher.quoteReplacement(replacement));

                contador++;
            }

            matcher.appendTail(sanitized);
            content.htmlSanitizado = sanitized.toString();

            return content;
        }

        private String limpiarBase64(String base64) {
            if (base64 == null) {
                return "";
            }

            return base64
                    .replace("\r", "")
                    .replace("\n", "")
                    .replace(" ", "")
                    .trim();
        }

        private String cortarBase64(String base64) {
            if (base64 == null || base64.isEmpty()) {
                return "";
            }

            String limpio = limpiarBase64(base64);
            StringBuilder sb = new StringBuilder();

            int limite = 76;

            for (int i = 0; i < limpio.length(); i += limite) {
                int fin = Math.min(i + limite, limpio.length());
                sb.append(limpio, i, fin).append("\r\n");
            }

            return sb.toString();
        }

        private static class InlineImage {
            private final String cid;
            private final String contentType;
            private final String base64;

            private InlineImage(String cid, String contentType, String base64) {
                this.cid = cid;
                this.contentType = contentType;
                this.base64 = base64;
            }
        }

        private static class InlineEmailContent {
            private String htmlSanitizado = "";
            private final List<InlineImage> imagenes = new ArrayList<>();

            private boolean hasImages() {
                return !imagenes.isEmpty();
            }
        }

public String comandosHelp() {
    return "<html><body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f8fafc; color: #1e293b;'>" +
            "<h2 style='color:#0284c7;'>Comandos del Sistema de Parrilla de Televisión (Playout)</h2>" +
            "<p>Escriba el comando en el asunto del correo o consulte la ayuda de cada módulo.</p>" +

            "<h3>CU1. Gestión de Usuarios y Roles</h3>" +
            "<ul><li>usuario help</li><li>rol help</li></ul>" +

            "<h3>CU2. Gestión de Catálogo de Programas</h3>" +
            "<ul><li>programa help</li><li>categoria help</li></ul>" +

            "<h3>CU3. Gestión de Confeccionar y Controlar la Parrilla</h3>" +
            "<ul><li>parrilla help</li><li>item_parrilla help</li></ul>" +

            "<h3>CU4. Gestión de Gestionar Materiales de Video (MAM)</h3>" +
            "<ul><li>material help</li></ul>" +

            "<h3>CU5. Gestión de Validar Estado de Emisión (Control de Calidad QC)</h3>" +
            "<ul><li>validacion help</li></ul>" +

            "<h3>CU6. Gestión de Pauta Publicitaria e Insertar Spots (Playout)</h3>" +
            "<ul><li>cliente help</li><li>spot help</li><li>pauta help</li></ul>" +

            "<h3>CU7. Gestión de Conciliar Emisión Real (As-Run Log) y Auditoría</h3>" +
            "<ul><li>emision help</li><li>auditoria help</li></ul>" +

            "<h3>CU8. Reportes y Estadísticas</h3>" +
            "<ul><li>reporte help</li></ul>" +

            "<p style='margin-top: 25px; color:#64748b; font-size: 13px;'>" +
            "Ejemplo de comando rápido: <b>parrilla listar</b> o <b>reporte general</b>" +
            "</p>" +
            "</body></html>";
}


    public String mensajeComandos(String mensajeHtml) {
        String[] partes = mensajeHtml.split("<br>");
        String mensaje = "";
        for (int i = 0; i < partes.length; i++) {
            mensaje = mensaje + partes[i] + "\r\n";
        }
        return mensaje;
    }

}
