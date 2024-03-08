package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid UserCreateRequest request) {
        log.info("POST /users");
        return client.add(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable @Min(1) Integer id) {
        log.info("GET /users/{}", id);
        return client.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET /users");
        return client.getAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable @Min(1) Integer id, @RequestBody @Valid UserUpdateRequest request) {
        log.info("PATCH /users/{}", id);
        return client.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        client.delete(id);
    }
}
