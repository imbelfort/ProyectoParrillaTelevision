/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.ArrayList;
import java.util.List;


public class Email {
    
    
    public static final String SUBJECT = "Request respose";
    private String from;
    private String to;
    private String subject;
    private String message;
    private String attachmentFileName;
    private String attachmentContentBase64;
    private String attachmentContentType;
    private List<Attachment> attachments = new ArrayList<>();
    
    public Email(){
    }

    public Email(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public Email(String from, String subject) {
        this.from = from;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentContentBase64() {
        return attachmentContentBase64;
    }

    public void setAttachmentContentBase64(String attachmentContentBase64) {
        this.attachmentContentBase64 = attachmentContentBase64;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments != null ? new ArrayList<>(attachments) : new ArrayList<>();
        syncPrimaryAttachment();
    }

    public Attachment getAttachment(int index) {
        if (attachments == null || index < 0 || index >= attachments.size()) {
            return null;
        }
        return attachments.get(index);
    }

    public void addAttachment(Attachment attachment) {
        if (attachment == null) {
            return;
        }
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        syncPrimaryAttachment();
    }

    private void syncPrimaryAttachment() {
        if (attachments != null && !attachments.isEmpty()) {
            Attachment first = attachments.get(0);
            this.attachmentFileName = first.getFileName();
            this.attachmentContentBase64 = first.getContentBase64();
            this.attachmentContentType = first.getContentType();
        }
    }

    public static class Attachment {
        private String fileName;
        private String contentBase64;
        private String contentType;

        public Attachment() {
        }

        public Attachment(String fileName, String contentBase64, String contentType) {
            this.fileName = fileName;
            this.contentBase64 = contentBase64;
            this.contentType = contentType;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getContentBase64() {
            return contentBase64;
        }

        public void setContentBase64(String contentBase64) {
            this.contentBase64 = contentBase64;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    @Override
    public String toString() {
        return "[From: " + from + ", To: " + to + ", Subject: " + subject + ", Message: " + message + ", Attachments: " + (attachments != null ? attachments.size() : 0) + "]";
    }
}
