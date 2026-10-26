
package Utils;

import Utils.Cinta;
import Utils.TSParams;
import Utils.Token;


public class Analex {
    public static final char EOF = 0;
    public static final char SPACE = 32;
    public static final char TAB = 9;
    public static final char NL = 10;
    
    private Cinta cinta;
    private String ac;
    private Token R;
    private TSParams tsp;// Necesitamos las demas tablas de memorias
    private int lastTokenType = -1; // Track last token type for syntax flow
    private boolean afterAction = false; // Flag to enter parameter mode after ACTION
    
    public Analex(String cinta){
        this.cinta = new Cinta(cinta);
        tsp = new TSParams();
        init();
    }
    
    public void init(){
        cinta.init();
        tsp.init();// contruyo de nuevo las tablas
        lastTokenType = -1; // Reset token type tracker
        dt();        
    }
    
    /**
     * Devuelve el token analizado.
     * @return 
     */
    public Token Preanalisis(){
        return R;
    }
    
    /**
     * Realiza el siguiente analisis.
     */
    public void next(){
        dt();
    }
    
    /**
     * Muestra los parametros.
     */
    public void showParams(){
        System.out.println(tsp);
    }
    
    /**
     * Devuelve el parametro en la posicion pos.
     * @param pos
     * @return 
     */
    public String getParam(int pos){
        return tsp.getStr(pos);
    }
    
    /**
     * Devuelve el lexema.
     * @return 
     */
    public String lexeme(){
        return ac;
    }
    
    private boolean isSpace(char c){
        return (c == SPACE || c == TAB || c == NL || c == '\r');
    }

    private boolean isDigit(char c){
        return ('0' <= c && c <= '9');
    }

    private boolean isLetter(char c){
        return Character.isLetter(c) || c == '_';
    }
    
    private boolean isBeginParams(char c){
        return c == ',';
    }
    
    private boolean isEndParams(char c){
        return c == ',';
    }
    
    private boolean isComa(char c){
        return c == ',';
    }
    
    private boolean isDate(char c){        
        //return c == '-' || c == '/' || c == ':';
        return c == '-'  || c == ':';
    }
    
    private boolean isEmail(char c){        
        return c == 64 || c == '.' || c == '_' || c == '-';
    }

    /**
     * Realiza el analisis del comando.
     */
    private void dt(){
        int status = 0;
        int index = -1;
        while(true){
            char c = cinta.CC();
            switch(status){
                case 0:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 0;
                    }else if(isLetter(c) || isDigit(c) || ((afterAction || tsp.length() > 0) && (c == '-' || c == '+' || c == '.' || c == '/' || c == '"' || c == '\'' || c == '('))){
                        // Check if we're after ACTION or already collecting parameters
                        if(afterAction || tsp.length() > 0) {
                            // After ACTION, or already have params, enter parameter mode
                            afterAction = false; // Reset flag
                            ac = "";
                            status = 33; // Parameters can start with letter or digit
                        } else {
                            // We're still parsing keywords (CU, ACTION) - must be letter
                            if(isLetter(c)) {
                                ac = c + "";
                                cinta.forward();
                                status = 1;
                            } else {
                                // Digit at start without action = error
                                ac = "" + c;
                                status = 7;
                            }
                        }
                    }else if(isBeginParams(c) || isComa(c)){                       
                        ac = "";
                        cinta.forward();
                        status = 33;
                    }else if(c == EOF || isEndParams(c)){
                        ac = "";
                        status = 6;
                    }else{
                        ac = "" + c;
                        status = 7;
                    }
                    break;

                case 1:
                    if(isLetter(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 1;
                    }else if(isSpace(c) || isBeginParams(c) || c == EOF){
                        status = 2;
                    }else{
                        ac = "" + c;
                        status = 7;
                    }
                    break;

                case 2:
                    R = new Token(ac, lastTokenType);
                    lastTokenType = R.getName(); // Track token type for syntax flow
                    // If this is an ACTION token, next letters should be parameters
                    if(R.getName() == 1) { // Token.ACTION = 1
                        afterAction = true;
                    }
                    return;
                    
                case 33:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 33;
                    }else if(isLetter(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isDigit(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 34;
                    }else if(c == '-' || c == '+' || c == '.' || c == '/' || c == '"' || c == '\'' || c == '('){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isEndParams(c)){
                        status = 5;
                    }else{
                        ac = "" + c;
                        status = 8;
                    }
                    break;
                    
                case 34:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 34;
                    }else if(isDigit(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 34;
                    }else if(isEndParams(c)){
                        status = 5;
                    }else if(isComa(c)){
                        status = 4;
                    }else if(isLetter(c)){
                        status = 3;
                    }else {
                        status = 3;
                    }
                    break;

                case 3:
                    if(isLetter(c) || isDigit(c) || isDate(c) || isEmail(c) || c == '/' || c == '+' || c == '(' || c == ')' || c == '"' || c == '\''){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(c == EOF){
                        status = 5;
                    }else if(isEndParams(c)){
                        status = 5;
                    } else if(c == SPACE){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isSpace(c)){
                        cinta.forward();
                        status = 3;
                    }else if(isComa(c)){
                        status = 4;
                    } else {
                        ac = "" + c;
                        status = 8;
                    }
                    break;

                case 4:
                    index = tsp.add(ac);
                    R = new Token(Token.PARAMS, index);
                    cinta.forward(); // Consume el separador ';'
                    return;

                case 5:
                    index = tsp.add(ac);
                    R = new Token(Token.PARAMS, index);
                    cinta.forward(); // Consume el delimitador ']' o '}'
                    return;

                case 6:
                    R = new Token(Token.END);
                    return;

                case 7:
                    R = new Token(Token.ERROR, Token.ERROR_COMMAND);
                    return;
                case 8:
                    R = new Token(Token.ERROR, Token.ERROR_CHARACTER);
                    return;
            }
        }
    }
}
