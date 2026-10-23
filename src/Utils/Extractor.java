/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;


public class Extractor {
    private static String GMAIL = "d=gmail";
    private static String HOTMAIL = "d=hotmail";
    private static String YAHOO = "d=yahoo";
    private static String UAGRM = "d=uagrm";
     private static String UASB = "d=uasb";
    
    
    public static Email getEmail(String plain_text){
        String normalizedText = plain_text == null ? "" : plain_text.replaceFirst("^\\s+", "");
        Email email = new Email(getFrom(normalizedText), getSubject(normalizedText));
        List<Email.Attachment> attachments = extractAttachments(normalizedText);
        email.setAttachments(attachments);
        if (!attachments.isEmpty()) {
            Email.Attachment attachment = attachments.get(0);
            email.setAttachmentFileName(attachment.getFileName());
            email.setAttachmentContentBase64(attachment.getContentBase64());
            email.setAttachmentContentType(attachment.getContentType());
        }
        return email;
    }

    private static List<Email.Attachment> extractAttachments(String plainText) {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(plainText.getBytes(StandardCharsets.ISO_8859_1)));
            List<Email.Attachment> found = new ArrayList<>();
            collectAttachmentsFromPart(message, found);
            if (!found.isEmpty()) {
                return found;
            }
        } catch (Exception ignored) {
        }

        List<Email.Attachment> fallback = extractAttachmentsFromRawText(plainText);
        if (!fallback.isEmpty()) {
            return fallback;
        }

        return new ArrayList<>();
    }

    private static byte[] toByteArray(java.io.InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    private static void collectAttachmentsFromPart(Part part, List<Email.Attachment> attachments) throws Exception {
        if (part.isMimeType("message/rfc822")) {
            Object nested = part.getContent();
            if (nested instanceof Part) {
                collectAttachmentsFromPart((Part) nested, attachments);
            }
        }

        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                collectAttachmentsFromPart(bodyPart, attachments);
            }
            return;
        }

        String disposition = part.getDisposition();
        String fileName = part.getFileName();
        String contentType = part.getContentType() != null ? part.getContentType().toLowerCase() : "";
        boolean isTextPart = part.isMimeType("text/*");
        boolean looksLikeAttachment = !isTextPart
            && (Part.ATTACHMENT.equalsIgnoreCase(disposition)
            || Part.INLINE.equalsIgnoreCase(disposition)
            || (fileName != null && !fileName.trim().isEmpty())
            || part.isMimeType("image/*")
            || contentType.startsWith("image/")
            || contentType.startsWith("application/"));

        if (!looksLikeAttachment) {
            return;
        }

        byte[] bytes = toByteArray(part.getInputStream());
        if (bytes.length == 0) {
            return;
        }

        attachments.add(new Email.Attachment(fileName, Base64.getEncoder().encodeToString(bytes), part.getContentType()));
    }

    private static List<Email.Attachment> extractAttachmentsFromRawText(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String normalized = plainText.replace("\r\n", "\n").replace('\r', '\n');
        String lower = normalized.toLowerCase();

        int attachmentHeader = lower.indexOf("content-disposition: attachment");
        if (attachmentHeader < 0) {
            attachmentHeader = lower.indexOf("filename=");
        }
        if (attachmentHeader < 0) {
            attachmentHeader = lower.indexOf("content-type: image/");
        }
        if (attachmentHeader < 0) {
            return new ArrayList<>();
        }

        List<Email.Attachment> attachments = new ArrayList<>();

        int fileNameIndex = lower.indexOf("filename=", attachmentHeader);
        if (fileNameIndex >= 0) {
            int fileNameStart = fileNameIndex + "filename=".length();
            int fileNameEnd = normalized.indexOf('\n', fileNameStart);
            if (fileNameEnd < 0) {
                fileNameEnd = normalized.length();
            }
            String rawFileName = normalized.substring(fileNameStart, fileNameEnd).trim();
            String fileName = rawFileName.replace("\"", "").replace(";", "").trim();
            attachments.add(new Email.Attachment(fileName, "", ""));
        }

        if (attachments.isEmpty()) {
            int nameIndex = lower.indexOf("name=", attachmentHeader);
            if (nameIndex >= 0) {
                int nameStart = nameIndex + "name=".length();
                int nameEnd = normalized.indexOf('\n', nameStart);
                if (nameEnd < 0) {
                    nameEnd = normalized.length();
                }
                String rawName = normalized.substring(nameStart, nameEnd).trim();
                String fileName = rawName.replace("\"", "").replace(";", "").trim();
                attachments.add(new Email.Attachment(fileName, "", ""));
            }
        }

        int encodingIndex = lower.indexOf("content-transfer-encoding: base64", attachmentHeader);
        if (encodingIndex < 0) {
            return new ArrayList<>();
        }

        int dataStart = normalized.indexOf("\n\n", encodingIndex);
        if (dataStart < 0) {
            return new ArrayList<>();
        }
        dataStart += 2;

        int dataEnd = normalized.indexOf("\n--", dataStart);
        if (dataEnd < 0) {
            dataEnd = normalized.length();
        }

        String base64Block = normalized.substring(dataStart, dataEnd)
                .replaceAll("\\s+", "")
                .trim();

        if (base64Block.trim().isEmpty()) {
            return new ArrayList<>();
        }

        if (attachments.isEmpty()) {
            attachments.add(new Email.Attachment("", base64Block, "application/octet-stream"));
        } else {
            Email.Attachment attachment = attachments.get(0);
            attachment.setContentBase64(base64Block);
            attachment.setContentType("application/octet-stream");
        }

        return attachments;
    }
    
    private static String getFrom(String plain_text){
        // 1) Intentar con Return-Path y From como cabeceras estÃ¡ndar
        String returnPath = getHeaderValueIgnoreCase(plain_text, "Return-Path");
        String from = getHeaderValueIgnoreCase(plain_text, "From");

        String email = extractEmailAddress(returnPath);
        if (!email.isEmpty()) {
            return email;
        }

        email = extractEmailAddress(from);
        if (!email.isEmpty()) {
            return email;
        }

        // 2) Fallback legado para mantener compatibilidad
        String search = "Return-Path: <";
        int index_begin = plain_text.indexOf(search);
        if (index_begin == -1) {
            search = "From: <";
            index_begin = plain_text.indexOf(search);
            if (index_begin == -1) {
                return "";
            }
        }
        index_begin += search.length();
        int index_end = plain_text.indexOf(">", index_begin);
        if (index_end == -1) {
            return "";
        }
        return plain_text.substring(index_begin, index_end).trim();
    }
    
    public static String getTo(String plain_text){
        if (plain_text == null || plain_text.isEmpty()) {
            return "";
        }
        String toHeader = getHeaderValueIgnoreCase(plain_text, "To");
        if (toHeader != null && !toHeader.trim().isEmpty()) {
            String extracted = extractEmailAddress(toHeader);
            if (!extracted.isEmpty()) {
                return extracted;
            }
        }
        if(plain_text.contains(GMAIL)){
            return getToFromGmail(plain_text);
        } else if(plain_text.contains(HOTMAIL)){
            return getToFromHotmail(plain_text);
        } else if(plain_text.contains(YAHOO)){
            return getToFromYahoo(plain_text);
        } else if(plain_text.contains(UAGRM)){
            return getToFromUAGRM(plain_text);
        } else if(plain_text.contains(UASB)){
            return getToFromUASB(plain_text);
        }
        return getToCommon(plain_text);
    }
    
    private static String getSubject(String plain_text){
        // Lectura robusta y agnÃ³stica al proveedor
        String subject = getHeaderValueIgnoreCase(plain_text, "Subject");
        if (subject != null) {
            return decodeMimeHeader(subject).trim();
        }

        // Fallback legado
        String search = "Subject: ";
        int i = plain_text.indexOf(search);
        if (i == -1) {
            return "";
        }
        i += search.length();

        String end_string = "";
        if(plain_text.contains(GMAIL)){
            end_string = "To:";
        } else if(plain_text.contains(HOTMAIL)){
            end_string = "Thread-Topic";
        } else if(plain_text.contains(YAHOO)){
            end_string = "MIME-Version:";
        }else if(plain_text.contains(UAGRM)){
            end_string = "To:";
        }else if(plain_text.contains(UASB)){
            end_string = "To:";
        }

        if (end_string.isEmpty()) {
            int eol = plain_text.indexOf("\n", i);
            if (eol == -1) {
                return plain_text.substring(i).trim();
            }
            return decodeMimeHeader(plain_text.substring(i)).trim();
        }

        int e = plain_text.indexOf(end_string, i);
        if(e==-1){
            int position=plain_text.indexOf("MIME",i);
            if(position==-1){
                int posicion=plain_text.indexOf("mime",i);
                if (posicion == -1) {
                    return decodeMimeHeader(plain_text.substring(i)).trim();
                }
                return decodeMimeHeader(plain_text.substring(i, posicion)).trim();
            }
            return decodeMimeHeader(plain_text.substring(i, position)).trim();
        }
        return decodeMimeHeader(plain_text.substring(i, e)).trim();
    }

    private static String getHeaderValueIgnoreCase(String plainText, String headerName) {
        String[] lines = plainText.split("\\r?\\n");
        String prefix = headerName.toLowerCase() + ":";

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line == null) {
                continue;
            }
            // Fin de cabeceras al encontrar linea vacia
            if (line.trim().isEmpty()) {
                break;
            }
            // Lineas con espacio o tab son continuaciones plegadas, no inicios de cabecera
            if (line.startsWith(" ") || line.startsWith("\t")) {
                continue;
            }
            if (line.toLowerCase().startsWith(prefix)) {
                StringBuilder value = new StringBuilder(line.substring(prefix.length()).trim());
                for (int j = i + 1; j < lines.length; j++) {
                    String continuation = lines[j];
                    if (continuation == null || continuation.isEmpty()) {
                        break;
                    }
                    if (!continuation.startsWith(" ") && !continuation.startsWith("\t")) {
                        break;
                    }
                    value.append(' ').append(continuation.trim());
                }
                return value.toString().trim();
            }
        }
        return null;
    }

    private static String decodeMimeHeader(String value) {
        if (value == null) {
            return "";
        }
        String unfolded = value.replaceAll("\\r?\\n[\\t ]+", " ").trim();
        try {
            return MimeUtility.decodeText(unfolded);
        } catch (UnsupportedEncodingException ex) {
            return unfolded;
        }
    }

    private static String extractEmailAddress(String rawHeaderValue) {
        if (rawHeaderValue == null) {
            return "";
        }
        String value = rawHeaderValue.trim();
        if (value.isEmpty()) {
            return "";
        }
        int lt = value.indexOf('<');
        int gt = value.indexOf('>', lt + 1);
        if (lt >= 0 && gt > lt) {
            return value.substring(lt + 1, gt).trim();
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").matcher(value);
        if (m.find()) {
            return m.group().trim();
        }
        return value.replace("\"", "").trim();
    }

    private static String getToFromGmail(String plain_text){
        return getToCommon(plain_text);
    }
      private static String getToFromUAGRM(String plain_text){
        return getToCommon(plain_text);
    }
          private static String getToFromUASB(String plain_text){
        return getToCommon(plain_text);
    }
    private static String getToFromHotmail(String plain_text){
        String aux = getToCommon(plain_text);
        return aux.substring(1, aux.length() - 1);
    }
    
    private static String getToFromYahoo(String plain_text){
        int index = plain_text.indexOf("To: ");
        int i = plain_text.indexOf("<", index);
        int e = plain_text.indexOf(">", i);
        return plain_text.substring(i + 1, e);
    }
    
    private static String getToCommon(String plain_text){
        String aux = "To: ";
        int index_begin = plain_text.indexOf(aux) + aux.length();
        int index_end = plain_text.indexOf("\n", index_begin);
        return plain_text.substring(index_begin, index_end);
    }
}
