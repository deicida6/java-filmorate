package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);

    void deleteUser(Long userId);

    User getUser(Long userId);

    List<User> getAllUsers();
}
