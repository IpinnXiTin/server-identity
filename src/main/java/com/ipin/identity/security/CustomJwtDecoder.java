package com.ipin.identity.security;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.ipin.identity.dto.request.IntrospectRequest;
import com.ipin.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    String signerKey;

    AuthenticationService authenticationService;
    NimbusJwtDecoder nimbusJwtDecoder = null;

    public CustomJwtDecoder(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(
                new IntrospectRequest(token)
            );
            if (!response.isAuthenticated()) {
                throw new JwtException("Token invalid");
            }
        }
        catch(JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (!Objects.nonNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
    
}
