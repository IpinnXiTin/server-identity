package com.ipin.identity.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipin.identity.dto.request.AuthenticationRequest;
import com.ipin.identity.dto.request.IntrospectRequest;
import com.ipin.identity.dto.request.LogoutRequest;
import com.ipin.identity.dto.request.RefreshRequest;
import com.ipin.identity.dto.response.AuthenticationResponse;
import com.ipin.identity.dto.response.IntrospectResponse;
import com.ipin.identity.entity.InvalidatedToken;
import com.ipin.identity.entity.User;
import com.ipin.identity.exception.AppException;
import com.ipin.identity.exception.ErrorCode;
import com.ipin.identity.repository.InvalidatedTokenRepository;
import com.ipin.identity.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.signerKey}")
    String signerKey;

    @Value("${jwt.valid-duration}")
    long validDuration;

    @Value("${jwt.refreshable-duration}")
    long refreshableDuration;

    public AuthenticationService(UserRepository userRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authenticated = passwordEncoder.matches(request.getPassWord(), user.getPassWord());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        String token = "";
        try {
            token = generateToken(user);
        }
        catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return AuthenticationResponse.builder()
            .token(token)
            .build();
    }

    public String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUserName())
            .issuer("ipin")
            .issueTime(new Date())
            .expirationTime(new Date(
                Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()
            ))
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", getRoles(user))
            .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(signerKey.getBytes()));
        return jwsObject.serialize();
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);
        
        var verified = signedJWT.verify(verifier);

        var expiryTime = isRefresh 
            ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().now().plus(refreshableDuration, ChronoUnit.SECONDS).toEpochMilli())
            : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        String token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        }
        catch(AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
            .authenticated(isValid)
            .build();
    }

    public List<String> getRoles(User user) {
        return user.getRoles().stream()
            .map(role -> role.getRoleName())
            .collect(Collectors.toList());
    }

    public void logout(LogoutRequest request) 
            throws ParseException, JOSEException {
        var token = request.getToken();

        try {
            var signedToken = verifyToken(token, true);

            String jwtId = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime((expiryTime))
                .build();
            
            invalidatedTokenRepository.save(invalidatedToken);
        }
        catch(AppException e) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws JOSEException, ParseException {
        var signedJWT = verifyToken(request.getToken(), true);

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
            .id(jwtId)
            .expiryTime((expiryTime))
            .build();
        
        invalidatedTokenRepository.save(invalidatedToken);

        String userName = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUserName
        (userName).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                    .token(token)
                    .build();
    }
}
