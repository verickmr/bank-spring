package org.example.service;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.example.model.Correntista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET =
            "segredo-de-teste-com-pelo-menos-trinta-e-dois-caracteres";

    @Test
    void deveGerarEValidarToken() {
        JwtService service = new JwtService(SECRET, 60);
        Correntista correntista = criarCorrentista();

        String token = service.gerarToken(correntista);

        assertEquals("12345678900", service.validarEObterCpf(token));
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {
        JwtService emissor = new JwtService(SECRET, 60);
        JwtService validador = new JwtService(
                "outro-segredo-de-teste-com-tamanho-suficiente-123",
                60
        );

        String token = emissor.gerarToken(criarCorrentista());

        assertThrows(
                SignatureVerificationException.class,
                () -> validador.validarEObterCpf(token)
        );
    }

    @Test
    void deveRejeitarTokenExpirado() {
        JwtService service = new JwtService(SECRET, -1);
        String token = service.gerarToken(criarCorrentista());

        assertThrows(
                TokenExpiredException.class,
                () -> service.validarEObterCpf(token)
        );
    }

    @Test
    void deveRejeitarSecretComMenosDeTrintaEDoisBytes() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new JwtService("segredo-curto", 60)
        );

        assertEquals(
                "JWT_SECRET deve possuir pelo menos 32 bytes.",
                exception.getMessage()
        );
    }

    private Correntista criarCorrentista() {
        return Correntista.builder()
                .id(1L)
                .cpf("12345678900")
                .build();
    }
}
