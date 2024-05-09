package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validation.AfterDate;

import java.time.LocalDate;
import java.util.Set;

@Data
@Validated
@Builder
public class User {
    private Long id;
    @Email
    @NotBlank(message = "Почта не может быть пустая")
    private String email;
    @NotBlank(message = "Login не должен быть пустым")
    private String login;
    private String name;
    @AfterDate
    private LocalDate birthday;
    private Set<Long> friends;
    private boolean statusFriendship;

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void removeFriend(Long id) {
        friends.remove(id);
    }
}

