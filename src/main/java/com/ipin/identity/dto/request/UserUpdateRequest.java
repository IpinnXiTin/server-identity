package com.ipin.identity.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.ipin.identity.validator.DobConstraint;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Size(min = 8, message = "INVALID_PASSWORD")
    String passWord;
    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
    List<String> roles;
}
