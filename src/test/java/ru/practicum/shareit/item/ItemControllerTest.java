package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class, ItemMapper.class, CommentMapper.class})
class ItemControllerTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void add() throws Exception {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                null, null, null, null);

        ItemCreateRequest request = new ItemCreateRequest("item", "desc", true, null);

        Mockito.when(itemService.add(Mockito.eq(1), Mockito.any())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void get() throws Exception {
        Item item = new Item(1, "item", "desc", true,
                new User(1, "user", "email"),
                null, null, null, null);

        Mockito.when(itemService.get(1, 1)).thenReturn(item);

        mvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.lastBooking").hasJsonPath())
                .andExpect(jsonPath("$.nextBooking").hasJsonPath())
                .andExpect(jsonPath("$.comments").hasJsonPath());
    }

    @Test
    void getAllForOwner() throws Exception {
        List<Item> items = List.of(new Item(1, "item", "desc", true,
                        new User(1, "user", "email"),
                        null, null, null, null),
                new Item(2, "item2", "desc2", true,
                        new User(1, "user", "email"),
                        null, null, null, null));

        Mockito.when(itemService.getByOwnerId(1, 0, 1)).thenReturn(List.of(items.get(0)));
        Mockito.when(itemService.getByOwnerId(1, null, null)).thenReturn(items);

        mvc.perform(MockMvcRequestBuilders.get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("item"))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].available").value("true"))
                .andExpect(jsonPath("[0].lastBooking").hasJsonPath())
                .andExpect(jsonPath("[0].nextBooking").hasJsonPath())
                .andExpect(jsonPath("[0].comments").hasJsonPath())
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].name").value("item2"))
                .andExpect(jsonPath("[1].description").value("desc2"))
                .andExpect(jsonPath("[1].available").value("true"))
                .andExpect(jsonPath("[1].lastBooking").hasJsonPath())
                .andExpect(jsonPath("[1].nextBooking").hasJsonPath())
                .andExpect(jsonPath("[1].comments").hasJsonPath());

        mvc.perform(MockMvcRequestBuilders.get("/items?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("item"))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].available").value("true"))
                .andExpect(jsonPath("[0].lastBooking").hasJsonPath())
                .andExpect(jsonPath("[0].nextBooking").hasJsonPath())
                .andExpect(jsonPath("[0].comments").hasJsonPath());
    }

    @Test
    void search() throws Exception {
        List<Item> items = List.of(new Item(1, "item", "desc", true,
                        new User(1, "user", "email"),
                        null, null, null, null),
                new Item(2, "other item", "other desc", true,
                        new User(1, "user", "email"),
                        null, null, null, null));

        Mockito.when(itemService.search("other", 0, 1)).thenReturn(List.of(items.get(0)));
        Mockito.when(itemService.search("item", null, null)).thenReturn(items);

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("item"))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].available").value("true"))
                .andExpect(jsonPath("[0].comments").hasJsonPath())
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].name").value("other item"))
                .andExpect(jsonPath("[1].description").value("other desc"))
                .andExpect(jsonPath("[1].available").value("true"))
                .andExpect(jsonPath("[1].comments").hasJsonPath());

        mvc.perform(MockMvcRequestBuilders.get("/items/search?text=other&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("item"))
                .andExpect(jsonPath("[0].description").value("desc"))
                .andExpect(jsonPath("[0].available").value("true"))
                .andExpect(jsonPath("[0].comments").hasJsonPath());
    }

    @Test
    void update() throws Exception {
        ItemUpdateRequest request = new ItemUpdateRequest("update name", "update desc", true);
        Item item = new Item(1, "update name", "update desc", true,
                new User(1, "user", "email"),
                null, null, null, null);

        Mockito.when(itemService.update(Mockito.eq(1), Mockito.any())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("update name"))
                .andExpect(jsonPath("$.description").value("update desc"))
                .andExpect(jsonPath("$.available").value("true"))
                .andExpect(jsonPath("$.comments").hasJsonPath());


    }

    @Test
    void addComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("comment");

        CommentRequest badRequest = new CommentRequest();
        badRequest.setText("");

        Comment comment = new Comment(1, "comment", new Item(),
                new User(1, "name", "email"), LocalDateTime.now());

        Mockito.when(itemService.addComment(Mockito.any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.authorName").hasJsonPath())
                .andExpect(jsonPath("$.created").hasJsonPath());

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(badRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}