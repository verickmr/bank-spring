package org.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.model.Correntista;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "bank-api";
    private static final int MINIMUM_SECRET_BYTES = 32;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        validarSecret(secret);
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        this.expirationMinutes = expirationMinutes;
    }

    private void validarSecret(String secret) {
        if (secret == null
                || secret.getBytes(StandardCharsets.UTF_8).length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET deve possuir pelo menos 32 bytes."
            );
        }
    }

    public String gerarToken(Correntista correntista) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(correntista.getCpf())
                .withClaim("correntistaId", correntista.getId())
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    public String validarEObterCpf(String token) {
        return verifier.verify(token).getSubject();
    }
}
