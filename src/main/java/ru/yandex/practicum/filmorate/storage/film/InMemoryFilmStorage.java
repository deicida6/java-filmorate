package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();

    @Override
    public void createFilm(Film film) {
        Set<Long> likes = new TreeSet<>();
        film.setLikes(likes);
        if (film.getId() == null) {
            film.setId(getNextId());
        }
        try {
            films.put(film.getId(), film);
            log.info("Фильм создан с ID " + film.getId() + ")");
        } catch (AlreadyExistsException e) {
            log.error("Ошибка создания фильма");
            throw new AlreadyExistsException("Ошибка добавления фильма");
        }
    }

    @Override
    public void updateFilm(Film film) {
        try {
            films.replace(film.getId(), film);
        } catch (NotFoundException e) {
            log.error("Ошибка обновления фильма");
            throw new NotFoundException("Ошибка обновления фильма");
        }
    }

    @Override
    public void deleteFilm(Long filmId) {
        try {
            films.remove(filmId);
        } catch (NotFoundException ex) {
            log.error("Ошибка при удалении фильма");
            throw new NotFoundException("Ошибка при удалении фильма");
        }
    }

    @Override
    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }

    public List<Film> getAllFilms() {
        List<Film> filmList = films.entrySet().parallelStream()
                        .collect(ArrayList::new,
                                (list, element) -> list.add(element.getValue()),
                                ArrayList::addAll);
        return filmList;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
