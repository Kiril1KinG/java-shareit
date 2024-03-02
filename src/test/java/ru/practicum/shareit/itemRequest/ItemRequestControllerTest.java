package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.classBuilder.ItemRequestBuilder;
import ru.practicum.shareit.classBuilder.UserBuilder;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemRequestController.class, ItemRequestMapper.class, ItemMapper.class, CommentMapper.class})
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("desc");
        request.setRequestor(null);
        request.setCreated(now);

        ItemRequest itemRequest = ItemRequestBuilder.buildItemRequest(1, "desc",
                UserBuilder.buildUser(1, "name", "email"),
                now, new ArrayList<>());

        when(itemRequestService.create(any())).thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.created").hasJsonPath());
    }

    @Test
    void getAllForUser() throws Exception {
        List<ItemRequest> itemRequests = List.of(
                ItemRequestBuilder.buildItemRequest(1, "desc",
                        UserBuilder.buildUser(1, "name", "email"),
                        LocalDateTime.now(), List.of(new Item())),
                ItemRequestBuilder.buildItemRequest(2, "desc2",
                        UserBuilder.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()));

        when(itemRequestService.getAllForUser(1)).thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].created").hasJsonPath())
                .andExpect(jsonPath("[0].items").hasJsonPath())
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].description").value("desc2"))
                .andExpect(jsonPath("[1].created").hasJsonPath())
                .andExpect(jsonPath("[1].items").hasJsonPath());
    }

    @Test
    void getAll() throws Exception {
        List<ItemRequest> itemRequests = List.of(
                ItemRequestBuilder.buildItemRequest(1, "desc",
                        UserBuilder.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()),
                ItemRequestBuilder.buildItemRequest(2, "desc2",
                        UserBuilder.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()));

        when(itemRequestService.getAll(1, null, null)).thenReturn(itemRequests);
        when(itemRequestService.getAll(1, 0, 1)).thenReturn(List.of(itemRequests.get(0)));
        when(itemRequestService.getAll(1, null, 1)).thenThrow(new PaginationParamsException(""));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].created").hasJsonPath())
                .andExpect(jsonPath("[0].items").hasJsonPath())
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].description").value("desc2"))
                .andExpect(jsonPath("[1].created").hasJsonPath())
                .andExpect(jsonPath("[1].items").hasJsonPath());

        mvc.perform(get("/requests/all?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].created").hasJsonPath())
                .andExpect(jsonPath("[0].items").hasJsonPath());

        mvc.perform(get("/requests/all?size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById() throws Exception {
        ItemRequest itemRequest = ItemRequestBuilder.buildItemRequest(1, "desc",
                UserBuilder.buildUser(1, "name", "email"),
                LocalDateTime.now(), new ArrayList<>());

        when(itemRequestService.getById(1, 1)).thenReturn(itemRequest);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.created").hasJsonPath())
                .andExpect(jsonPath("$.items").hasJsonPath());
    }
}
