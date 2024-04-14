package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> filmMap = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return filmMap.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм
        filmMap.put(film.getId(), film);
        log.info("Новый фильм добавлен" + film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (filmMap.containsKey(newFilm.getId())) {
            Film oldFilm = filmMap.get(newFilm.getId());
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

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
