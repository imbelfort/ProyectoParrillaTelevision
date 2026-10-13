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

        while (true) {
            System.out.print("\nparrilla-tv> ");
            if (!scanner.hasNextLine()) {
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

            try {
                Email emailSimulado = new Email(remitenteSimulado, linea);
                Home.interprete(emailSimulado);
            } catch (Exception e) {
                System.err.println("[ERROR] Ocurrió un error al procesar el comando: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
