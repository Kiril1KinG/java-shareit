package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.classBuilder.TestCommentProvider;
import ru.practicum.shareit.classBuilder.TestItemProvider;
import ru.practicum.shareit.classBuilder.TestUserProvider;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class, ItemMapper.class, CommentMapper.class})
class ItemControllerTest {

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void add() throws Exception {
        Item item = TestItemProvider.provideItem(1, "item", "desc", true,
                TestUserProvider.buildUser(1, "user", "email"),
                null, null, null, null);

        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("item");
        request.setDescription("desc");
        request.setAvailable(true);
        request.setRequestId(null);

        when(itemService.add(eq(1), any())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(item))));
    }

    @Test
    void get() throws Exception {
        Item item = TestItemProvider.provideItem(1, "item", "desc", true,
                TestUserProvider.buildUser(1, "user", "email"),
                null, null, null, null);

        when(itemService.get(1, 1)).thenReturn(item);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toItemWithBookingsResponse(item))));
    }

    @Test
    void getAllForOwner() throws Exception {
        List<Item> items = List.of(TestItemProvider.provideItem(1, "item", "desc", true,
                        TestUserProvider.buildUser(1, "user", "email"),
                        null, null, null, null),
                TestItemProvider.provideItem(2, "item2", "desc2", true,
                        TestUserProvider.buildUser(1, "user", "email"),
                        null, null, null, null));

        when(itemService.getByOwnerId(1, 0, 1)).thenReturn(List.of(items.get(0)));
        when(itemService.getByOwnerId(1, null, null)).thenReturn(items);

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items.stream()
                        .map(mapper::toItemWithBookingsResponse)
                        .collect(Collectors.toList()))));

        mvc.perform(MockMvcRequestBuilders.get("/items?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items.stream()
                        .map(mapper::toItemWithBookingsResponse)
                        .limit(1)
                        .collect(Collectors.toList()))));
    }

    @Test
    void search() throws Exception {
        List<Item> items = List.of(TestItemProvider.provideItem(1, "item", "desc", true,
                        TestUserProvider.buildUser(1, "user", "email"),
                        null, null, null, null),
                TestItemProvider.provideItem(2, "other item", "other desc", true,
                        TestUserProvider.buildUser(1, "user", "email"),
                        null, null, null, null));

        when(itemService.search("other", 0, 1)).thenReturn(List.of(items.get(0)));
        when(itemService.search("item", null, null)).thenReturn(items);

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))));

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=other&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items.stream()
                        .map(mapper::toResponse)
                        .limit(1)
                        .collect(Collectors.toList()))));
    }

    @Test
    void update() throws Exception {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("update name");
        request.setDescription("update desc");
        request.setAvailable(true);
        Item item = TestItemProvider.provideItem(1, "update name", "update desc", true,
                TestUserProvider.buildUser(1, "user", "email"),
                null, null, null, null);

        when(itemService.update(eq(1), any())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mapper.toResponse(item))));


    }

    @Test
    void addComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("comment");

        CommentRequest badRequest = new CommentRequest();
        badRequest.setText("");

        Comment comment = TestCommentProvider.provideComment(1, "comment", new Item(),
                TestUserProvider.buildUser(1, "name", "email"), LocalDateTime.now());

        when(itemService.addComment(any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentMapper.toResponse(comment))));

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(badRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}