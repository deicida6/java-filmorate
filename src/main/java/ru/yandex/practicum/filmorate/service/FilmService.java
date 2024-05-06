package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    public FilmService(@Autowired InMemoryFilmStorage filmStorage, @Autowired InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        filmStorage.getFilm(filmId).addLike(userId);
    }

    public void removeLikeToFilm(Long filmId, Long userId) {
        filmStorage.getFilm(filmId).removeLike(userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> films = filmStorage.getAllFilms();
        return films.stream()
                .sorted((film1, film2) -> film2.getUsersWhoLiked().size() - film1.getUsersWhoLiked().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}