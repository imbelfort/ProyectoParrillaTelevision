package Comunication;

import Interfaces.IEmailEventListener;
import Interfaces.ITokenCasoUsoEventListener;
import Services.*;
import Utils.CommandException;
import Utils.Email;
import Utils.ErrorCode;
import Utils.Interpreter;
import Utils.Token;
import proyectomailgrupo12sc.Principal;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

public class Home {

    public static void iniciar() {
        MailVerificationThread mail = new MailVerificationThread();
        mail.setEmailEventListener(new IEmailEventListener() {
            @Override
            public void onReceiveEmailEvent(List<Email> emails) {
                for (Email email : emails) {
                    interprete(email);
                }
            }
        });
        Thread thread = new Thread(mail);
        thread.setName("Mail Verification Thread");
        thread.start();
    }

    public static void interprete(Email email) {
        SendEmail respuesta = new SendEmail();

        // Inicialización de Servicios (8 Casos de Uso Aprobados)
        final UsuarioService usuarioService = new UsuarioService();
        final RolService rolService = new RolService();
        final CategoriaProgramaService categoriaService = new CategoriaProgramaService();
        final ProgramaService programaService = new ProgramaService();
        final ParrillaService parrillaService = new ParrillaService();
        final ItemParrillaService itemParrillaService = new ItemParrillaService();
        final MaterialVideoService materialVideoService = new MaterialVideoService();
        final ValidacionEmisionService validacionService = new ValidacionEmisionService();
        final ClientePublicitarioService clienteService = new ClientePublicitarioService();
        final SpotPublicitarioService spotService = new SpotPublicitarioService();
        final PautaComercialService pautaService = new PautaComercialService();
        final EmisionRealLogService emisionRealService = new EmisionRealLogService();
        final AuditoriaSistemaService auditoriaService = new AuditoriaSistemaService();
        final ReporteService reporteService = new ReporteService();

        String subjectText = email.getSubject() != null ? email.getSubject().trim() : "";
        System.out.println("[COMANDO RECIBIDO] De: " + email.getFrom() + " | Subject: " + subjectText);

        // Comando HELP global
        if ("help".equalsIgnoreCase(subjectText) || "ayuda".equalsIgnoreCase(subjectText)) {
            String menuAyuda = getMenuGeneralAyuda();
            mostrarYEnviarHELP(email, respuesta, menuAyuda);
            return;
        }

        Interpreter interpreter = new Interpreter(subjectText, email.getFrom());
        interpreter.setListener(new ITokenCasoUsoEventListener() {

            // ============================================================
            // CU1: GESTIÓN DE USUARIOS Y ROLES
            // ============================================================

            @Override
            public void usuario(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = usuarioService.listarUsuarios();
                            enviarTabla(email, respuesta, "CU1 - Lista de Usuarios", HtmlBuilder.HEADERS_USER, list);
                            break;
                        case Token.AGREGAR:
                            usuarioService.crearUsuario(event.getParams(), email.getAttachmentContentBase64());
                            auditoriaService.registrarAuditoria(1, "usuario", "INSERT", "Usuario agregado: " + event.getParams());
                            List<String[]> listAdd = usuarioService.listarUsuarios();
                            enviarTabla(email, respuesta, "Usuario agregado exitosamente", HtmlBuilder.HEADERS_USER, listAdd);
                            break;
                        case Token.MODIFICAR:
                            usuarioService.actualizarUsuario(event.getParams(), email.getAttachmentContentBase64());
                            auditoriaService.registrarAuditoria(1, "usuario", "UPDATE", "Usuario modificado: " + event.getParams());
                            List<String[]> listMod = usuarioService.listarUsuarios();
                            enviarTabla(email, respuesta, "Usuario modificado exitosamente", HtmlBuilder.HEADERS_USER, listMod);
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del usuario a eliminar. Uso: usuario eliminar id", "usuario eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            usuarioService.eliminarUsuario(id);
                            auditoriaService.registrarAuditoria(1, "usuario", "DELETE", "Usuario eliminado ID: " + id);
                            List<String[]> listDel = usuarioService.listarUsuarios();
                            enviarTabla(email, respuesta, "Usuario eliminado exitosamente", HtmlBuilder.HEADERS_USER, listDel);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del usuario a consultar. Uso: usuario consultar id", "usuario consultar 1");
                            int idCons = Integer.parseInt(event.getParams().get(0).trim());
                            String[] usuarioData = usuarioService.consultarUsuario(idCons);
                            if (usuarioData == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Usuario No Encontrado", "No existe usuario con ID " + idCons, "usuario listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Usuario #" + idCons, HtmlBuilder.HEADERS_USER, Collections.singletonList(usuarioData));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, usuarioService.getComandosUsuario());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'usuario'.", usuarioService.getComandosUsuario());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'usuario'.", usuarioService.getComandosUsuario());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, usuarioService.getComandosUsuario());
                }
            }

            @Override
            public void rol(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = rolService.listarRoles();
                            enviarTabla(email, respuesta, "CU1 - Lista de Roles", HtmlBuilder.HEADERS_ROL, list);
                            break;
                        case Token.AGREGAR:
                            rolService.crearRol(event.getParams());
                            List<String[]> listAdd = rolService.listarRoles();
                            enviarTabla(email, respuesta, "Rol agregado exitosamente", HtmlBuilder.HEADERS_ROL, listAdd);
                            break;
                        case Token.ASIGNAR:
                            rolService.asignarRol(event.getParams());
                            enviarExito(email, respuesta, "Rol Asignado", "Rol asignado correctamente al usuario.");
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, rolService.getComandosRol());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'rol'.", rolService.getComandosRol());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'rol'.", rolService.getComandosRol());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, rolService.getComandosRol());
                }
            }

            // ============================================================
            // CU2: GESTIÓN DE CATÁLOGO DE PROGRAMAS
            // ============================================================

            @Override
            public void categoria(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = categoriaService.listarCategorias();
                            enviarTabla(email, respuesta, "CU2 - Categorías de Programas", HtmlBuilder.HEADERS_CATEGORIA, list);
                            break;
                        case Token.AGREGAR:
                            categoriaService.crearCategoria(event.getParams());
                            List<String[]> listAdd = categoriaService.listarCategorias();
                            enviarTabla(email, respuesta, "Categoría agregada exitosamente", HtmlBuilder.HEADERS_CATEGORIA, listAdd);
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, categoriaService.getComandosCategoria());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'categoria'.", categoriaService.getComandosCategoria());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'categoria'.", categoriaService.getComandosCategoria());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, categoriaService.getComandosCategoria());
                }
            }

            @Override
            public void programa(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = programaService.listarProgramas();
                            enviarTabla(email, respuesta, "CU2 - Catálogo de Programas", HtmlBuilder.HEADERS_PROGRAMA, list);
                            break;
                        case Token.AGREGAR:
                            programaService.crearPrograma(event.getParams());
                            auditoriaService.registrarAuditoria(1, "programa", "INSERT", "Programa agregado: " + event.getParams());
                            List<String[]> listAdd = programaService.listarProgramas();
                            enviarTabla(email, respuesta, "Programa registrado en catálogo", HtmlBuilder.HEADERS_PROGRAMA, listAdd);
                            break;
                        case Token.MODIFICAR:
                            programaService.modificarPrograma(event.getParams());
                            auditoriaService.registrarAuditoria(1, "programa", "UPDATE", "Programa modificado: " + event.getParams());
                            List<String[]> listMod = programaService.listarProgramas();
                            enviarTabla(email, respuesta, "Programa actualizado exitosamente", HtmlBuilder.HEADERS_PROGRAMA, listMod);
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del programa a eliminar. Uso: programa eliminar id", "programa eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            programaService.eliminarPrograma(id);
                            auditoriaService.registrarAuditoria(1, "programa", "DELETE", "Programa eliminado ID: " + id);
                            List<String[]> listDel = programaService.listarProgramas();
                            enviarTabla(email, respuesta, "Programa eliminado", HtmlBuilder.HEADERS_PROGRAMA, listDel);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del programa a consultar. Uso: programa consultar id", "programa consultar 1");
                            int idCons = Integer.parseInt(event.getParams().get(0).trim());
                            String[] prog = programaService.consultarPrograma(idCons);
                            if (prog == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Programa No Encontrado", "No existe programa con ID " + idCons, "programa listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Programa #" + idCons, HtmlBuilder.HEADERS_PROGRAMA, Collections.singletonList(prog));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, programaService.getComandosPrograma());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'programa'.", programaService.getComandosPrograma());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'programa'.", programaService.getComandosPrograma());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, programaService.getComandosPrograma());
                }
            }

            // ============================================================
            // CU3: GESTIÓN DE CONFECCIONAR Y CONTROLAR LA PARRILLA
            // ============================================================

            @Override
            public void parrilla(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = parrillaService.listarParrillas();
                            enviarTabla(email, respuesta, "CU3 - Parrillas de Programación", HtmlBuilder.HEADERS_PARRILLA, list);
                            break;
                        case Token.AGREGAR:
                            parrillaService.crearParrilla(event.getParams());
                            auditoriaService.registrarAuditoria(1, "parrilla", "INSERT", "Parrilla creada: " + event.getParams());
                            List<String[]> listAdd = parrillaService.listarParrillas();
                            enviarTabla(email, respuesta, "Parrilla creada exitosamente", HtmlBuilder.HEADERS_PARRILLA, listAdd);
                            break;
                        case Token.CAMBIAR_ESTADO:
                            parrillaService.cambiarEstado(event.getParams());
                            auditoriaService.registrarAuditoria(1, "parrilla", "UPDATE_ESTADO", "Estado modificado: " + event.getParams());
                            List<String[]> listEst = parrillaService.listarParrillas();
                            enviarTabla(email, respuesta, "Estado de Parrilla actualizado", HtmlBuilder.HEADERS_PARRILLA, listEst);
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID de la parrilla a eliminar. Uso: parrilla eliminar id", "parrilla eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            parrillaService.eliminarParrilla(id);
                            List<String[]> listDel = parrillaService.listarParrillas();
                            enviarTabla(email, respuesta, "Parrilla eliminada", HtmlBuilder.HEADERS_PARRILLA, listDel);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID de la parrilla a consultar. Uso: parrilla consultar id_parrilla", "parrilla consultar 1");
                            int idParr = Integer.parseInt(event.getParams().get(0).trim());
                            String[] parr = parrillaService.consultarParrilla(idParr);
                            if (parr == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Parrilla No Encontrada", "No existe parrilla con ID " + idParr, "parrilla listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Parrilla #" + idParr, HtmlBuilder.HEADERS_PARRILLA, Collections.singletonList(parr));
                            }
                            break;
                        case Token.AL_AIRE:
                            String[] alAireP = itemParrillaService.consultarAlAire();
                            if (alAireP != null) {
                                enviarTabla(email, respuesta, "Programa Actualmente al Aire (Playout)", HtmlBuilder.HEADERS_ITEM_PARRILLA, Collections.singletonList(alAireP));
                            } else {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Sin Emisión Activa", "No hay bloques de programación activos en este momento.", "parrilla hoy");
                            }
                            break;
                        case Token.HOY:
                            List<String[]> listHoyP = itemParrillaService.listarHoy();
                            enviarTabla(email, respuesta, "Parrilla Oficial - Programación de Hoy", HtmlBuilder.HEADERS_ITEM_PARRILLA, listHoyP);
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, parrillaService.getComandosParrilla());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'parrilla'.", parrillaService.getComandosParrilla());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'parrilla'.", parrillaService.getComandosParrilla());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, parrillaService.getComandosParrilla());
                }
            }

            @Override
            public void itemParrilla(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list;
                            String tituloTabla;
                            if (event.getParams().isEmpty()) {
                                list = itemParrillaService.listarTodos();
                                tituloTabla = "CU3 - Todos los Bloques de Parrilla";
                            } else {
                                int idParrilla = Integer.parseInt(event.getParams().get(0).trim());
                                list = itemParrillaService.listarPorParrilla(idParrilla);
                                tituloTabla = "CU3 - Bloques de Parrilla #" + idParrilla;
                            }
                            enviarTabla(email, respuesta, tituloTabla, HtmlBuilder.HEADERS_ITEM_PARRILLA, list);
                            break;
                        case Token.AGREGAR:
                            itemParrillaService.crearItemParrilla(event.getParams());
                            int idRet = Integer.parseInt(event.getParams().get(0).trim());
                            List<String[]> listAdd = itemParrillaService.listarPorParrilla(idRet);
                            enviarTabla(email, respuesta, "Bloque asignado a parrilla", HtmlBuilder.HEADERS_ITEM_PARRILLA, listAdd);
                            break;
                        case Token.MODIFICAR:
                            itemParrillaService.modificarItemParrilla(event.getParams());
                            enviarExito(email, respuesta, "Bloque de Parrilla Modificado", "Horarios y estado actualizados correctamente.");
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque a eliminar. Uso: item_parrilla eliminar id_item", "item_parrilla eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            itemParrillaService.eliminarItemParrilla(id);
                            enviarExito(email, respuesta, "Bloque Eliminado", "Item de parrilla removido correctamente.");
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque a consultar. Uso: item_parrilla consultar id_item", "item_parrilla consultar 1");
                            int idCons = Integer.parseInt(event.getParams().get(0).trim());
                            String[] itemData = itemParrillaService.consultarItem(idCons);
                            if (itemData == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Bloque No Encontrado", "No existe bloque con ID " + idCons, "item_parrilla listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Bloque #" + idCons, HtmlBuilder.HEADERS_ITEM_PARRILLA, Collections.singletonList(itemData));
                            }
                            break;
                        case Token.AL_AIRE:
                            String[] alAire = itemParrillaService.consultarAlAire();
                            if (alAire != null) {
                                enviarTabla(email, respuesta, "Programa Actualmente al Aire (Playout Rundown)", HtmlBuilder.HEADERS_ITEM_PARRILLA, Collections.singletonList(alAire));
                            } else {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Sin Emisión Activa", "No hay bloques de programación activos en este horario.", "item_parrilla listar");
                            }
                            break;
                        case Token.HOY:
                            List<String[]> listHoy = itemParrillaService.listarHoy();
                            enviarTabla(email, respuesta, "Rundown de Parrilla de Hoy", HtmlBuilder.HEADERS_ITEM_PARRILLA, listHoy);
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, itemParrillaService.getComandosItemParrilla());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'item_parrilla'.", itemParrillaService.getComandosItemParrilla());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'item_parrilla'.", itemParrillaService.getComandosItemParrilla());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, itemParrillaService.getComandosItemParrilla());
                }
            }

            // ============================================================
            // CU4: GESTIÓN DE MATERIALES DE VIDEO
            // ============================================================

            @Override
            public void materialVideo(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = materialVideoService.listarMateriales();
                            enviarTabla(email, respuesta, "CU4 - Materiales de Video (MAM)", HtmlBuilder.HEADERS_MATERIAL_VIDEO, list);
                            break;
                        case Token.LISTAR_PROGRAMA:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del programa. Uso: material listar_programa id_programa", "material listar_programa 1");
                            int idProg = Integer.parseInt(event.getParams().get(0).trim());
                            List<String[]> listProg = materialVideoService.listarPorPrograma(idProg);
                            enviarTabla(email, respuesta, "CU4 - Clips de Video de Programa #" + idProg, HtmlBuilder.HEADERS_MATERIAL_VIDEO, listProg);
                            break;
                        case Token.AGREGAR:
                            materialVideoService.crearMaterial(event.getParams());
                            auditoriaService.registrarAuditoria(1, "material_video", "INSERT", "Material ingresado: " + event.getParams());
                            List<String[]> listAdd = materialVideoService.listarMateriales();
                            enviarTabla(email, respuesta, "Material de video ingestado exitosamente", HtmlBuilder.HEADERS_MATERIAL_VIDEO, listAdd);
                            break;
                        case Token.CAMBIAR_ESTADO:
                            materialVideoService.actualizarEstado(event.getParams());
                            auditoriaService.registrarAuditoria(1, "material_video", "UPDATE_ESTADO", "Estado material: " + event.getParams());
                            List<String[]> listEst = materialVideoService.listarMateriales();
                            enviarTabla(email, respuesta, "Estado técnico de material actualizado", HtmlBuilder.HEADERS_MATERIAL_VIDEO, listEst);
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del material a eliminar. Uso: material eliminar id", "material eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            materialVideoService.eliminarMaterial(id);
                            List<String[]> listDel = materialVideoService.listarMateriales();
                            enviarTabla(email, respuesta, "Material de video eliminado", HtmlBuilder.HEADERS_MATERIAL_VIDEO, listDel);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del material a consultar. Uso: material consultar id", "material consultar 1");
                            int idCons = Integer.parseInt(event.getParams().get(0).trim());
                            String[] mat = materialVideoService.consultarMaterial(idCons);
                            if (mat == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Material No Encontrado", "No existe material con ID " + idCons, "material listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Material #" + idCons, HtmlBuilder.HEADERS_MATERIAL_VIDEO, Collections.singletonList(mat));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, materialVideoService.getComandosMaterial());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'material'.", materialVideoService.getComandosMaterial());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'material'.", materialVideoService.getComandosMaterial());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, materialVideoService.getComandosMaterial());
                }
            }

            // ============================================================
            // CU5: GESTIÓN DE VALIDAR ESTADO DE EMISIÓN (QC)
            // ============================================================

            @Override
            public void validacion(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = validacionService.listarValidaciones();
                            enviarTabla(email, respuesta, "CU5 - Validaciones de Calidad (QC)", HtmlBuilder.HEADERS_VALIDACION, list);
                            break;
                        case Token.PENDIENTES:
                            List<String[]> listPend = validacionService.listarPendientes();
                            enviarTabla(email, respuesta, "CU5 - Items de Parrilla Pendientes de QC", HtmlBuilder.HEADERS_VALIDACION_PENDIENTES, listPend);
                            break;
                        case Token.APROBAR:
                            validacionService.aprobarValidacion(event.getParams());
                            auditoriaService.registrarAuditoria(1, "validacion_emision", "APROBAR", "QC Aprobado: " + event.getParams());
                            List<String[]> listApr = validacionService.listarValidaciones();
                            enviarTabla(email, respuesta, "Bloque Aprobado para Aire (QC OK)", HtmlBuilder.HEADERS_VALIDACION, listApr);
                            break;
                        case Token.RECHAZAR:
                            validacionService.rechazarValidacion(event.getParams());
                            auditoriaService.registrarAuditoria(1, "validacion_emision", "RECHAZAR", "QC Rechazado: " + event.getParams());
                            List<String[]> listRech = validacionService.listarValidaciones();
                            enviarTabla(email, respuesta, "Bloque Rechazado en Control de Calidad", HtmlBuilder.HEADERS_VALIDACION, listRech);
                            break;
                        case Token.REGISTRAR:
                            validacionService.registrarValidacion(event.getParams());
                            auditoriaService.registrarAuditoria(1, "validacion_emision", "INSERT", "QC Registrado: " + event.getParams());
                            List<String[]> listVal = validacionService.listarValidaciones();
                            enviarTabla(email, respuesta, "Control de Calidad (QC) Registrado", HtmlBuilder.HEADERS_VALIDACION, listVal);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque a consultar QC. Uso: validacion consultar id_item_parrilla", "validacion consultar 1");
                            int idItem = Integer.parseInt(event.getParams().get(0).trim());
                            String[] val = validacionService.consultarPorItem(idItem);
                            if (val == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Sin Validación QC", "El bloque #" + idItem + " no tiene registro de QC.", "validacion pendientes");
                            } else {
                                enviarTabla(email, respuesta, "Estado QC de Bloque #" + idItem, HtmlBuilder.HEADERS_VALIDACION, Collections.singletonList(val));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, validacionService.getComandosValidacion());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'validacion'.", validacionService.getComandosValidacion());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'validacion'.", validacionService.getComandosValidacion());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, validacionService.getComandosValidacion());
                }
            }

            // ============================================================
            // CU6: GESTIÓN DE PAUTA PUBLICITARIA Y SPOTS
            // ============================================================

            @Override
            public void cliente(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = clienteService.listarClientes();
                            enviarTabla(email, respuesta, "CU6 - Clientes / Anunciantes", HtmlBuilder.HEADERS_CLIENTE, list);
                            break;
                        case Token.AGREGAR:
                            clienteService.crearCliente(event.getParams());
                            List<String[]> listAdd = clienteService.listarClientes();
                            enviarTabla(email, respuesta, "Cliente publicitario registrado", HtmlBuilder.HEADERS_CLIENTE, listAdd);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del cliente a consultar. Uso: cliente consultar id", "cliente consultar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            String[] cli = clienteService.consultarCliente(id);
                            if (cli == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Cliente No Encontrado", "No existe cliente con ID " + id, "cliente listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Cliente #" + id, HtmlBuilder.HEADERS_CLIENTE, Collections.singletonList(cli));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, clienteService.getComandosCliente());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'cliente'.", clienteService.getComandosCliente());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'cliente'.", clienteService.getComandosCliente());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, clienteService.getComandosCliente());
                }
            }

            @Override
            public void spot(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = spotService.listarSpots();
                            enviarTabla(email, respuesta, "CU6 - Spots Publicitarios", HtmlBuilder.HEADERS_SPOT, list);
                            break;
                        case Token.AGREGAR:
                            spotService.crearSpot(event.getParams());
                            List<String[]> listAdd = spotService.listarSpots();
                            enviarTabla(email, respuesta, "Spot comercial registrado", HtmlBuilder.HEADERS_SPOT, listAdd);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del spot a consultar. Uso: spot consultar id", "spot consultar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            String[] sp = spotService.consultarSpot(id);
                            if (sp == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Spot No Encontrado", "No existe spot con ID " + id, "spot listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Spot #" + id, HtmlBuilder.HEADERS_SPOT, Collections.singletonList(sp));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, spotService.getComandosSpot());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'spot'.", spotService.getComandosSpot());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'spot'.", spotService.getComandosSpot());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, spotService.getComandosSpot());
                }
            }

            @Override
            public void pauta(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            if (!event.getParams().isEmpty()) {
                                int idItem = Integer.parseInt(event.getParams().get(0).trim());
                                List<String[]> listItem = pautaService.listarPorItem(idItem);
                                enviarTabla(email, respuesta, "CU6 - Pautas de Bloque #" + idItem, HtmlBuilder.HEADERS_PAUTA, listItem);
                            } else {
                                List<String[]> list = pautaService.listarPautas();
                                enviarTabla(email, respuesta, "CU6 - Pautas Comerciales Programadas", HtmlBuilder.HEADERS_PAUTA, list);
                            }
                            break;
                        case Token.LISTAR_ITEM:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque de parrilla. Uso: pauta listar_item id_item_parrilla", "pauta listar_item 1");
                            int idItem = Integer.parseInt(event.getParams().get(0).trim());
                            List<String[]> listItem = pautaService.listarPorItem(idItem);
                            enviarTabla(email, respuesta, "Pautas de Bloque #" + idItem, HtmlBuilder.HEADERS_PAUTA, listItem);
                            break;
                        case Token.INSERTAR:
                        case Token.AGREGAR:
                            pautaService.insertarPauta(event.getParams());
                            auditoriaService.registrarAuditoria(1, "pauta_comercial", "INSERT", "Pauta insertada: " + event.getParams());
                            List<String[]> listAdd = pautaService.listarPautas();
                            enviarTabla(email, respuesta, "Spot insertado en tanda comercial", HtmlBuilder.HEADERS_PAUTA, listAdd);
                            break;
                        case Token.CAMBIAR_ESTADO:
                            pautaService.cambiarEstado(event.getParams());
                            enviarExito(email, respuesta, "Estado de Pauta Modificado", "Pauta comercial actualizada correctamente.");
                            break;
                        case Token.ELIMINAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID de la pauta a eliminar. Uso: pauta eliminar id_pauta", "pauta eliminar 1");
                            int id = Integer.parseInt(event.getParams().get(0).trim());
                            pautaService.eliminarPauta(id);
                            List<String[]> listDel = pautaService.listarPautas();
                            enviarTabla(email, respuesta, "Pauta comercial eliminada", HtmlBuilder.HEADERS_PAUTA, listDel);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID de la pauta a consultar. Uso: pauta consultar id_pauta", "pauta consultar 1");
                            int idCons = Integer.parseInt(event.getParams().get(0).trim());
                            String[] pautaData = pautaService.consultarPauta(idCons);
                            if (pautaData == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Pauta No Encontrada", "No existe pauta comercial con ID " + idCons, "pauta listar");
                            } else {
                                enviarTabla(email, respuesta, "Detalle de Pauta Comercial #" + idCons, HtmlBuilder.HEADERS_PAUTA, Collections.singletonList(pautaData));
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, pautaService.getComandosPauta());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'pauta'.", pautaService.getComandosPauta());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'pauta'.", pautaService.getComandosPauta());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, pautaService.getComandosPauta());
                }
            }

            // ============================================================
            // CU7: GESTIÓN DE CONCILIAR EMISIÓN REAL Y AUDITORÍA
            // ============================================================

            @Override
            public void emisionReal(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = emisionRealService.listarEmisiones();
                            enviarTabla(email, respuesta, "CU7 - Bitácora de Emisión Real (As-Run Log)", HtmlBuilder.HEADERS_EMISION_REAL, list);
                            break;
                        case Token.CONCILIAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque a conciliar. Uso: emision conciliar id_item", "emision conciliar 1");
                            emisionRealService.conciliarEmision(event.getParams());
                            auditoriaService.registrarAuditoria(1, "emision_real_log", "CONCILIAR", "Emisión conciliada: " + event.getParams());
                            List<String[]> listConc = emisionRealService.listarEmisiones();
                            enviarTabla(email, respuesta, "Emisión Conciliada y Registrada en Bitácora", HtmlBuilder.HEADERS_EMISION_REAL, listConc);
                            break;
                        case Token.DISCREPANCIAS:
                            List<String[]> listDisc = emisionRealService.listarDiscrepancias();
                            enviarTabla(email, respuesta, "CU7 - Discrepancias y Desplazamientos de Emisión", HtmlBuilder.HEADERS_DISCREPANCIAS, listDisc);
                            break;
                        case Token.REGISTRAR:
                            emisionRealService.registrarEmisionReal(event.getParams());
                            auditoriaService.registrarAuditoria(1, "emision_real_log", "INSERT", "Emisión As-Run: " + event.getParams());
                            List<String[]> listReg = emisionRealService.listarEmisiones();
                            enviarTabla(email, respuesta, "Emisión Real Registrada en Bitácora", HtmlBuilder.HEADERS_EMISION_REAL, listReg);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del bloque a consultar log. Uso: emision consultar id_item_parrilla", "emision consultar 1");
                            int idItem = Integer.parseInt(event.getParams().get(0).trim());
                            String[] em = emisionRealService.consultarPorItem(idItem);
                            if (em == null) {
                                enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Sin Registro As-Run", "No existe log para bloque #" + idItem, "emision listar");
                            } else {
                                enviarTabla(email, respuesta, "Bitácora As-Run de Bloque #" + idItem, HtmlBuilder.HEADERS_EMISION_REAL, Collections.singletonList(em));
                            }
                            break;
                        case Token.AL_AIRE:
                            String[] emAlAire = emisionRealService.consultarAlAire();
                            if (emAlAire != null) {
                                enviarTabla(email, respuesta, "Emisión en Curso / Último Evento al Aire (As-Run Log)", HtmlBuilder.HEADERS_EMISION_REAL, Collections.singletonList(emAlAire));
                            } else {
                                String[] fallback = itemParrillaService.consultarAlAire();
                                if (fallback != null) {
                                    enviarTabla(email, respuesta, "Programa Programado al Aire", HtmlBuilder.HEADERS_ITEM_PARRILLA, Collections.singletonList(fallback));
                                } else {
                                    enviarErrorDetallado(email, respuesta, ErrorCode.ERR_NEG_001, "Sin Registro", "No hay eventos registrados al aire actualmente.", "emision listar");
                                }
                            }
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, emisionRealService.getComandosEmision());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'emision'.", emisionRealService.getComandosEmision());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'emision'.", emisionRealService.getComandosEmision());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, emisionRealService.getComandosEmision());
                }
            }

            @Override
            public void auditoria(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.LISTAR:
                            List<String[]> list = auditoriaService.listarAuditoria();
                            enviarTabla(email, respuesta, "CU7 - Registro de Auditoría del Sistema", HtmlBuilder.HEADERS_AUDITORIA, list);
                            break;
                        case Token.CONSULTAR:
                            if (event.getParams().isEmpty()) throw new CommandException(ErrorCode.ERR_INC_002, "Falta el ID del usuario a consultar auditoría. Uso: auditoria consultar id_usuario", "auditoria consultar 1");
                            int idUser = Integer.parseInt(event.getParams().get(0).trim());
                            List<String[]> listUser = auditoriaService.listarPorUsuario(idUser);
                            enviarTabla(email, respuesta, "Auditoría de Usuario #" + idUser, HtmlBuilder.HEADERS_AUDITORIA, listUser);
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, auditoriaService.getComandosAuditoria());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese la acción a ejecutar sobre 'auditoria'.", auditoriaService.getComandosAuditoria());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Acción no reconocida para 'auditoria'.", auditoriaService.getComandosAuditoria());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, auditoriaService.getComandosAuditoria());
                }
            }

            // ============================================================
            // CU8: REPORTES Y ESTADÍSTICAS
            // ============================================================

            @Override
            public void reporte(TokenEvent event) {
                try {
                    switch (event.getAction()) {
                        case Token.GENERAL:
                            List<String[]> rGral = reporteService.generarReporteGeneral();
                            enviarTabla(email, respuesta, "CU8 - Reporte Estadístico Consolidado del Canal", HtmlBuilder.HEADERS_REPORTE_GENERAL, rGral);
                            break;
                        case Token.PARRILLA_ACT:
                            List<String[]> rParr = reporteService.generarReporteParrilla();
                            enviarTabla(email, respuesta, "CU8 - Reporte de Programación y Parrilla", HtmlBuilder.HEADERS_REPORTE_PARRILLA, rParr);
                            break;
                        case Token.PROGRAMAS:
                            List<String[]> rProg = reporteService.generarReporteProgramas();
                            enviarTabla(email, respuesta, "CU8 - Reporte de Catálogo por Categorías", HtmlBuilder.HEADERS_REPORTE_PROGRAMAS, rProg);
                            break;
                        case Token.PAUTAS:
                            List<String[]> rPaut = reporteService.generarReportePautas();
                            enviarTabla(email, respuesta, "CU8 - Certificación y Facturación de Pautas", HtmlBuilder.HEADERS_REPORTE_PAUTAS, rPaut);
                            break;
                        case Token.CUMPLIMIENTO:
                            List<String[]> rCump = reporteService.generarReporteCumplimiento();
                            enviarTabla(email, respuesta, "CU8 - Reporte de Cumplimiento de Emisión al Aire", HtmlBuilder.HEADERS_REPORTE_CUMPLIMIENTO, rCump);
                            break;
                        case Token.HELP:
                            mostrarYEnviarHELP(email, respuesta, reporteService.getComandosReporte());
                            break;
                        default:
                            if (event.getAction() == 0) {
                                throw new CommandException(ErrorCode.ERR_INC_001, "Orden incompleta: Ingrese el tipo de reporte. Opciones: general, parrilla, programas, pautas, cumplimiento.", reporteService.getComandosReporte());
                            } else {
                                throw new CommandException(ErrorCode.ERR_CMD_003, "Tipo de reporte no reconocido.", reporteService.getComandosReporte());
                            }
                    }
                } catch (Exception e) {
                    manejarExcepcionComando(email, respuesta, e, reporteService.getComandosReporte());
                }
            }

            // ============================================================
            // ============================================================
            // MANEJO GENERAL DE ERRORES SINTÁCTICOS
            // ============================================================

            @Override
            public void error(TokenEvent event) {
                String cmd = (email != null && email.getSubject() != null) ? email.getSubject().trim() : "";
                String codigo = event.getErrorCode() != null ? event.getErrorCode() : ErrorCode.ERR_CMD_002;
                String titulo = ErrorCode.getDescripcionCorta(codigo);
                String detalle;
                String sugerencia = event.getSuggestion() != null && !event.getSuggestion().isEmpty()
                        ? event.getSuggestion()
                        : sugerirComando(cmd);

                if (!event.getParams().isEmpty() && event.getParams().get(0) != null && !event.getParams().get(0).trim().isEmpty()) {
                    detalle = event.getParams().get(0);
                } else if (cmd.isEmpty()) {
                    codigo = ErrorCode.ERR_CMD_001;
                    titulo = ErrorCode.getDescripcionCorta(codigo);
                    detalle = "El asunto (Subject) del correo electrónico está vacío. Por favor ingrese el comando en el asunto.";
                } else {
                    detalle = "La estructura del comando '" + cmd + "' no coincide con ninguna entidad o acción válida del sistema.";
                }
                enviarErrorDetallado(email, respuesta, codigo, titulo, detalle, sugerencia);
            }

            @Override
            public void posibleserrores(TokenEvent event) {
                String cmd = (email != null && email.getSubject() != null) ? email.getSubject().trim() : "";
                String codigo = event.getErrorCode() != null ? event.getErrorCode() : ErrorCode.ERR_INC_002;
                String titulo = ErrorCode.getDescripcionCorta(codigo);
                String sugerencia = event.getSuggestion() != null && !event.getSuggestion().isEmpty()
                        ? event.getSuggestion()
                        : sugerirComando(cmd);
                String detalle = (!event.getParams().isEmpty() && event.getParams().get(0) != null)
                        ? event.getParams().get(0)
                        : "Los parámetros de '" + cmd + "' están incompletos, tienen formato inválido o faltan campos obligatorios.";
                enviarErrorDetallado(email, respuesta, codigo, titulo, detalle, sugerencia);
            }
        });

        // Ejecutar interpretación sintáctica
        interpreter.run();
    }

    private static void manejarExcepcionComando(Email email, SendEmail respuesta, Exception e, String fallbackHelp) {
        if (e instanceof CommandException) {
            CommandException ce = (CommandException) e;
            enviarErrorDetallado(email, respuesta, ce.getErrorCode(), ErrorCode.getDescripcionCorta(ce.getErrorCode()), ce.getMessage(), ce.getSuggestion());
            return;
        }
        if (e instanceof NumberFormatException) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            enviarErrorDetallado(email, respuesta, ErrorCode.ERR_FMT_001, "Formato Numérico Inválido",
                    "Se esperaba un número entero (ID numérico o cantidad), pero se ingresó un valor no numérico: " + msg,
                    fallbackHelp);
            return;
        }
        if (e instanceof IllegalArgumentException) {
            String msg = e.getMessage() != null ? e.getMessage() : "Parámetros inválidos.";
            String codigo = parseIllegalArgumentErrorCode(msg);
            enviarErrorDetallado(email, respuesta, codigo, ErrorCode.getDescripcionCorta(codigo), msg, fallbackHelp);
            return;
        }
        if (e instanceof SQLException) {
            SQLException sqle = (SQLException) e;
            String codigo = parseSqlErrorCode(sqle);
            String msg = parseErrorMessage(sqle);
            enviarErrorDetallado(email, respuesta, codigo, ErrorCode.getDescripcionCorta(codigo), msg, fallbackHelp);
            return;
        }
        enviarErrorDetallado(email, respuesta, ErrorCode.ERR_SYS_001, "Error en Operación", parseErrorMessage(e), fallbackHelp);
    }

    private static String parseSqlErrorCode(SQLException e) {
        if (e == null || e.getMessage() == null) return ErrorCode.ERR_BD_005;
        String m = e.getMessage().toLowerCase();
        if (m.contains("foreign key") || m.contains("violates foreign key constraint") || m.contains("is not present in table")) {
            return ErrorCode.ERR_BD_002;
        }
        if (m.contains("duplicate key") || m.contains("unique constraint") || m.contains("ya existe")) {
            return ErrorCode.ERR_BD_003;
        }
        if (m.contains("cannot delete") || m.contains("referenced from table") || m.contains("still referenced")) {
            return ErrorCode.ERR_BD_004;
        }
        if (m.contains("not-null") || m.contains("null value in column")) {
            return ErrorCode.ERR_INC_003;
        }
        return ErrorCode.ERR_BD_005;
    }

    private static String parseIllegalArgumentErrorCode(String msg) {
        if (msg == null) return ErrorCode.ERR_INC_002;
        String m = msg.toLowerCase();
        if (m.contains("fecha") || m.contains("yyyy-mm-dd")) {
            return ErrorCode.ERR_FMT_002;
        }
        if (m.contains("hora") || m.contains("hh:mm")) {
            return ErrorCode.ERR_FMT_003;
        }
        if (m.contains("estado")) {
            return ErrorCode.ERR_FMT_004;
        }
        if (m.contains("faltan parámetros") || m.contains("al menos") || m.contains("insuficientes")) {
            return ErrorCode.ERR_INC_003;
        }
        if (m.contains("uso:") || m.contains("formato:") || m.contains("falta")) {
            return ErrorCode.ERR_INC_002;
        }
        return ErrorCode.ERR_INC_002;
    }

    private static String parseErrorMessage(Exception e) {
        if (e == null) return "Error desconocido al procesar la solicitud.";
        if (e instanceof NumberFormatException) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            return "Se esperaba un número entero (ID numérico o cantidad), pero se ingresó un valor no numérico: " + msg;
        }
        if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        }
        if (e instanceof java.sql.SQLException) {
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("violates foreign key constraint") || msg.contains("is not present in table")) {
                    return "Error de referencia: El identificador (ID) relacionado no existe en la base de datos.";
                }
                if (msg.contains("duplicate key value") || msg.contains("unique constraint")) {
                    return "Error de duplicidad: Ya existe un registro con ese mismo código, identificador o correo electrónico.";
                }
                if (msg.contains("syntax error at or near")) {
                    return "Error de sintaxis SQL al procesar los datos enviados.";
                }
                return "Error en la base de datos: " + msg;
            }
        }
        return e.getMessage() != null && !e.getMessage().trim().isEmpty() ? e.getMessage() : e.toString();
    }

    private static String sugerirComando(String cmd) {
        if (cmd == null || cmd.trim().isEmpty()) {
            return "Comandos más frecuentes de Playout:\n- parrilla al_aire\n- parrilla hoy\n- programa listar\n- pauta listar\n- emision al_aire\n- help (manual completo)";
        }
        String c = java.text.Normalizer.normalize(cmd.toLowerCase().trim(), java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        if (c.contains("usu") || c.contains("user")) {
            return "Comandos para Usuarios:\n- usuario listar\n- usuario consultar 1\n- usuario agregar Nombre,Apellidos,email,pass,Rol\n- usuario eliminar id";
        }
        if (c.contains("rol")) {
            return "Comandos para Roles:\n- rol listar\n- rol agregar Nombre,Descripcion";
        }
        if (c.contains("prog") || c.contains("show")) {
            return "Comandos para Programas:\n- programa listar\n- programa consultar 1\n- programa agregar codigo,titulo,duracion_min\n- programa eliminar id";
        }
        if (c.contains("cat") || c.contains("gen")) {
            return "Comandos para Categorías:\n- categoria listar\n- categoria agregar Nombre,Descripcion";
        }
        if (c.contains("parr") || c.contains("gril")) {
            return "Comandos para Parrilla (Playout):\n- parrilla al_aire (programa al aire)\n- parrilla hoy (escaleta del día)\n- parrilla listar\n- parrilla crear YYYY-MM-DD\n- parrilla estado id,APROBADA";
        }
        if (c.contains("item") || c.contains("bloq") || c.contains("play")) {
            return "Comandos para Bloques de Parrilla:\n- item_parrilla al_aire\n- item_parrilla listar [id_parrilla]\n- item_parrilla agregar id_parrilla,id_prog,inicio,fin,tipo\n- item_parrilla eliminar id";
        }
        if (c.contains("mat") || c.contains("vid") || c.contains("clip") || c.contains("mam")) {
            return "Comandos para Materiales MAM:\n- material listar\n- material consultar 1\n- material agregar id_programa,codigo,titulo,duracion_seg\n- material estado id,LISTO";
        }
        if (c.contains("val") || c.contains("qc") || c.contains("calidad")) {
            return "Comandos para Control de Calidad (QC):\n- validacion pendientes\n- validacion listar\n- validacion aprobar id_item\n- validacion rechazar id_item,motivo";
        }
        if (c.contains("cli") || c.contains("anun")) {
            return "Comandos para Clientes/Anunciantes:\n- cliente listar\n- cliente consultar 1\n- cliente agregar RazonSocial,NIT,Contacto,Email,Telefono";
        }
        if (c.contains("spot") || c.contains("pub")) {
            return "Comandos para Spots Comerciales:\n- spot listar\n- spot consultar 1\n- spot agregar id_cliente,codigo,titulo,duracion_seg";
        }
        if (c.contains("pau") || c.contains("cort") || c.contains("tand")) {
            return "Comandos para Pauta y Cortes (Playout):\n- pauta listar\n- pauta listar_item id_item\n- pauta agregar id_item,id_spot,tanda,orden\n- pauta estado id,EMITIDO\n- pauta eliminar id";
        }
        if (c.contains("emi") || c.contains("aire") || c.contains("asrun")) {
            return "Comandos para Emisión Real (As-Run Log):\n- emision al_aire\n- emision listar\n- emision discrepancias\n- emision conciliar id_item";
        }
        if (c.contains("aud") || c.contains("log")) {
            return "Comandos para Auditoría:\n- auditoria listar\n- auditoria consultar id_usuario";
        }
        if (c.contains("rep") || c.contains("est") || c.contains("metr")) {
            return "Comandos para Reportes:\n- reporte general\n- reporte parrilla\n- reporte programas\n- reporte pautas\n- reporte cumplimiento";
        }
        return "Comandos recomendados de Playout:\n- parrilla al_aire\n- parrilla hoy\n- programa listar\n- pauta listar\n- emision al_aire\n- help (para ver el manual completo)";
    }

    // ============================================================
    // MÉTODOS DE ENVÍO Y COMUNICACIÓN
    // ============================================================

    private static void enviarTabla(Email email, SendEmail respuesta, String titulo, String[] headers, List<String[]> filas) {
        String html = HtmlBuilder.generarTabla(titulo, headers, filas);
        mostraRespuesta(html);
        if (email.getFrom() != null && !email.getFrom().trim().isEmpty()) {
            respuesta.sendmail(email.getFrom(), titulo, html);
        }
        System.out.println("[RESPUESTA TABLA] '" + titulo + "' (" + (filas != null ? filas.size() : 0) + " registros) -> " + email.getFrom());
    }

    private static void enviarExito(Email email, SendEmail respuesta, String titulo, String mensaje) {
        String html = HtmlBuilder.generarMensajeExito(titulo, mensaje);
        mostraRespuesta(html);
        if (email.getFrom() != null && !email.getFrom().trim().isEmpty()) {
            respuesta.sendmail(email.getFrom(), titulo, html);
        }
        System.out.println("[RESPUESTA EXITO] '" + titulo + "' -> " + email.getFrom() + " | " + mensaje);
    }

    public static void enviarErrorDetallado(Email email, SendEmail respuesta, String codigoError, String titulo, String queFallo, String sugerencia) {
        String comandoRecibido = email != null && email.getSubject() != null ? email.getSubject() : "";
        String html = HtmlBuilder.generarMensajeErrorDetallado(codigoError, titulo, comandoRecibido, queFallo, sugerencia);
        mostraRespuesta(html);
        if (email.getFrom() != null && !email.getFrom().trim().isEmpty()) {
            String subject = "[" + codigoError + "] ERROR: " + titulo;
            respuesta.sendmail(email.getFrom(), subject, html);
        }
        System.err.println("[RESPUESTA ERROR " + codigoError + "] '" + titulo + "' -> " + email.getFrom() + " | " + queFallo);
    }

    private static void enviarErrorDetallado(Email email, SendEmail respuesta, String titulo, String queFallo, String sugerencia) {
        enviarErrorDetallado(email, respuesta, ErrorCode.ERR_SYS_001, titulo, queFallo, sugerencia);
    }

    private static void mostrarYEnviarHELP(Email email, SendEmail respuesta, String menuAyuda) {
        String html = HtmlBuilder.generarMenuAyudaHtml(menuAyuda);
        mostraRespuesta(html);
        if (email.getFrom() != null && !email.getFrom().trim().isEmpty()) {
            respuesta.sendmail(email.getFrom(), "AYUDA - Sistema de Parrilla de Televisión", html);
        }
        System.out.println("[RESPUESTA HELP] Enviada a -> " + email.getFrom());
    }

    public static void mostraRespuesta(String html) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (Principal.ta_respuesta != null) {
                    Principal.ta_respuesta.setContentType("text/html");
                    Principal.ta_respuesta.setText(html);
                }
            } catch (Exception ignored) {
            }
        });
    }

    private static String getMenuGeneralAyuda() {
        return "========================================================================\n" +
               " GUIA RAPIDA DE COMANDOS - SISTEMA DE PARRILLA DE TELEVISION (8 CASOS)\n" +
               "               FICCT - UAGRM | GRUPO 12 SC                              \n" +
               "========================================================================\n\n" +
               "Enviar el comando en el ASUNTO (Subject) del correo electronico:\n\n" +
               "CU1. GESTION DE USUARIOS:\n" +
               "  - usuario listar\n" +
               "  - usuario consultar 1\n" +
               "  - usuario agregar Carlos,Perez,carlos@canal.com,123,Programador\n" +
               "  - usuario eliminar 4\n" +
               "  - rol listar\n\n" +
               "CU2. GESTION DE CATALOGO DE PROGRAMAS:\n" +
               "  - programa listar\n" +
               "  - programa consultar 1\n" +
               "  - programa agregar PROG-10,MasterChef,45\n" +
               "  - programa eliminar 3\n" +
               "  - categoria listar\n\n" +
               "CU3. GESTION DE CONFECCIONAR Y CONTROLAR LA PARRILLA:\n" +
               "  - parrilla listar\n" +
               "  - parrilla crear 2026-10-15\n" +
               "  - parrilla cambiar_estado 1,APROBADA\n" +
               "  - item listar\n" +
               "  - item agregar 1,1,14:00,15:00\n" +
               "  - item eliminar 5\n\n" +
               "CU4. GESTION DE MATERIALES DE VIDEO (MAM):\n" +
               "  - material listar\n" +
               "  - material consultar 1\n" +
               "  - material agregar 1,CLIP-05,Episodio 5,1800\n" +
               "  - material estado 1,LISTO\n\n" +
               "CU5. GESTION DE VALIDAR ESTADO DE EMISION (QC):\n" +
               "  - validacion listar\n" +
               "  - validacion pendientes\n" +
               "  - validacion aprobar 1\n" +
               "  - validacion rechazar 1,Audio desfasado\n" +
               "  - validacion consultar 1\n\n" +
               "CU6. GESTION DE PAUTA PUBLICITARIA Y SPOTS:\n" +
               "  - pauta listar\n" +
               "  - spot listar\n" +
               "  - cliente listar\n" +
               "  - cliente agregar Entel,1020304050\n" +
               "  - spot agregar 1,SPOT-03,Promo Verano,30\n" +
               "  - pauta agregar 1,1,CORTE_1,150.00\n" +
               "  - pauta estado 1,EMITIDO\n\n" +
               "CU7. GESTION DE CONCILIAR EMISION REAL Y AUDITORIA:\n" +
               "  - emision listar\n" +
               "  - emision conciliar 1\n" +
               "  - emision discrepancias\n" +
               "  - auditoria listar\n" +
               "  - auditoria consultar 1\n\n" +
               "CU8. REPORTES Y ESTADISTICAS:\n" +
               "  - reporte general\n" +
               "  - reporte parrilla\n" +
               "  - reporte programas\n" +
               "  - reporte pautas\n" +
               "  - reporte cumplimiento\n" +
               "========================================================================";
    }
}
