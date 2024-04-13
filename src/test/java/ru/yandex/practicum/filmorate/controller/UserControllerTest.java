package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController;
    User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = User.builder()
                .email("email@mail.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1994,12,13))
                .build();
    }

    @Test
    void testCreateWithEmptyEmail() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void testCreateWithEmptyLogin() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.create(user));

    }

    @Test
    void testCreateWithInvalidEmail() {
        user.setEmail("InvalidEmail");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void testCreateWithFutureBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

}