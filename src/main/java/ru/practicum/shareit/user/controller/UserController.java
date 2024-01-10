package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserResponse add(@RequestBody UserRequest request) {
        return mapper.toResponse(userService.add(mapper.toUser(request)));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Integer id) {
        return mapper.toResponse(userService.get(id));
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Integer id, @RequestBody UserRequest request) {
        return mapper.toResponse(userService.update(id, mapper.toUser(request)));
    }


}
