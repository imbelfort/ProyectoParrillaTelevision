package test;

import Comunication.TokenEvent;
import Interfaces.ITokenCasoUsoEventListener;
import Utils.ErrorCode;
import Utils.Interpreter;

public class PruebaCodigosError {

    private static int pruebasExitosas = 0;
    private static int pruebasTotales = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  INICIANDO SUITE DE PRUEBAS DE CÓDIGOS DE ERROR EXPLÍCITOS");
        System.out.println("=================================================================");

        // Test 1: Comando vacío
        probarComando("", ErrorCode.ERR_CMD_001, "Comando Vacío");

        // Test 2: Orden invertido - acción antes de entidad (CU1 a CU8)
        probarComando("listar usuario", ErrorCode.ERR_ORD_001, "CU1 Invertido (listar usuario)");
        probarComando("consultar programa 1", ErrorCode.ERR_ORD_001, "CU2 Invertido (consultar programa 1)");
        probarComando("listar parrilla", ErrorCode.ERR_ORD_001, "CU3 Invertido (listar parrilla)");
        probarComando("listar material", ErrorCode.ERR_ORD_001, "CU4 Invertido (listar material)");
        probarComando("aprobar validacion 1", ErrorCode.ERR_ORD_001, "CU5 Invertido (aprobar validacion 1)");
        probarComando("listar pauta", ErrorCode.ERR_ORD_001, "CU6 Invertido (listar pauta)");
        probarComando("conciliar emision 1", ErrorCode.ERR_ORD_001, "CU7 Invertido (conciliar emision 1)");
        probarComando("general reporte", ErrorCode.ERR_ORD_001, "CU8 Invertido (general reporte)");

        // Test 3: Parámetro antes de la entidad
        probarComando("1 usuario consultar", ErrorCode.ERR_ORD_002, "CU1 Parámetro al inicio (1 usuario consultar)");
        probarComando("5 material consultar", ErrorCode.ERR_ORD_002, "CU4 Parámetro al inicio (5 material consultar)");

        // Test 4: Orden incompleta - solo entidad (CU1 a CU8)
        probarComando("usuario", ErrorCode.ERR_INC_001, "CU1 Incompleto (usuario)");
        probarComando("programa", ErrorCode.ERR_INC_001, "CU2 Incompleto (programa)");
        probarComando("parrilla", ErrorCode.ERR_INC_001, "CU3 Incompleto (parrilla)");
        probarComando("material", ErrorCode.ERR_INC_001, "CU4 Incompleto (material)");
        probarComando("validacion", ErrorCode.ERR_INC_001, "CU5 Incompleto (validacion)");
        probarComando("pauta", ErrorCode.ERR_INC_001, "CU6 Incompleto (pauta)");
        probarComando("spot", ErrorCode.ERR_INC_001, "CU6 Incompleto (spot)");
        probarComando("emision", ErrorCode.ERR_INC_001, "CU7 Incompleto (emision)");
        probarComando("reporte", ErrorCode.ERR_INC_001, "CU8 Incompleto (reporte)");

        // Test 5: Parámetros mal ordenados (ID antes de acción)
        probarComando("usuario 1 consultar", ErrorCode.ERR_ORD_003, "CU1 Parámetro antes de acción (usuario 1 consultar)");
        probarComando("validacion 1 aprobar", ErrorCode.ERR_ORD_003, "CU5 Parámetro antes de acción (validacion 1 aprobar)");
        probarComando("material 3 consultar", ErrorCode.ERR_ORD_003, "CU4 Parámetro antes de acción (material 3 consultar)");

        // Test 6: Entidad mal escrita (typos)
        probarComando("usuari listar", ErrorCode.ERR_CMD_002, "CU1 Typo (usuari listar)");
        probarComando("programaa listar", ErrorCode.ERR_CMD_002, "CU2 Typo (programaa listar)");
        probarComando("materia listar", ErrorCode.ERR_CMD_002, "CU4 Typo (materia listar)");
        probarComando("validacio pendientes", ErrorCode.ERR_CMD_002, "CU5 Typo (validacio pendientes)");
        probarComando("spott listar", ErrorCode.ERR_CMD_002, "CU6 Typo (spott listar)");
        probarComando("pautaa listar", ErrorCode.ERR_CMD_002, "CU6 Typo (pautaa listar)");

        // Test 7: Acción no reconocida para entidad
        probarComando("usuario bailar", ErrorCode.ERR_CMD_003, "CU1 Acción desconocida (usuario bailar)");
        probarComando("material cocinar", ErrorCode.ERR_CMD_003, "CU4 Acción desconocida (material cocinar)");
        probarComando("pauta bailar", ErrorCode.ERR_CMD_003, "CU6 Acción desconocida (pauta bailar)");

        // Test 8: Carácter inválido
        probarComando("usuario listar #$", ErrorCode.ERR_CMD_004, "Carácter no permitido (usuario listar #$)");

        // Test 9: Comandos válidos en todos los CUs no deben disparar error sintáctico
        probarComandoValido("usuario listar", "CU1 Válido (usuario listar)");
        probarComandoValido("programa listar", "CU2 Válido (programa listar)");
        probarComandoValido("parrilla al_aire", "CU3 Válido (parrilla al_aire)");
        probarComandoValido("material listar", "CU4 Válido (material listar)");
        probarComandoValido("validacion pendientes", "CU5 Válido (validacion pendientes)");
        probarComandoValido("pauta listar", "CU6 Válido (pauta listar)");
        probarComandoValido("emision al_aire", "CU7 Válido (emision al_aire)");
        probarComandoValido("reporte general", "CU8 Válido (reporte general)");

        System.out.println("\n=================================================================");
        System.out.println("  RESULTADO: " + pruebasExitosas + "/" + pruebasTotales + " pruebas superadas con éxito!");
        System.out.println("=================================================================");
    }

    private static void probarComando(String comando, String codigoEsperado, String casoPrueba) {
        pruebasTotales++;
        Interpreter interpreter = new Interpreter(comando, "test@correo.com");
        final boolean[] errorCapturado = {false};
        final String[] codigoCapturado = {null};
        final String[] detalleCapturado = {null};
        final String[] sugerenciaCapturada = {null};

        interpreter.setListener(new DummyListener() {
            @Override
            public void error(TokenEvent event) {
                errorCapturado[0] = true;
                codigoCapturado[0] = event.getErrorCode();
                detalleCapturado[0] = !event.getParams().isEmpty() ? event.getParams().get(0) : "";
                sugerenciaCapturada[0] = event.getSuggestion();
            }
        });

        interpreter.run();

        if (errorCapturado[0] && codigoEsperado.equals(codigoCapturado[0])) {
            pruebasExitosas++;
            System.out.println("✔ [OK] " + casoPrueba + " -> " + codigoCapturado[0] + " | Detalle: " + detalleCapturado[0] + " | Sugerencia: " + sugerenciaCapturada[0]);
        } else {
            System.err.println("✖ [FAIL] " + casoPrueba + " -> Esperado: " + codigoEsperado + ", pero se obtuvo: " + codigoCapturado[0] + " (errorCapturado=" + errorCapturado[0] + ")");
        }
    }

    private static void probarComandoValido(String comando, String casoPrueba) {
        pruebasTotales++;
        Interpreter interpreter = new Interpreter(comando, "test@correo.com");
        final boolean[] operacionLlamada = {false};
        final boolean[] errorLlamado = {false};

        interpreter.setListener(new DummyListener() {
            @Override
            public void usuario(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void rol(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void categoria(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void programa(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void parrilla(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void itemParrilla(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void materialVideo(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void validacion(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void cliente(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void spot(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void pauta(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void emisionReal(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void auditoria(TokenEvent event) { operacionLlamada[0] = true; }
            @Override
            public void reporte(TokenEvent event) { operacionLlamada[0] = true; }

            @Override
            public void error(TokenEvent event) {
                errorLlamado[0] = true;
            }
        });

        interpreter.run();

        if (operacionLlamada[0] && !errorLlamado[0]) {
            pruebasExitosas++;
            System.out.println("✔ [OK] " + casoPrueba + " -> Ejecutado correctamente sin errores.");
        } else {
            System.err.println("✖ [FAIL] " + casoPrueba + " -> Falló ejecución. operacionLlamada=" + operacionLlamada[0] + ", errorLlamado=" + errorLlamado[0]);
        }
    }

    private static class DummyListener implements ITokenCasoUsoEventListener {
        public void usuario(TokenEvent event) {}
        public void rol(TokenEvent event) {}
        public void categoria(TokenEvent event) {}
        public void programa(TokenEvent event) {}
        public void parrilla(TokenEvent event) {}
        public void itemParrilla(TokenEvent event) {}
        public void materialVideo(TokenEvent event) {}
        public void validacion(TokenEvent event) {}
        public void cliente(TokenEvent event) {}
        public void spot(TokenEvent event) {}
        public void pauta(TokenEvent event) {}
        public void emisionReal(TokenEvent event) {}
        public void auditoria(TokenEvent event) {}
        public void reporte(TokenEvent event) {}
        public void error(TokenEvent event) {}
        public void posibleserrores(TokenEvent event) {}
    }
}
