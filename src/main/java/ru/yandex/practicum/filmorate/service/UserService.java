package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    public UserService(@Autowired InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriend(Long userId, Long friendId) {
           userStorage.getUser(friendId).addFriend(userId);
           userStorage.getUser(userId).addFriend(friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
            userStorage.getUser(userId).removeFriend(friendId);
            userStorage.getUser(friendId).removeFriend(userId);
    }

    public List<User> getUsersFriends(Long userId) {
        List<User> friends = new ArrayList<>();
        for (Long id : userStorage.getUser(userId).getFriends()) {
            friends.add(userStorage.getUser(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);
        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();
        List<User> common = new ArrayList<>();
        for (Long id : friends1) {
            if (friends2.contains(id)) {
                common.add(userStorage.getUser(id));
            }
        }
        return common;
    }

}
