package Utils;

/**
 * Excepción personalizada para errores explícitos de comandos de playout.
 * Transporta el código de error oficial (ERR-xxx-yyy), el mensaje detallado
 * y la sugerencia de corrección correspondiente.
 */
public class CommandException extends RuntimeException {

    private final String errorCode;
    private final String suggestion;

    public CommandException(String errorCode, String message, String suggestion) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : ErrorCode.ERR_SYS_001;
        this.suggestion = suggestion != null ? suggestion : "";
    }

    public CommandException(String errorCode, String message) {
        this(errorCode, message, "");
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
