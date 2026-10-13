package Utils;

import Interfaces.ITokenCasoUsoEventListener;
import Comunication.TokenEvent;

public class Interpreter implements Runnable {

    private ITokenCasoUsoEventListener listener;
    private Analex analex;

    private String command;
    private String sender;

    public Interpreter(String command, String sender) {
        this.command = command;
        this.sender = sender;
    }

    public ITokenCasoUsoEventListener getListener() {
        return listener;
    }

    public void setListener(ITokenCasoUsoEventListener listener) {
        this.listener = listener;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private void filterEvent(TokenCommand tokenCommand) {
        TokenEvent tokenEvent = new TokenEvent(this, sender);
        tokenEvent.setAction(tokenCommand.getAction());

        int countParams = tokenCommand.countParams();

        for (int i = 0; i < countParams; i++) {
            int pos = tokenCommand.getParams(i);
            tokenEvent.addParams(analex.getParam(pos));
        }

        switch (tokenCommand.getName()) {
            case Token.USUARIO:
                listener.usuario(tokenEvent);
                break;
            case Token.ROL:
                listener.rol(tokenEvent);
                break;
            case Token.CATEGORIA:
                listener.categoria(tokenEvent);
                break;
            case Token.PROGRAMA:
                listener.programa(tokenEvent);
                break;
            case Token.PARRILLA:
                listener.parrilla(tokenEvent);
                break;
            case Token.ITEM_PARRILLA:
                listener.itemParrilla(tokenEvent);
                break;
            case Token.MATERIAL_VIDEO:
                listener.materialVideo(tokenEvent);
                break;
            case Token.VALIDACION:
                listener.validacion(tokenEvent);
                break;
            case Token.CLIENTE:
                listener.cliente(tokenEvent);
                break;
            case Token.SPOT:
                listener.spot(tokenEvent);
                break;
            case Token.PAUTA:
                listener.pauta(tokenEvent);
                break;
            case Token.EMISION_REAL:
                listener.emisionReal(tokenEvent);
                break;
            case Token.AUDITORIA:
                listener.auditoria(tokenEvent);
                break;
            case Token.REPORTE:
                listener.reporte(tokenEvent);
                break;
            default:
                listener.error(tokenEvent);
                break;
        }
    }

    private void tokenError(Token token, String lexeme) {
        TokenEvent tokenEvent = new TokenEvent(this, sender);
        tokenEvent.setAction(token.getAttribute());
        tokenEvent.addParams(lexeme);
        listener.error(tokenEvent);
    }

    @Override
    public void run() {
        analex = new Analex(stripAttachmentMarkers(command));
        TokenCommand tokenCommand = new TokenCommand();
        Token token;

        while ((token = analex.Preanalisis()).getName() != Token.END && token.getName() != Token.ERROR) {
            if (token.getName() == Token.CU) {
                tokenCommand.setName(token.getAttribute());
            } else if (token.getName() == Token.ACTION) {
                tokenCommand.setAction(token.getAttribute());
            } else if (token.getName() == Token.PARAMS) {
                tokenCommand.addParams(token.getAttribute());
            }
            analex.next();
        }

        if (token.getName() == Token.END) {
            filterEvent(tokenCommand);
        } else if (token.getName() == Token.ERROR) {
            tokenError(token, analex.lexeme());
        }
    }

    private String stripAttachmentMarkers(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        return input.replaceAll("\\[[^\\]]*\\]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}