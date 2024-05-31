package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Component
@Slf4j
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        String sqlQuery =
                "INSERT INTO users (email, login, name, birthday)values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
       return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("дао обновление{}", user);
        String sqlQuery =
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public User getUser(Long id) {
       try {
           return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", new DataClassRowMapper<>(User.class), id);
       } catch (EmptyResultDataAccessException e) {
           return null;
       }
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new DataClassRowMapper<>(User.class));
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO friends (user1_id, user2_id, status)values (?, ?, ?)", userId, friendId, true);
    }

    @Override
    public void removeFriends(Long  userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user1_id = ? AND user2_id = ?", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id IN (SELECT user2_id FROM friends WHERE user1_id = ? AND status = true)", new DataClassRowMapper<>(User.class), id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long friendId) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id IN (SELECT user2_id FROM friends WHERE user1_id = ? AND status = true AND user2_id IN ( SELECT user2_id FROM friends WHERE user1_id = ? AND status = true))", new DataClassRowMapper<>(User.class), id, friendId);

    }

}
