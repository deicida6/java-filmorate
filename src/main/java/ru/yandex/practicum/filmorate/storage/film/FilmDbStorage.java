package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sqlQuery =
                "INSERT INTO films (name, description, release_date, duration, rating_mpa_id)values (?, ?, ?, ? ,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5,film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        if (film.getGenres() != null) {
            Set<Genre> genres = new LinkedHashSet<>(film.getGenres());
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO films_genre (film_id, genre_id)values(?,?)", film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery =
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film getFilm(Long filmId) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?", new DataClassRowMapper<>(Film.class), filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", new DataClassRowMapper<>(Film.class));
    }

    @Override
    public void addLike(Long id, Long userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id)values (?, ?);", id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?;", id, userId);

    }

    @Override
    public List<User> getLikes(Long filmId) {
        try {
            return jdbcTemplate.query("SELECT * FROM users WHERE id IN (SELECT user_id FROM likes WHERE film_id = ?)",new DataClassRowMapper<>(User.class), filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(
                "SELECT ID, NAME, cnt_like " +
                "FROM PUBLIC.FILMS f " +
                "LEFT JOIN (select FILM_ID, COUNT(user_id) cnt_like from likes group by FILM_ID) l ON (f.id = l.FILM_ID) " +
                "ORDER BY l.cnt_like DESC " +
                "LIMIT ?", new DataClassRowMapper<>(Film.class), count);
    }

    @Override
    public boolean checkLikeOnFilm(Long filmId, Long userId) {
         if ((jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id = ? AND user_id = ?", new ColumnMapRowMapper(), filmId, userId)).contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " уже поставил лайк");
        }
        return true;
    }
}
