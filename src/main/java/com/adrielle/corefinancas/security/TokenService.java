package com.adrielle.corefinancas.security;

import com.adrielle.corefinancas.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

    // O Spring vai buscar aquela senha que colocamos no application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${app.timezone:America/Sao_Paulo}")
    private String timezone;

    public String generateToken(User user) {
        try {
            // O algoritmo de criptografia que vai assinar o nosso crachá
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            return JWT.create()
                    .withIssuer("core-financas-api") // Quem emitiu o token
                    .withSubject(user.getId().toString()) // O "dono" do token (estamos guardando o UUID dele aqui!)
                    .withExpiresAt(genExpirationDate()) // Quando o token vence
                    .sign(algorithm);
                    
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    // NOVO MÉTODO: Valida o token e extrai o ID do usuário
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("core-financas-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Pega aquele UUID que guardamos no subject!
        } catch (JWTVerificationException exception) {
            // Se o token for falso, expirado ou alterado, ele cai aqui e devolve vazio
            return ""; 
        }
    }
    
    // Define que o token tem validade de 2 horas
    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).atZone(ZoneId.of(timezone)).toInstant();
    }
}