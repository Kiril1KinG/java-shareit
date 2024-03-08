package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.classBuilder.TestItemRequestProvider;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.exception.PaginationParamsException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemRequestController.class, ItemRequestMapper.class, ItemMapper.class, CommentMapper.class})
class ItemRequestControllerTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);
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

        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "name", "email"),
                now, new ArrayList<>());

        when(itemRequestService.create(any())).thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(itemRequest))));
    }

    @Test
    void getAllForUser() throws Exception {
        List<ItemRequest> itemRequests = List.of(
                TestItemRequestProvider.provideItemRequest(1, "desc",
                        TestUserProvider.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()),
                TestItemRequestProvider.provideItemRequest(2, "desc2",
                        TestUserProvider.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()));

        when(itemRequestService.getAllForUser(1)).thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequests.stream()
                        .map(mapper::toResponseWithItems)
                        .collect(Collectors.toList()))));
    }

    @Test
    void getAll() throws Exception {
        List<ItemRequest> itemRequests = List.of(
                TestItemRequestProvider.provideItemRequest(1, "desc",
                        TestUserProvider.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()),
                TestItemRequestProvider.provideItemRequest(2, "desc2",
                        TestUserProvider.buildUser(1, "name", "email"),
                        LocalDateTime.now(), new ArrayList<>()));

        when(itemRequestService.getAll(1, null, null)).thenReturn(itemRequests);
        when(itemRequestService.getAll(1, 0, 1)).thenReturn(List.of(itemRequests.get(0)));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequests.stream()
                        .map(mapper::toResponseWithItems)
                        .collect(Collectors.toList()))));

        mvc.perform(get("/requests/all?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequests.stream()
                        .map(mapper::toResponseWithItems)
                        .limit(1)
                        .collect(Collectors.toList()))));
    }

    @Test
    void getById() throws Exception {
        ItemRequest itemRequest = TestItemRequestProvider.provideItemRequest(1, "desc",
                TestUserProvider.buildUser(1, "name", "email"),
                LocalDateTime.now(), new ArrayList<>());

        when(itemRequestService.getById(1, 1)).thenReturn(itemRequest);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponseWithItems(itemRequest))));
    }
}
