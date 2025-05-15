package com.vlz.authservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${spring.spring.security.jwt.private-key-location}")
    private Resource privateKeyResource;
    @Value("${spring.spring.security.jwt.public-key-location}")
    private Resource publicKeyResource;
    @Value("${spring.spring.security.jwt.expiration}")
    private long expiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String keyId;

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
        this.keyId = UUID.randomUUID().toString();
    }

    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        String pem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        byte[] decoded = decodePem(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey loadPublicKey(Resource resource) throws Exception {
        String pem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        byte[] decoded = decodePem(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public Map<String, Object> getJwkSet() {
        Map<String, Object> jwkSet = new HashMap<>();

        Map<String, Object> jwk = new HashMap<>();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        jwk.put("kty", "RSA");
        jwk.put("kid", keyId);
        jwk.put("use", "sig");
        jwk.put("alg", "RS256");

        byte[] modulus = rsaPublicKey.getModulus().toByteArray();
        if (modulus[0] == 0) {
            byte[] tmp = new byte[modulus.length - 1];
            System.arraycopy(modulus, 1, tmp, 0, tmp.length);
            modulus = tmp;
        }
        jwk.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(modulus));

        byte[] exponent = rsaPublicKey.getPublicExponent().toByteArray();
        jwk.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(exponent));

        jwkSet.put("keys", List.of(jwk));

        return jwkSet;
    }

    private byte[] decodePem(String pem) {
        String base64 = pem
                .replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s+", "");
        return Base64.getMimeDecoder().decode(base64);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}