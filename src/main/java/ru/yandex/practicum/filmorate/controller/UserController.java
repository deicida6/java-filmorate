package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> userMap = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return userMap.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Указан не корректный имейл");
            throw new ValidationException("Имейл должен быть указан");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.info("Не указан логин");
            throw new ValidationException("Логин пустой");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Не корректно указана дата рождения, она не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Не задано имя пользователя, будет использован логин " + user.getLogin());
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        // сохраняем нового пользователя
        userMap.put(user.getId(), user);
        log.info("Создан пользователь " + user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.info("не указан Id при поиске пользователя");
            throw new ValidationException("Id должен быть указан");
        }
        if (userMap.containsKey(newUser.getId())) {
            User oldUser = userMap.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.info("Указан не корректный имейл");
                throw new ValidationException("Имейл должен быть указан");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
                log.info("Не указан логин");
                throw new ValidationException("Логин пустой");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.info("Не корректно указана дата рождения, она не может быть в будущем");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.info("Не задано имя пользователя, будет использован логин " + newUser.getLogin());
            }
            // если пользователь найден и все условия соблюдены, обновляем данные
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь обновлен" + newUser.getLogin());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
