package com.ipin.identity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipin.identity.dto.request.UserCreationRequest;
import com.ipin.identity.dto.request.UserUpdateRequest;
import com.ipin.identity.dto.response.ApiResponse;
import com.ipin.identity.dto.response.UserResponse;
import com.ipin.identity.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        var user = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("User created successfully")
                .result(user)
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        var users = userService.getUsers();

        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Users fetched successfully")
                .result(users)
                .build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        var user = userService.getMyInfo();

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("My info fetched successfully")
                .result(user)
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        var user = userService.getUser(userId);

        return ApiResponse.<UserResponse>builder()
            .code(1000)
            .message("User fetched successfully")
            .result(user)
            .build();
    }
    
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UserUpdateRequest request) {
        var user = userService.updateUser(userId, request);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("User updated successfully")
                .result(user)
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("User deleted successfully")
                .build();
    }
}
