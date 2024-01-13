package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserResponse add(@RequestBody @Valid UserCreateRequest request) {
        log.info("POST /users");
        return mapper.toResponse(userService.add(mapper.toUser(request)));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Integer id) {
        log.info("GET /users/{}", id);
        return mapper.toResponse(userService.get(id));
    }

    @GetMapping
    public Collection<UserResponse> getAll() {
        log.info("GET /users");
        return userService.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Integer id, @RequestBody @Valid UserUpdateRequest request) {
        log.info("PATCH /users/{}", id);
        return mapper.toResponse(userService.update(id, mapper.toUser(request)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }


}
