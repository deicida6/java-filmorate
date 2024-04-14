package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.validation.AfterDate;

import java.time.LocalDate;


/**
 * User.
 */
@Data
@Builder
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank(message = "Login не должен быть пустым")
    private String login;
    private String name;
    @AfterDate
    private LocalDate birthday;
}

