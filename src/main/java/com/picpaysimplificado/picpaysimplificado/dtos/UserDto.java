package com.picpaysimplificado.picpaysimplificado.dtos;

import java.math.BigDecimal;

import com.picpaysimplificado.picpaysimplificado.domain.user.UserType;

public record UserDto(
    String firstName,
    String lastName,
    String document,
    BigDecimal balance,
    String email,
    String password,
    UserType userType
) {
    
}
