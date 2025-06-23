package com.ipin.identity.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipin.identity.dto.request.UserCreationRequest;
import com.ipin.identity.dto.request.UserUpdateRequest;
import com.ipin.identity.dto.response.UserResponse;
import com.ipin.identity.entity.User;
import com.ipin.identity.exception.AppException;
import com.ipin.identity.exception.ErrorCode;
import com.ipin.identity.repository.RoleRepository;
import com.ipin.identity.repository.UserRepository;

@Service
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var roles = roleRepository.findAllById(request.getRoles());

        User user = User.builder()
                .userName(request.getUserName())
                .passWord(request.getPassWord())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dob(request.getDob())
                .roles(new HashSet<>(roles))
                .build();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));

        userRepository.save(user);

        return UserResponse.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .passWord(user.getPassWord())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .roles(user.getRoles())
            .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .passWord(user.getPassWord())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roles(user.getRoles())
                .build())
            .collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.userName == authentication.name")
    public UserResponse getUser(String userId) {
        return userRepository.findById(userId)
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .passWord(user.getPassWord())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roles(user.getRoles())
                .build()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        var user = userRepository.findByUserName(name)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .passWord(user.getPassWord())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .roles(user.getRoles())
            .build();
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        userRepository.save(user);

        return UserResponse.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .passWord(user.getPassWord())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .roles(user.getRoles())
            .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
