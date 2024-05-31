package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        if (service.getById(id) == null) {
            throw new NotFoundException("Жанра с таким id = " + id + " нет");
        } else {
            return service.getById(id);
        }

    }
}
