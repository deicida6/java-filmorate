package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final UserDbStorage userStorage;

    public Film createFilm(Film film) {
        if (mpaStorage.getById(film.getMpa().getId()) == null) {
            throw new ValidationException("Указанный MPA  не найден");
        }
        if (film.getGenres() != null) {
            genreStorage.checkGenresExists(film.getGenres());
        }
        log.info("Фильм {} создан", film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.info("Id должен быть указан");
            throw new NullPointerException("Id должен быть указан");
        }
        if (filmStorage.getFilm(film.getId()) != null) {
            return filmStorage.updateFilm(film);
        }
        log.info("Не найден фильм");
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Film getFilm(Long filmId) {
        Film film = filmStorage.getFilm(filmId);
        film.setLikes(filmStorage.getLikes(film.getId()));
        film.setGenres(genreStorage.getGenresOfFilm(film.getId()));
        film.setMpa(mpaStorage.getMpaOfFilm(filmId));
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        for (Film film : films) {
            film.setLikes(filmStorage.getLikes(film.getId()));
            film.setGenres(genreStorage.getGenresOfFilm(film.getId()));
            film.setMpa(mpaStorage.getMpaOfFilm(film.getId()));
        }
        return films;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм отсуствует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь отсутсвует");
        }
        filmStorage.checkLikeOnFilm(filmId,userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLikeToFilm(Long filmId, Long userId) {
            if (filmStorage.getFilm(filmId) == null) {
                throw new NotFoundException("Фильм с таким ID не найден!");
            } else if (userStorage.getUser(userId) == null) {
                throw new NotFoundException("Пользователь с таким ID не найден!");
            }
            filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

}