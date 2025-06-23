package com.ipin.identity.controller;
 
import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipin.identity.dto.request.AuthenticationRequest;
import com.ipin.identity.dto.request.IntrospectRequest;
import com.ipin.identity.dto.request.LogoutRequest;
import com.ipin.identity.dto.request.RefreshRequest;
import com.ipin.identity.dto.response.ApiResponse;
import com.ipin.identity.dto.response.AuthenticationResponse;
import com.ipin.identity.dto.response.IntrospectResponse;
import com.ipin.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationController {
    AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var authenticated = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .message("Authentication success")
                .result(authenticated)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var authenticated = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .code(1000)
                .message("Introspect success")
                .result(authenticated)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<AuthenticationResponse> logout(@RequestBody LogoutRequest request) 
            throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .message("Logout token success")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) 
            throws ParseException, JOSEException {
        var refresh = authenticationService.refreshToken(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .code(1000)
                .message("Refresh token success")
                .result(refresh)
                .build();
    }
}
