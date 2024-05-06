package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public void createUser(User user) {
        Set<Long> friends = new TreeSet<>();
        user.setFriends(friends);
        user.setId(getNextId());

        try {
            users.put(user.getId(), user);
        } catch (AlreadyExistsException e) {
            log.error("Ошибка добавления пользователя");
            throw new AlreadyExistsException("Ошибка добавления пользователя");
        }
    }

    @Override
    public void updateUser(User user) {
        try {
            users.replace(user.getId(), user);
        } catch (NotFoundException e) {
            log.error("Ошибка обновления пользователя");
            throw new NotFoundException("Ошибка обновления пользователя");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            users.remove(userId);
        } catch (RuntimeException e) {
            log.info("Ошибка удаления пользователя");
            throw new RuntimeException("Ошибка удаления пользователя");
        }
        log.info("Пользователь " + users.get(userId) + " удален");
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        return usersList;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}