package com.movieapp.movie_api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final int TOKEN_EXPIRATION_TIME = 1000 * 60 * 15; // 15 minutes
    private static final int PASSWORD_RESET_TOKEN_EXPIRATION_TIME = 1000 * 60 * 3; //3 minutos

    public JwtService(){
    }

    /**
     *  Recibe un objeto de tipo UserDetails para generar el JwtToken de autenticación de
     *  15 minutos de duración.
     * @param userDetails
     * @return String con el JwtToken.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .issuer("Movie API")
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .id(UUID.randomUUID().toString())
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Recibe el email del usuario para la generacion de un JwtToken de corta duración
     * con el cual realizar el cambio de contraseña de usuario.
     * @param email
     * @return String - JwtToken con 3 minutos de validez.
     */
    public String generatePasswordResetToken(String email){
        return Jwts.builder()
                .issuer("Movie API")
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + PASSWORD_RESET_TOKEN_EXPIRATION_TIME))
                .id(UUID.randomUUID().toString())
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Rebibe un String con el JwtToken y el UserDetails del usuario autenticado para la
     * validación del mismo.
     * @param token
     * @param userDetails
     * @return boolean - Retorna true al validar el token o false en caso de datos del
     * userDetails que nop concuerden con los incluidos en el token o en caso de que el
     * token se encuentre expirado.
     * @exception JwtException en caso de no validar la firma.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        try {
            final String username = extractUsername(token);

            return (username.equals(userDetails.getUsername()) && !isTokenExpirated(token));
        } catch (JwtException e){
            throw new JwtException("Error en la  verificacion de la firma del token.");
        }
    }

    /**
     * Valida la expiración del JwtToken recibido como String.
     * @param token
     * @return boolean - true si el token esta vigente.
     */
    public boolean isTokenExpirated(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Recibe un String con el JwtToken para extraer el Username.
     * @param token
     * @return String
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Recibe un String con el JwtToken para extraer la expiración.
     *
     * @param token
     * @return Date
     */
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     *  Extrae un valor específico (claim) de un token JWT.
     *
     * @param <T> Tipo de dato del claim a extraer.
     * @param token JwtToken
     * @param claimsResolver Función que recibe los claims del JwtToken y el nombre del claim especifico
     *                       a buscar
     * @return El valor del claim extraído de tipo T, según lo que resuelva la función `claimsResolver`.
     * @throws JwtException Si el token es inválido o no se pueden extraer los claims.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los Claims de un JwtToken.
     *
     * @param token
     * @return un objeto del Tipo Claims con un Map con el nombre de cada valor y su valor.
     * @throws JwtException al no poder verificar la firma del JwtToken.
     */
    private Claims extractAllClaims(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;
        } catch (JwtException e){
            throw new JwtException("Error en la  verificacion de la firma del token.");
        }
    }

    /**
     * genera una clave de firma compatible con JWTs usando HMAC SHA, a partir de una
     * clave secreta codificada en Base64.
     *
     * @return Una instancia de la interfaz Key
     * @throws JwtException puede lanzar una excepción en caso de algun problema al
     *                      decodificar o generar la clave.
     */
    private Key getSignInKey(){
        try {
            byte[] keyBytes = Decoders.BASE64.decode((CharSequence) SECRET_KEY);

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (JwtException e){
            throw new JwtException("Error en la  verificación de la firma del token.");
        }
    }

}
