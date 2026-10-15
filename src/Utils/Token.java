package Utils;

public class Token {

    private int name;
    private int attribute;

    public static final int CU = 0;
    public static final int ACTION = 1;
    public static final int PARAMS = 2;
    public static final int END = 3;
    public static final int ERROR = 4;

    // Entidades / Casos de Uso (100 - 199)
    public static final int USUARIO = 100;
    public static final int ROL = 101;
    public static final int CATEGORIA = 102;
    public static final int PROGRAMA = 103;
    public static final int PARRILLA = 104;
    public static final int ITEM_PARRILLA = 105;
    public static final int MATERIAL_VIDEO = 106;
    public static final int VALIDACION = 107;
    public static final int CLIENTE = 108;
    public static final int SPOT = 109;
    public static final int PAUTA = 110;
    public static final int EMISION_REAL = 111;
    public static final int AUDITORIA = 112;
    public static final int REPORTE = 113;

    // Acciones (200 - 299)
    public static final int ELIMINAR = 200;
    public static final int MODIFICAR = 201;
    public static final int LISTAR = 202;
    public static final int AGREGAR = 203;
    public static final int MOSTRAR = 204;
    public static final int GENERAR = 205;
    public static final int HELP = 206;
    public static final int CONSULTAR = 207;
    public static final int REGISTRAR = 208;
    public static final int ASIGNAR = 209;
    public static final int CAMBIAR_ESTADO = 210;
    public static final int PENDIENTES = 211;
    public static final int INSERTAR = 212;
    public static final int DISCREPANCIAS = 213;
    public static final int GENERAL = 214;
    public static final int PARRILLA_ACT = 215;
    public static final int PROGRAMAS = 216;
    public static final int PAUTAS = 217;
    public static final int CUMPLIMIENTO = 218;
    public static final int LISTAR_PROGRAMA = 219;
    public static final int LISTAR_ITEM = 220;
    public static final int APROBAR = 221;
    public static final int RECHAZAR = 222;
    public static final int CONCILIAR = 223;
    public static final int AL_AIRE = 224;
    public static final int HOY = 225;

    public static final int ERROR_COMMAND = 300;
    public static final int ERROR_CHARACTER = 301;

    public static final String LEXEME_CU = "caso de uso";
    public static final String LEXEME_ACTION = "action";
    public static final String LEXEME_PARAMS = "params";
    public static final String LEXEME_END = "end";
    public static final String LEXEME_ERROR = "error";

    // Lexemas Entidades
    public static final String LEXEME_USUARIO = "usuario";
    public static final String LEXEME_ROL = "rol";
    public static final String LEXEME_CATEGORIA = "categoria";
    public static final String LEXEME_CATEGORIA_ALT = "categoria_programa";
    public static final String LEXEME_PROGRAMA = "programa";
    public static final String LEXEME_PARRILLA = "parrilla";
    public static final String LEXEME_ITEM_PARRILLA = "item_parrilla";
    public static final String LEXEME_ITEM_ALT = "item";
    public static final String LEXEME_BLOQUE = "bloque";
    public static final String LEXEME_MATERIAL_VIDEO = "material_video";
    public static final String LEXEME_MATERIAL_ALT = "material";
    public static final String LEXEME_VALIDACION = "validacion";
    public static final String LEXEME_VALIDACION_ALT = "qc";
    public static final String LEXEME_CLIENTE = "cliente";
    public static final String LEXEME_CLIENTE_ALT = "cliente_publicitario";
    public static final String LEXEME_SPOT = "spot";
    public static final String LEXEME_SPOT_ALT = "spot_publicitario";
    public static final String LEXEME_PAUTA = "pauta";
    public static final String LEXEME_PAUTA_ALT = "pauta_comercial";
    public static final String LEXEME_EMISION_REAL = "emision_real";
    public static final String LEXEME_EMISION_ALT = "emision";
    public static final String LEXEME_AUDITORIA = "auditoria";
    public static final String LEXEME_REPORTE = "reporte";

    // Lexemas Acciones
    public static final String LEXEME_ELIMINAR = "eliminar";
    public static final String LEXEME_BORRAR = "borrar";
    public static final String LEXEME_MODIFICAR = "modificar";
    public static final String LEXEME_LISTAR = "listar";
    public static final String LEXEME_AGREGAR = "agregar";
    public static final String LEXEME_CREAR = "crear";
    public static final String LEXEME_MOSTRAR = "mostrar";
    public static final String LEXEME_VER = "ver";
    public static final String LEXEME_GENERAR = "generar";
    public static final String LEXEME_HELP = "help";
    public static final String LEXEME_AYUDA = "ayuda";
    public static final String LEXEME_CONSULTAR = "consultar";
    public static final String LEXEME_REGISTRAR = "registrar";
    public static final String LEXEME_ASIGNAR = "asignar";
    public static final String LEXEME_CAMBIAR_ESTADO = "cambiar_estado";
    public static final String LEXEME_ACTUALIZAR_ESTADO = "actualizar_estado";
    public static final String LEXEME_ESTADO = "estado";
    public static final String LEXEME_PENDIENTES = "pendientes";
    public static final String LEXEME_INSERTAR = "insertar";
    public static final String LEXEME_DISCREPANCIAS = "discrepancias";
    public static final String LEXEME_GENERAL = "general";
    public static final String LEXEME_PARRILLA_ACT = "parrilla";
    public static final String LEXEME_PROGRAMAS = "programas";
    public static final String LEXEME_PAUTAS = "pautas";
    public static final String LEXEME_CUMPLIMIENTO = "cumplimiento";
    public static final String LEXEME_LISTAR_PROGRAMA = "listar_programa";
    public static final String LEXEME_LISTAR_ITEM = "listar_item";
    public static final String LEXEME_APROBAR = "aprobar";
    public static final String LEXEME_RECHAZAR = "rechazar";
    public static final String LEXEME_CONCILIAR = "conciliar";
    public static final String LEXEME_AL_AIRE = "al_aire";
    public static final String LEXEME_HOY = "hoy";

    public static final String LEXEME_ERROR_COMMAND = "UNKNOWN COMMAND";
    public static final String LEXEME_ERROR_CHARACTER = "UNKNOWN CHARACTER";

    public Token() {
    }

    public Token(String token) {
        this(token, -1);
    }

    public Token(String token, int lastTokenType) {
        int id = findByLexeme(token, lastTokenType);

        if (id != -1) {
            if (100 <= id && id < 200) {
                this.name = CU;
                this.attribute = id;
            } else if (200 <= id && id < 300) {
                this.name = ACTION;
                this.attribute = id;
            } else {
                this.name = ERROR;
                this.attribute = ERROR_COMMAND;
            }
        } else {
            this.name = ERROR;
            this.attribute = ERROR_COMMAND;
        }
    }

    public Token(int name) {
        this.name = name;
    }

    public Token(int name, int attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        if (0 <= name && name <= 1) {
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        } else if (name == 2) {
            return "< " + getStringToken(name) + " , " + attribute + ">";
        } else if (name == 3) {
            return "< " + getStringToken(name) + " , _______ >";
        } else if (name == 4) {
            return "< " + getStringToken(name) + " , " + getStringToken(attribute) + ">";
        }
        return "< TOKEN , DESCONOCIDO>";
    }

    public String getStringToken(int token) {
        switch (token) {
            case CU: return LEXEME_CU;
            case ACTION: return LEXEME_ACTION;
            case PARAMS: return LEXEME_PARAMS;
            case END: return LEXEME_END;
            case ERROR: return LEXEME_ERROR;

            case USUARIO: return LEXEME_USUARIO;
            case ROL: return LEXEME_ROL;
            case CATEGORIA: return LEXEME_CATEGORIA;
            case PROGRAMA: return LEXEME_PROGRAMA;
            case PARRILLA: return LEXEME_PARRILLA;
            case ITEM_PARRILLA: return LEXEME_ITEM_PARRILLA;
            case MATERIAL_VIDEO: return LEXEME_MATERIAL_VIDEO;
            case VALIDACION: return LEXEME_VALIDACION;
            case CLIENTE: return LEXEME_CLIENTE;
            case SPOT: return LEXEME_SPOT;
            case PAUTA: return LEXEME_PAUTA;
            case EMISION_REAL: return LEXEME_EMISION_REAL;
            case AUDITORIA: return LEXEME_AUDITORIA;
            case REPORTE: return LEXEME_REPORTE;

            case ELIMINAR: return LEXEME_ELIMINAR;
            case MODIFICAR: return LEXEME_MODIFICAR;
            case LISTAR: return LEXEME_LISTAR;
            case AGREGAR: return LEXEME_AGREGAR;
            case MOSTRAR: return LEXEME_MOSTRAR;
            case GENERAR: return LEXEME_GENERAR;
            case CONSULTAR: return LEXEME_CONSULTAR;
            case HELP: return LEXEME_HELP;
            case REGISTRAR: return LEXEME_REGISTRAR;
            case ASIGNAR: return LEXEME_ASIGNAR;
            case CAMBIAR_ESTADO: return LEXEME_CAMBIAR_ESTADO;
            case PENDIENTES: return LEXEME_PENDIENTES;
            case INSERTAR: return LEXEME_INSERTAR;
            case DISCREPANCIAS: return LEXEME_DISCREPANCIAS;
            case GENERAL: return LEXEME_GENERAL;
            case PARRILLA_ACT: return LEXEME_PARRILLA_ACT;
            case PROGRAMAS: return LEXEME_PROGRAMAS;
            case PAUTAS: return LEXEME_PAUTAS;
            case CUMPLIMIENTO: return LEXEME_CUMPLIMIENTO;
            case LISTAR_PROGRAMA: return LEXEME_LISTAR_PROGRAMA;
            case LISTAR_ITEM: return LEXEME_LISTAR_ITEM;
            case AL_AIRE: return LEXEME_AL_AIRE;
            case HOY: return LEXEME_HOY;

            case ERROR_COMMAND: return LEXEME_ERROR_COMMAND;
            case ERROR_CHARACTER: return LEXEME_ERROR_CHARACTER;
            default: return "N: " + token;
        }
    }

    private int findByLexeme(String lexeme) {
        return findByLexeme(lexeme, -1);
    }

    private int findByLexeme(String lexeme, int lastTokenType) {
        if (lexeme == null) {
            return -1;
        }

        String normalized = java.text.Normalizer.normalize(lexeme.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        if ("parrilla".equals(normalized) || "parrillas".equals(normalized)) {
            if (lastTokenType == CU) {
                return PARRILLA_ACT;
            }
            return PARRILLA;
        }

        if ("programas".equals(normalized) || "programa".equals(normalized)) {
            if (lastTokenType == CU) {
                return PROGRAMAS;
            }
            return PROGRAMA;
        }

        if ("pautas".equals(normalized) || "pauta".equals(normalized)) {
            if (lastTokenType == CU) {
                return PAUTAS;
            }
            return PAUTA;
        }

        switch (normalized) {
            case LEXEME_CU: return CU;
            case LEXEME_ACTION: return ACTION;
            case LEXEME_PARAMS: return PARAMS;
            case LEXEME_END: return END;
            case LEXEME_ERROR: return ERROR;

            case LEXEME_USUARIO:
            case "usuarios":
            case "user":
            case "users":
                return USUARIO;
            case LEXEME_ROL:
            case "roles":
                return ROL;
            case LEXEME_CATEGORIA:
            case LEXEME_CATEGORIA_ALT:
            case "categorias":
            case "genero":
            case "generos":
                return CATEGORIA;
            case "catalogo":
                return PROGRAMA;
            case LEXEME_ITEM_PARRILLA:
            case LEXEME_ITEM_ALT:
            case "items":
            case "bloque":
            case "bloques":
            case "playlist":
                return ITEM_PARRILLA;
            case LEXEME_MATERIAL_VIDEO:
            case LEXEME_MATERIAL_ALT:
            case "materiales":
            case "video":
            case "videos":
            case "clip":
            case "clips":
            case "mam":
                return MATERIAL_VIDEO;
            case LEXEME_VALIDACION:
            case LEXEME_VALIDACION_ALT:
            case "validaciones":
            case "calidad":
            case "control":
                return VALIDACION;
            case LEXEME_CLIENTE:
            case LEXEME_CLIENTE_ALT:
            case "clientes":
            case "anunciante":
            case "anunciantes":
                return CLIENTE;
            case LEXEME_SPOT:
            case LEXEME_SPOT_ALT:
            case "spots":
            case "comercial":
            case "comerciales":
            case "publicidad":
                return SPOT;
            case "corte":
            case "cortes":
            case "tanda":
            case "tandas":
                return PAUTA;
            case LEXEME_EMISION_REAL:
            case LEXEME_EMISION_ALT:
            case "emisiones":
            case "asrun":
            case "as_run":
            case "bitacora":
                return EMISION_REAL;
            case LEXEME_AUDITORIA:
            case "auditorias":
            case "log":
            case "logs":
                return AUDITORIA;
            case LEXEME_REPORTE:
            case "reportes":
            case "estadisticas":
            case "metricas":
                return REPORTE;

            case LEXEME_ELIMINAR:
            case LEXEME_BORRAR:
            case "quitar":
            case "remover":
            case "delete":
                return ELIMINAR;
            case LEXEME_MODIFICAR:
            case "actualizar":
            case "editar":
            case "cambiar":
            case "update":
                return MODIFICAR;
            case LEXEME_LISTAR:
            case "ver":
            case "mostrar":
            case "todos":
            case "list":
            case "get":
                return LISTAR;
            case LEXEME_AGREGAR:
            case LEXEME_CREAR:
            case "insertar":
            case "nuevo":
            case "add":
                return AGREGAR;
            case LEXEME_GENERAR: return GENERAR;
            case LEXEME_CONSULTAR:
            case "buscar":
            case "obtener":
            case "detalle":
            case "info":
                return CONSULTAR;
            case LEXEME_HELP:
            case LEXEME_AYUDA:
            case "manual":
            case "comandos":
                return HELP;
            case LEXEME_REGISTRAR: return REGISTRAR;
            case LEXEME_ASIGNAR: return ASIGNAR;
            case LEXEME_CAMBIAR_ESTADO:
            case LEXEME_ACTUALIZAR_ESTADO:
            case LEXEME_ESTADO:
                return CAMBIAR_ESTADO;
            case LEXEME_PENDIENTES:
            case "por_aprobar":
                return PENDIENTES;
            case LEXEME_DISCREPANCIAS:
            case "desfases":
            case "diferencias":
                return DISCREPANCIAS;
            case LEXEME_GENERAL: return GENERAL;
            case LEXEME_CUMPLIMIENTO: return CUMPLIMIENTO;
            case LEXEME_LISTAR_PROGRAMA: return LISTAR_PROGRAMA;
            case LEXEME_LISTAR_ITEM: return LISTAR_ITEM;
            case LEXEME_APROBAR:
            case "aprobado":
                return APROBAR;
            case LEXEME_RECHAZAR:
            case "rechazado":
                return RECHAZAR;
            case LEXEME_CONCILIAR:
            case "conciliado":
                return CONCILIAR;
            case LEXEME_AL_AIRE:
            case "alaire":
            case "actual":
            case "aire":
            case "envivo":
            case "en_vivo":
            case "vivo":
            case "ahora":
                return AL_AIRE;
            case LEXEME_HOY:
            case "diaria":
            case "diario":
                return HOY;

            case LEXEME_ERROR_COMMAND: return ERROR_COMMAND;
            case LEXEME_ERROR_CHARACTER: return ERROR_CHARACTER;
            default: return -1;
        }
    }

    public static boolean isCU(String lexeme) {
        if (lexeme == null || lexeme.trim().isEmpty()) return false;
        int id = new Token().findByLexeme(lexeme, -1);
        return 100 <= id && id < 200;
    }

    public static boolean isAction(String lexeme) {
        if (lexeme == null || lexeme.trim().isEmpty()) return false;
        int id = new Token().findByLexeme(lexeme, -1);
        return 200 <= id && id < 300;
    }

    public static int getCUId(String lexeme) {
        if (lexeme == null) return -1;
        int id = new Token().findByLexeme(lexeme, -1);
        return (100 <= id && id < 200) ? id : -1;
    }

    public static int getActionId(String lexeme) {
        if (lexeme == null) return -1;
        int id = new Token().findByLexeme(lexeme, -1);
        return (200 <= id && id < 300) ? id : -1;
    }

    public static String sugerirEntidadCercana(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) return "usuario";
        String p = java.text.Normalizer.normalize(palabra.toLowerCase().trim(), java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        if (p.contains("usu") || p.contains("usr") || p.contains("ser")) return "usuario";
        if (p.contains("rol")) return "rol";
        if (p.contains("cat") || p.contains("gen")) return "categoria";
        if (p.contains("prog") || p.contains("show")) return "programa";
        if (p.contains("parr") || p.contains("grill")) return "parrilla";
        if (p.contains("item") || p.contains("bloq") || p.contains("play")) return "item";
        if (p.contains("mat") || p.contains("vid") || p.contains("clip") || p.contains("mam")) return "material";
        if (p.contains("val") || p.contains("qc") || p.contains("calid")) return "validacion";
        if (p.contains("cli") || p.contains("anun")) return "cliente";
        if (p.contains("spot") || p.contains("pub")) return "spot";
        if (p.contains("paut") || p.contains("cort") || p.contains("tand")) return "pauta";
        if (p.contains("emi") || p.contains("aire") || p.contains("asrun")) return "emision";
        if (p.contains("aud") || p.contains("log")) return "auditoria";
        if (p.contains("rep") || p.contains("est") || p.contains("metr")) return "reporte";
        return null;
    }

    public static String sugerirAccionCercana(String palabra) {
        if (palabra == null || palabra.trim().isEmpty()) return "listar";
        String p = java.text.Normalizer.normalize(palabra.toLowerCase().trim(), java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        if (p.contains("list") || p.contains("ver") || p.contains("most") || p.contains("tod")) return "listar";
        if (p.contains("agr") || p.contains("cre") || p.contains("nuev") || p.contains("add") || p.contains("ins")) return "agregar";
        if (p.contains("cons") || p.contains("busc") || p.contains("det") || p.contains("inf") || p.contains("get")) return "consultar";
        if (p.contains("mod") || p.contains("act") || p.contains("edit") || p.contains("upd")) return "modificar";
        if (p.contains("elim") || p.contains("borr") || p.contains("del") || p.contains("quit")) return "eliminar";
        if (p.contains("aire") || p.contains("vivo") || p.contains("ahor")) return "al_aire";
        if (p.contains("hoy") || p.contains("diari")) return "hoy";
        if (p.contains("est") || p.contains("camb")) return "estado";
        if (p.contains("pend")) return "pendientes";
        if (p.contains("apr")) return "aprobar";
        if (p.contains("rech")) return "rechazar";
        if (p.contains("conc")) return "conciliar";
        if (p.contains("disc")) return "discrepancias";
        if (p.contains("gen")) return "general";
        if (p.contains("cump")) return "cumplimiento";
        if (p.contains("help") || p.contains("ayud") || p.contains("man")) return "help";
        return null;
    }
}