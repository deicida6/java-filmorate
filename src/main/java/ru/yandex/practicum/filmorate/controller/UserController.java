package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
