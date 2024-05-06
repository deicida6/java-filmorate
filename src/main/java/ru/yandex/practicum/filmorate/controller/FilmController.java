package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistsException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        // сохраняем новый фильм
        filmService.createFilm(film);
        log.info("Новый фильм добавлен" + film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (filmService.getFilm(newFilm.getId()) != null) {
            Film oldFilm = filmService.getFilm(newFilm.getId());
            // если фильм найден и все условия соблюдены, обновляем данные
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Фильм обновлен" + newFilm.getName());
            return oldFilm;
        }
        log.info("Не найден фильм");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long filmId, @PathVariable Long userId) {
       if (filmService.getFilm(filmId) == null) {
           throw new NotFoundException("Фильм отсуствует");
       } else if (filmService.getUser(userId) == null) {
           throw new NotFoundException("Пользователь отсутсвует");
       } else if (filmService.getFilm(filmId).getLikes().contains(userId)) {
           throw new AlreadyExistsException("Пользователь уже поставил лайк");
       } else {
           filmService.addLikeToFilm(filmId, userId);
       }
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLikeFromFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        if (filmService.getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с таким ID не найден!");
        } else if (filmService.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с таким ID не найден!");
        } else {
            filmService.removeLikeToFilm(filmId, userId);
        }
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsList(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getMostPopularFilms(count);
    }
}
