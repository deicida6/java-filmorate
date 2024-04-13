package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = Film.builder()
                .name("film1")
                .description("descriptionFilm1")
                .releaseDate(LocalDate.of(2000,12,12))
                .duration(100)
                .build();
    }

    @Test
    void testCreateWithEmptyName() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void testCreateWithDescriptionOver200Symbols() {
        film.setDescription("Это очень длинное описание фильма, которое должно превышать 200 символов," +
                " в котором расписывается все тонкости сюжетной линии, " +
                "хронология повествования и главное под какие снэки стоит смотреть этот фильм");
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void testCreateWithInvalidReleaseDate() {
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    public void testCreateWithNegativeDuration() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}