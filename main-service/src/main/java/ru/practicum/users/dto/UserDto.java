package ru.practicum.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;

    @Length(min = 2, max = 250)
    @NotBlank
    private String name;
}
