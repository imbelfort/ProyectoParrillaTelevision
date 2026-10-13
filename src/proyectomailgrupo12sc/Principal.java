package proyectomailgrupo12sc;

import Comunication.Home;
import Utils.Email;
import Comunication.SendEmail;
import Database.InicializarDB;

public class Principal extends javax.swing.JFrame {

    Home iniciar;
    SendEmail enviar;

    public Principal() {
        initComponents();
        setLocationRelativeTo(null);
        this.setTitle("SISTEMA DE GESTION DE PARRILLA DE TELEVISION - GRUPO 12 SC");

        iniciar = new Home();
        enviar = new SendEmail();

        System.out.println("=======================================================");
        System.out.println("  SERVIDOR DE PARRILLA DE TELEVISION - GRUPO 12 SC     ");
        System.out.println("=======================================================");
        System.out.println("Inicializando base de datos...");

        try {
            InicializarDB.crearTablasSiNoExisten();
            InicializarDB.insertarDatosIniciales();

            System.out.println("[OK] Base de datos de television inicializada correctamente");
        } catch (Exception e) {
            System.out.println("[AVISO] Servidor DB no disponible o modo offline: " + e.getMessage());
        }

        System.out.println("Iniciando servicio de monitoreo de correos...");

        Home.iniciar();

        System.out.println("[OK] Servicio de monitoreo ACTIVO");
        System.out.println("[OK] Monitoreando: grupo12sc@tecnoweb.org.bo");
        System.out.println("[OK] Revisando correos cada 5 segundos");
        System.out.println("=======================================================");
        System.out.println("El sistema esta listo para responder correos.");
        System.out.println("Envie un correo con subject 'help' para ver comandos.");
        System.out.println("=======================================================");

        tf_mailfrom.setText("ejemplo@gmail.com");
        tf_rcptto.setText("grupo12sc@tecnoweb.org.bo");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tf_mailfrom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tf_rcptto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tf_subject = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_respuesta = new javax.swing.JEditorPane();
        cbx_emailsReceptores = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        ta_emailsReceptores = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu = new javax.swing.JMenu();
        jMenuItemIniciar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(25, 255, 255));
        jLabel1.setForeground(new java.awt.Color(204, 255, 204));
        jLabel1.setText("MAIL FROM:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(129, 81, 70, 30));
        jPanel1.add(tf_mailfrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 79, 450, 30));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setForeground(new java.awt.Color(204, 255, 204));
        jLabel2.setText("RCPT TO:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(129, 145, 70, 30));
        jPanel1.add(tf_rcptto, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 140, 450, 30));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setForeground(new java.awt.Color(204, 255, 204));
        jLabel3.setText("SUBJECT:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(129, 205, 70, 30));
        jLabel3.getAccessibleContext().setAccessibleParent(this);

        jPanel1.add(tf_subject, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 200, 450, 30));

        java.net.URL sendIconUrl = getClass().getResource("/Images/image_send.png");

        if (sendIconUrl != null) {
            jButton1.setIcon(new javax.swing.ImageIcon(sendIconUrl));
        } else {
            jButton1.setText("Enviar");
        }

        jButton1.setText("Enviar");
        jButton1.setOpaque(true);
        jButton1.setContentAreaFilled(true);
        jButton1.setBorderPainted(true);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 380, 100, 40));

        jPanel2.setBackground(new java.awt.Color(0, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setBackground(new java.awt.Color(204, 255, 255));
        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel4.setForeground(new java.awt.Color(204, 255, 255));
        jLabel4.setText("RESPUESTA");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 120, 30));

        jScrollPane1.setViewportView(ta_respuesta);
        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 490, 440));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 510, 480));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setForeground(new java.awt.Color(204, 255, 204));
        jLabel5.setText("RECEPTORES:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 290, 100, 30));

        cbx_emailsReceptores.setBackground(new java.awt.Color(0, 51, 51));
        cbx_emailsReceptores.setFont(new java.awt.Font("Dialog", 3, 14));
        cbx_emailsReceptores.setForeground(new java.awt.Color(204, 255, 255));
        cbx_emailsReceptores.setText("Incluir Receptores");
        jPanel1.add(cbx_emailsReceptores, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 200, 25));

        ta_emailsReceptores.setColumns(20);
        ta_emailsReceptores.setRows(5);
        jScrollPane2.setViewportView(ta_emailsReceptores);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 260, 460, 200));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 480));

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));
        jMenuBar1.setForeground(new java.awt.Color(0, 204, 204));
        jMenuBar1.setOpaque(false);

        jMenu.setText("Servicio");

        jMenuItemIniciar.setText("Estado del Servicio");
        jMenuItemIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIniciarActionPerformed(evt);
            }
        });

        jMenu.add(jMenuItemIniciar);
        jMenuBar1.add(jMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }

    private void jMenuItemIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        String mensaje =
                "=======================================\n" +
                "  ESTADO DEL SERVICIO DE CORREO\n" +
                "=======================================\n\n" +
                "[OK] Servicio: ACTIVO\n" +
                "[OK] Correo monitoreado: grupo12sc@tecnoweb.org.bo\n" +
                "[OK] Frecuencia de revision: cada 5 segundos\n\n" +
                "El sistema esta respondiendo automaticamente\n" +
                "a todos los correos recibidos.\n\n" +
                "Envie un correo con subject 'help' para\n" +
                "ver la lista de comandos disponibles.";

        javax.swing.JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Estado del Servicio",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String mailfrom = tf_mailfrom.getText().trim();
        String rcptto = tf_rcptto.getText().trim();
        String subject = tf_subject.getText().trim();

        if (mailfrom.isEmpty() || rcptto.isEmpty() || subject.isEmpty()) {
            String errorMsg =
                    "<html><body style='font-family: Arial; padding: 20px;'>" +
                    "<h2 style='color: #d9534f;'>Error</h2>" +
                    "<p style='font-size: 14px;'>Por favor complete todos los campos:</p>" +
                    "<ul>" +
                    "<li><b>MAIL FROM:</b> Correo del remitente</li>" +
                    "<li><b>RCPT TO:</b> Correo del destinatario</li>" +
                    "<li><b>SUBJECT:</b> Comando o asunto. Ejemplo: help, usuario listar, programa listar, parrilla listar</li>" +
                    "</ul></body></html>";

            Home.mostraRespuesta(errorMsg);
            return;
        }

        String processingMsg =
                "<html><body style='font-family: Arial; padding: 20px;'>" +
                "<h2 style='color: #5bc0de;'>Procesando comando...</h2>" +
                "<p><b>Comando:</b> " + subject + "</p>" +
                "<p><b>De:</b> " + mailfrom + "</p>" +
                "<p><b>Para:</b> " + rcptto + "</p>" +
                "</body></html>";

        Home.mostraRespuesta(processingMsg);

        try {
            Email email = new Email(mailfrom, subject);
            Home.interprete(email);
            enviar.sendmail(rcptto, subject);

            System.out.println("[OK] Comando procesado y correo enviado desde el panel grafico");
        } catch (Exception e) {
            String errorMsg =
                    "<html><body style='font-family: Arial; padding: 20px;'>" +
                    "<h2 style='color: #d9534f;'>Error al procesar</h2>" +
                    "<p>" + e.getMessage() + "</p>" +
                    "</body></html>";

            Home.mostraRespuesta(errorMsg);
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        if (args != null && args.length > 0 && ("--console".equalsIgnoreCase(args[0]) || "-c".equalsIgnoreCase(args[0]))) {
            Consola.main(args);
            return;
        }
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            Consola.main(args);
            return;
        }

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    public static javax.swing.JCheckBox cbx_emailsReceptores;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemIniciar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public static javax.swing.JTextArea ta_emailsReceptores;
    public static javax.swing.JEditorPane ta_respuesta;
    private javax.swing.JTextField tf_mailfrom;
    private javax.swing.JTextField tf_rcptto;
    private javax.swing.JTextField tf_subject;
}