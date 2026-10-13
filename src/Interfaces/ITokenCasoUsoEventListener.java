package Interfaces;

import Comunication.TokenEvent;

public interface ITokenCasoUsoEventListener {

    // CU1
    void usuario(TokenEvent event);
    void rol(TokenEvent event);

    // CU2
    void categoria(TokenEvent event);
    void programa(TokenEvent event);

    // CU3
    void parrilla(TokenEvent event);
    void itemParrilla(TokenEvent event);

    // CU4
    void materialVideo(TokenEvent event);

    // CU5
    void validacion(TokenEvent event);

    // CU6
    void cliente(TokenEvent event);
    void spot(TokenEvent event);
    void pauta(TokenEvent event);

    // CU7
    void emisionReal(TokenEvent event);
    void auditoria(TokenEvent event);

    // CU8
    void reporte(TokenEvent event);

    // Control de Errores
    void error(TokenEvent event);
    void posibleserrores(TokenEvent event);
}