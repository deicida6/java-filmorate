package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


/**
 * User.
 */
@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    Long id;
    @Email
    String email;
    @NotBlank(message = "Login не должен быть пустым")
    String login;
    String name;
    LocalDate birthday;
}
