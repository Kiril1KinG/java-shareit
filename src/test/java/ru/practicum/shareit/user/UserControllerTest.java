package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.classBuilder.UserBuilder;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, UserMapper.class})
class UserControllerTest {

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
        User user = UserBuilder.buildUser(1, "user", "email@yandex.ru");

        when(userService.add(any())).thenReturn(user);

        mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("user"))
                .andExpect(jsonPath("email").value("email@yandex.ru"));

    }

    @Test
    void get() throws Exception {
        User user = UserBuilder.buildUser(1, "user", "email@yandex.ru");

        when(userService.get(1)).thenReturn(user);
        when(userService.get(99)).thenThrow(new DataDoesNotExistsException(""));

        mvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("user"))
                .andExpect(jsonPath("email").value("email@yandex.ru"));

        mvc.perform(MockMvcRequestBuilders.get("/users/99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        List<User> users = List.of(
                UserBuilder.buildUser(1, "user", "email@yandex.ru"),
                UserBuilder.buildUser(2, "user2", "email2@yandex.ru"));

        when(userService.getAll()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("user"))
                .andExpect(jsonPath("[0].email").value("email@yandex.ru"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].name").value("user2"))
                .andExpect(jsonPath("[1].email").value("email2@yandex.ru"));
    }

    @Test
    void update() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("update user");
        request.setEmail("updateEmail@yandex.ru");
        User user = UserBuilder.buildUser(1, "update user", "updateEmail@yandex.ru");

        when(userService.update(any())).thenReturn(user);
        when(userService.update(UserBuilder.buildUser(99, "update user", "updateEmail@yandex.ru")))
                .thenThrow(new DataAlreadyExistsException(""));

        mvc.perform(patch("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("update user"))
                .andExpect(jsonPath("email").value("updateEmail@yandex.ru"));

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