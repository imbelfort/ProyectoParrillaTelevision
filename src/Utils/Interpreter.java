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
                tokenEvent.setErrorCode(ErrorCode.ERR_CMD_002);
                tokenEvent.addParams("Estructura de comando no reconocida.");
                tokenEvent.setSuggestion("help (para ver manual completo)");
                listener.error(tokenEvent);
                break;
        }
    }

    private void tokenError(Token token, String lexeme) {
        TokenEvent tokenEvent = new TokenEvent(this, sender);
        tokenEvent.setAction(token.getAttribute());
        tokenEvent.addParams(lexeme);
        if (token.getAttribute() == Token.ERROR_CHARACTER || (lexeme != null && lexeme.matches(".*[^a-zA-Z0-9_,;\\s\\.@-].*"))) {
            tokenEvent.setErrorCode(ErrorCode.ERR_CMD_004);
            tokenEvent.setSuggestion("Utilice solo caracteres alfanuméricos, comas o guiones. Ejemplo: usuario listar");
        } else {
            tokenEvent.setErrorCode(ErrorCode.ERR_CMD_002);
            tokenEvent.setSuggestion("help (para ver el manual completo de comandos)");
        }
        listener.error(tokenEvent);
    }

    @Override
    public void run() {
        String cleanCommand = stripAttachmentMarkers(command);

        // 0. Detección temprana de caracteres de control o no imprimibles peligrosos
        for (int i = 0; i < cleanCommand.length(); i++) {
            char ch = cleanCommand.charAt(i);
            if ((ch < 32 && ch != '\t' && ch != '\n' && ch != '\r') || ch == 127) {
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_CMD_004);
                errorEvent.setAction(Token.ERROR_COMMAND);
                errorEvent.addParams("El comando contiene un carácter de control no permitido (ASCII: " + (int) ch + ").");
                errorEvent.setSuggestion("Utilice solo caracteres alfanuméricos y texto estándar.");
                listener.error(errorEvent);
                return;
            }
        }

        // 1. Comando vacío
        if (cleanCommand.isEmpty()) {
            TokenEvent errorEvent = new TokenEvent(this, sender);
            errorEvent.setErrorCode(ErrorCode.ERR_CMD_001);
            errorEvent.setAction(Token.ERROR_COMMAND);
            errorEvent.addParams("El asunto (Subject) del correo electrónico está vacío. Por favor ingrese el comando en el asunto.");
            errorEvent.setSuggestion("help (para ver el manual completo de comandos de Playout)");
            listener.error(errorEvent);
            return;
        }

        // Análisis sintáctico estructural previo (detección de orden y completitud)
        String[] words = cleanCommand.split("\\s+");
        String firstWord = words[0].replaceAll("[,;\\]\\[]", "").trim();
        String secondWord = words.length > 1 ? words[1].replaceAll("[,;\\]\\[]", "").trim() : null;

        // Caso comando help / ayuda global
        if ("help".equalsIgnoreCase(firstWord) || "ayuda".equalsIgnoreCase(firstWord)) {
            // Se procesará en Home directamente o pasa
        }
        // 2. Comprobar si la primera palabra es una ENTIDAD conocida (CU1 a CU8)
        if (Token.isCU(firstWord)) {
            // Caso de Orden Incompleta: Solo se escribió la entidad sin acción (ERR-INC-001)
            if (words.length == 1) {
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_INC_001);
                errorEvent.setAction(Token.ERROR_COMMAND);
                errorEvent.addParams("Orden incompleta: Ingresó la entidad '" + firstWord + "' pero no indicó la acción a realizar.");
                errorEvent.setSuggestion(sugerirAccionesParaEntidad(firstWord));
                listener.error(errorEvent);
                return;
            }

            // Si hay segunda palabra pero no es una acción válida
            if (secondWord != null && !Token.isAction(secondWord)) {
                // Caso: ID colocado antes de la acción (ej: 'usuario 1 consultar' -> ERR-ORD-003)
                if (secondWord.matches("^\\d+$") && words.length > 2 && Token.isAction(words[2])) {
                    TokenEvent errorEvent = new TokenEvent(this, sender);
                    errorEvent.setErrorCode(ErrorCode.ERR_ORD_003);
                    errorEvent.setAction(Token.ERROR_COMMAND);
                    errorEvent.addParams("Parámetros mal ordenados: Se colocó el ID '" + secondWord + "' antes de la acción '" + words[2] + "'.");
                    errorEvent.setSuggestion(firstWord + " " + words[2] + " " + secondWord);
                    listener.error(errorEvent);
                    return;
                }

                // Acción no reconocida para la entidad (ERR-CMD-003)
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_CMD_003);
                errorEvent.setAction(Token.ERROR_COMMAND);
                String sugerida = Token.sugerirAccionCercana(secondWord);
                String msg = "Acción '" + secondWord + "' no reconocida para la entidad '" + firstWord + "'.";
                if (sugerida != null) {
                    msg += " ¿Quizás quiso decir '" + sugerida + "'?";
                }
                errorEvent.addParams(msg);
                errorEvent.setSuggestion(sugerirAccionesParaEntidad(firstWord));
                listener.error(errorEvent);
                return;
            }
        }
        // 3. Comprobar si empezó con un parámetro numérico / ID (ERR-ORD-002)
        else if (firstWord.matches("^\\d+$")) {
            TokenEvent errorEvent = new TokenEvent(this, sender);
            errorEvent.setErrorCode(ErrorCode.ERR_ORD_002);
            errorEvent.setAction(Token.ERROR_COMMAND);
            String entidad = (secondWord != null && Token.isCU(secondWord)) ? secondWord : "entidad";
            String accion = words.length > 2 ? words[2] : "consultar";
            errorEvent.addParams("Se detectó un parámetro numérico ('" + firstWord + "') antes del nombre de la entidad o acción.");
            errorEvent.setSuggestion(entidad + " " + accion + " " + firstWord);
            listener.error(errorEvent);
            return;
        }
        // 4. Comprobar si empezó con una ACCIÓN antes de una ENTIDAD (ERR-ORD-001)
        else if (Token.isAction(firstWord)) {
            if (secondWord != null && Token.isCU(secondWord)) {
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_ORD_001);
                errorEvent.setAction(Token.ERROR_COMMAND);
                StringBuilder sugerencia = new StringBuilder();
                sugerencia.append(secondWord).append(" ").append(firstWord);
                for (int i = 2; i < words.length; i++) {
                    sugerencia.append(" ").append(words[i]);
                }
                errorEvent.addParams("Orden sintáctico invertido: Se escribió la acción '" + firstWord + "' antes de la entidad '" + secondWord + "'.");
                errorEvent.setSuggestion(sugerencia.toString().trim());
                listener.error(errorEvent);
                return;
            } else if (secondWord == null) {
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_INC_001);
                errorEvent.setAction(Token.ERROR_COMMAND);
                errorEvent.addParams("Orden incompleta: Se especificó la acción '" + firstWord + "' pero falta indicar sobre qué entidad aplicarla.");
                errorEvent.setSuggestion("usuario " + firstWord + "  (o consulte 'help' para ver entidades disponibles)");
                listener.error(errorEvent);
                return;
            } else {
                TokenEvent errorEvent = new TokenEvent(this, sender);
                errorEvent.setErrorCode(ErrorCode.ERR_ORD_001);
                errorEvent.setAction(Token.ERROR_COMMAND);
                errorEvent.addParams("Orden sintáctico incorrecto: Se inició con la acción '" + firstWord + "' pero '" + secondWord + "' no es una entidad reconocida.");
                errorEvent.setSuggestion("usuario " + firstWord);
                listener.error(errorEvent);
                return;
            }
        }
        // 5. La primera palabra NO es ni CU ni Action conocida (ERR-CMD-002)
        else {
            TokenEvent errorEvent = new TokenEvent(this, sender);
            errorEvent.setErrorCode(ErrorCode.ERR_CMD_002);
            errorEvent.setAction(Token.ERROR_COMMAND);
            String sugerida = Token.sugerirEntidadCercana(firstWord);
            String msg = "Entidad o comando '" + firstWord + "' no reconocido.";
            if (sugerida != null) {
                msg += " ¿Quizás quiso decir '" + sugerida + "'?";
            }
            errorEvent.addParams(msg);
            errorEvent.setSuggestion(sugerida != null ? (sugerida + " listar") : "help (para ver todas las entidades y comandos)");
            listener.error(errorEvent);
            return;
        }

        // Ejecutar analizador léxico estándar
        analex = new Analex(cleanCommand);
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

    private String sugerirAccionesParaEntidad(String entidad) {
        String e = entidad.toLowerCase().trim();
        if (e.contains("usu")) {
            return "Acciones para usuario:\n- usuario listar\n- usuario consultar id\n- usuario agregar Nombre,Email,Password,Rol\n- usuario eliminar id";
        }
        if (e.contains("rol")) {
            return "Acciones para rol:\n- rol listar\n- rol agregar Nombre,Descripcion\n- rol asignar id_usuario,id_rol";
        }
        if (e.contains("cat")) {
            return "Acciones para categoria:\n- categoria listar\n- categoria agregar Nombre,Descripcion";
        }
        if (e.contains("prog")) {
            return "Acciones para programa:\n- programa listar\n- programa consultar id\n- programa agregar codigo,titulo,duracion_min\n- programa eliminar id";
        }
        if (e.contains("parr")) {
            return "Acciones para parrilla:\n- parrilla listar\n- parrilla hoy\n- parrilla al_aire\n- parrilla crear YYYY-MM-DD\n- parrilla estado id,APROBADA\n- parrilla eliminar id";
        }
        if (e.contains("item") || e.contains("bloq")) {
            return "Acciones para item_parrilla:\n- item listar [id_parrilla]\n- item al_aire\n- item hoy\n- item consultar id\n- item agregar id_parrilla,id_programa,hora_ini,hora_fin\n- item eliminar id";
        }
        if (e.contains("mat")) {
            return "Acciones para material_video:\n- material listar\n- material consultar id\n- material listar_programa id_prog\n- material agregar id_prog,codigo,titulo,duracion_seg\n- material estado id,LISTO\n- material eliminar id";
        }
        if (e.contains("val") || e.contains("qc")) {
            return "Acciones para validacion (QC):\n- validacion pendientes\n- validacion listar\n- validacion consultar id_item\n- validacion aprobar id_item\n- validacion rechazar id_item,motivo";
        }
        if (e.contains("cli")) {
            return "Acciones para cliente:\n- cliente listar\n- cliente consultar id\n- cliente agregar RazonSocial,NIT,Contacto,Email,Telefono";
        }
        if (e.contains("spot")) {
            return "Acciones para spot:\n- spot listar\n- spot consultar id\n- spot agregar id_cliente,codigo,titulo,duracion_seg";
        }
        if (e.contains("paut")) {
            return "Acciones para pauta:\n- pauta listar\n- pauta listar id_item\n- pauta consultar id\n- pauta agregar id_item,id_spot,tanda,orden\n- pauta estado id,EMITIDO\n- pauta eliminar id";
        }
        if (e.contains("emi")) {
            return "Acciones para emision_real:\n- emision al_aire\n- emision listar\n- emision discrepancias\n- emision conciliar id_item\n- emision consultar id_item";
        }
        if (e.contains("aud")) {
            return "Acciones para auditoria:\n- auditoria listar\n- auditoria consultar id_usuario";
        }
        if (e.contains("rep")) {
            return "Acciones para reporte:\n- reporte general\n- reporte parrilla\n- reporte programas\n- reporte pautas\n- reporte cumplimiento";
        }
        return "Consulte 'help' para ver el manual completo de comandos.";
    }

    private String stripAttachmentMarkers(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Remueve marcadores explícitos de adjuntos (ej: [ATTACHMENT], [ADJUNTO], [FOTO])
        String s = input.replaceAll("(?i)\\[(attachment|adjunto|foto|file|archivo)\\]", " ");
        // Si el usuario ingresó parámetros entre corchetes o paréntesis [p1, p2], se eliminan solo los delimitadores
        s = s.replaceAll("[\\[\\]]", " ");
        return s.replaceAll("\\s+", " ").trim();
    }
}