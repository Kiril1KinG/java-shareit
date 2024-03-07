package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.exception.DataAlreadyExistsException;
import ru.practicum.shareit.exception.DataDoesNotExistsException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserMapper.class})
class UserControllerTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;

    @Test
    void add() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("user");
        request.setEmail("email@yandex.ru");
        User user = TestUserProvider.buildUser(1, "user", "email@yandex.ru");

        when(userService.add(any())).thenReturn(user);

        mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(user))));

    }

    @Test
    void get() throws Exception {
        User user = TestUserProvider.buildUser(1, "user", "email@yandex.ru");

        when(userService.get(1)).thenReturn(user);
        when(userService.get(99)).thenThrow(new DataDoesNotExistsException(""));

        mvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(user))));

        mvc.perform(MockMvcRequestBuilders.get("/users/99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        List<User> users = List.of(
                TestUserProvider.buildUser(1, "user", "email@yandex.ru"),
                TestUserProvider.buildUser(2, "user2", "email2@yandex.ru"));

        when(userService.getAll()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))));
    }

    @Test
    void update() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("update user");
        request.setEmail("updateEmail@yandex.ru");
        User user = TestUserProvider.buildUser(1, "update user", "updateEmail@yandex.ru");

        when(userService.update(any())).thenReturn(user);
        when(userService.update(TestUserProvider.buildUser(99, "update user", "updateEmail@yandex.ru")))
                .thenThrow(new DataAlreadyExistsException(""));

        mvc.perform(patch("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(user))));

        mvc.perform(patch("/users/99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}