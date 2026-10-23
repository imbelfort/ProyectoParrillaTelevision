package proyectomailgrupo12sc;

import Comunication.Home;
import Database.InicializarDB;
import Utils.Email;

import java.util.Scanner;

/**
 * Modo Consola / Terminal para el Servidor de Casos Especiales Académicos.
 * Permite ejecutar el sistema sin entorno gráfico (Swing), manteniendo activo
 * el monitoreo continuo de correos electrónicos en segundo plano y permitiendo
 * ingresar comandos de prueba directamente por consola.
 */
public class Consola {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  SISTEMA DE GESTION DE PARRILLA DE TELEVISION - FICCT UAGRM (GRUPO 12)");
        System.out.println("                     MODO TERMINAL / CONSOLA                     ");
        System.out.println("=================================================================");

        // 1. Inicialización de la base de datos
        System.out.println("[INFO] Inicializando base de datos PostgreSQL...");
        try {
            InicializarDB.crearTablasSiNoExisten();
            InicializarDB.insertarDatosIniciales();
            System.out.println("[OK] Base de datos verificada y lista.");
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo conectar a la BD o ya estaba inicializada: " + e.getMessage());
        }

        // 2. Inicio del hilo de monitoreo de correos (POP3 / SMTP)
        System.out.println("[INFO] Iniciando demonio de monitoreo de correos POP3...");
        try {
            Home.iniciar();
            System.out.println("[OK] Demonio de correo ACTIVO (cuenta: grupo12sc@tecnoweb.org.bo)");
            System.out.println("[OK] Revisando servidor cada 5 segundos.");
        } catch (Exception e) {
            System.err.println("[ERROR] Error al iniciar el hilo de correo: " + e.getMessage());
        }

        System.out.println("=================================================================");
        System.out.println("El sistema está activo respondiendo correos entrantes.");
        System.out.println("Puede escribir comandos directamente en esta consola o 'exit' para salir.");
        System.out.println("Comandos de ejemplo:");
        System.out.println("  - help");
        System.out.println("  - usuario listar");
        System.out.println("  - programa listar");
        System.out.println("  - parrilla listar");
        System.out.println("  - material_video listar");
        System.out.println("  - pauta listar");
        System.out.println("  - emision_real listar");
        System.out.println("  - reporte general");
        System.out.println("=================================================================");

        Scanner scanner = new Scanner(System.in);
        String remitenteSimulado = "admin@tecnoweb.org.bo";
        String rolSimulado = "ADMINISTRADOR";

        if (!scanner.hasNextLine()) {
            System.out.println("[INFO] Demonio en segundo plano sin terminal interactiva detectado (Docker / headless).");
            System.out.println("[INFO] El servidor de correos continuara monitoreando indefinidamente...");
            try {
                while (true) {
                    Thread.sleep(60000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[INFO] Demonio detenido.");
            }
            return;
        }

        System.out.println("\n[TIPS PARA MODO CONSOLA]:");
        System.out.println("  - Cambiar de rol: 'as admin' | 'as programador' | 'as contenidos'");
        System.out.println("  - Ver rol activo: 'whoami'");
        System.out.println("  - Ver roles y casos de uso: 'roles'");
        System.out.println("  - Cambiar remitente manual: 'set-from <correo>'");
        System.out.println("  - Salir: 'exit'\n");

        while (true) {
            System.out.print("[" + rolSimulado + " | " + remitenteSimulado + "] tv> ");
            if (!scanner.hasNextLine()) {
                System.out.println("[INFO] Fin de entrada interactiva. Manteniendo servidor activo...");
                try {
                    while (true) {
                        Thread.sleep(60000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            }
            String linea = scanner.nextLine().trim();

            if (linea.isEmpty()) {
                continue;
            }

            if ("exit".equalsIgnoreCase(linea) || "salir".equalsIgnoreCase(linea) || "quit".equalsIgnoreCase(linea)) {
                System.out.println("[INFO] Deteniendo servidor de consola. ¡Hasta luego!");
                System.exit(0);
                break;
            }

            // Comandos internos de consola
            if ("whoami".equalsIgnoreCase(linea)) {
                System.out.println("\n=======================================================");
                System.out.println(" USUARIO ACTIVO EN CONSOLA:");
                System.out.println("   Correo: " + remitenteSimulado);
                System.out.println("   Rol:    " + rolSimulado);
                System.out.println("   Perfil: " + getDescripcionRol(rolSimulado));
                System.out.println("=======================================================\n");
                continue;
            }

            if ("roles".equalsIgnoreCase(linea)) {
                imprimirAyudaRoles();
                continue;
            }

            if (linea.toLowerCase().startsWith("as ")) {
                String target = linea.substring(3).trim().toLowerCase();
                if (target.contains("admin")) {
                    remitenteSimulado = "admin@tecnoweb.org.bo";
                    rolSimulado = "ADMINISTRADOR";
                    System.out.println("[OK] Rol cambiado a: ADMINISTRADOR (" + remitenteSimulado + ")");
                } else if (target.contains("prog")) {
                    remitenteSimulado = "programador@tecnoweb.org.bo";
                    rolSimulado = "PROGRAMADOR";
                    System.out.println("[OK] Rol cambiado a: PROGRAMADOR (" + remitenteSimulado + ")");
                } else if (target.contains("cont") || target.contains("oper") || target.contains("qc")) {
                    remitenteSimulado = "contenidos@tecnoweb.org.bo";
                    rolSimulado = "ENCARGADO_CONTENIDOS";
                    System.out.println("[OK] Rol cambiado a: ENCARGADO_CONTENIDOS / QC (" + remitenteSimulado + ")");
                } else if (target.contains("@")) {
                    remitenteSimulado = target;
                    rolSimulado = "USUARIO_EXTERNO";
                    System.out.println("[OK] Remitente cambiado a: " + remitenteSimulado);
                } else {
                    System.out.println("[WARN] Rol no reconocido. Opciones: 'as admin', 'as programador', 'as contenidos' o 'as <email>'");
                }
                continue;
            }

            if (linea.toLowerCase().startsWith("set-from ")) {
                remitenteSimulado = linea.substring(9).trim();
                rolSimulado = "PERSONALIZADO";
                System.out.println("[OK] Remitente simulado cambiado a: " + remitenteSimulado);
                continue;
            }

            try {
                Email emailSimulado = new Email(remitenteSimulado, linea);
                Home.interprete(emailSimulado);
            } catch (Exception e) {
                System.err.println("[ERROR] Ocurrió un error al procesar el comando: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static String getDescripcionRol(String rol) {
        if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
            return "Gestion de Usuarios, Roles, Auditoria del Sistema y Reportes Gerenciales (CU1, CU7, CU8).";
        }
        if ("PROGRAMADOR".equalsIgnoreCase(rol)) {
            return "Catalogo de Programas, Confeccion de Parrilla, Bloques de Emision y Pautas Publicitarias (CU2, CU3, CU6, CU8).";
        }
        if ("ENCARGADO_CONTENIDOS".equalsIgnoreCase(rol)) {
            return "Ingesta de Materiales de Video (MAM), Control de Calidad QC y Conciliacion de Emision Real (CU4, CU5, CU7).";
        }
        return "Usuario externo del canal de television.";
    }

    private static void imprimirAyudaRoles() {
        System.out.println("\n=========================================================================");
        System.out.println(" ROLES OPERATIVOS DEL CANAL DE TELEVISIÓN (FICCT UAGRM)");
        System.out.println("=========================================================================");
        System.out.println("1. ADMINISTRADOR ('as admin') -> admin@tecnoweb.org.bo");
        System.out.println("   - CU1: usuario agregar | usuario listar | usuario modificar | usuario eliminar");
        System.out.println("   - CU1: rol listar | rol agregar | rol asignar");
        System.out.println("   - CU7: auditoria listar | auditoria consultar <id>");
        System.out.println("   - CU8: reporte general | reporte cumplimiento");
        System.out.println();
        System.out.println("2. PROGRAMADOR ('as programador') -> programador@tecnoweb.org.bo");
        System.out.println("   - CU2: programa agregar | programa listar | programa consultar | categoria listar");
        System.out.println("   - CU3: parrilla crear | parrilla listar | parrilla estado | parrilla hoy | parrilla al_aire");
        System.out.println("   - CU3: item agregar | item listar | item consultar | item modificar");
        System.out.println("   - CU6: cliente listar | spot listar | pauta agregar | pauta listar");
        System.out.println("   - CU8: reporte parrilla | reporte programas");
        System.out.println();
        System.out.println("3. ENCARGADO DE CONTENIDOS / QC / OPERADOR ('as contenidos') -> contenidos@tecnoweb.org.bo");
        System.out.println("   - CU4: material agregar | material listar | material consultar | material estado");
        System.out.println("   - CU5: validacion pendientes | validacion aprobar | validacion rechazar");
        System.out.println("   - CU6: pauta estado <id>,EMITIDO");
        System.out.println("   - CU7: emision conciliar <id> | emision listar | emision discrepancias | emision al_aire");
        System.out.println("   - CU8: reporte pautas | reporte cumplimiento");
        System.out.println("=========================================================================\n");
    }
}
