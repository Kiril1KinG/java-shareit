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
import ru.practicum.shareit.classBuilder.CommentBuilder;
import ru.practicum.shareit.classBuilder.ItemBuilder;
import ru.practicum.shareit.classBuilder.UserBuilder;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void get() throws Exception {
        Item item = ItemBuilder.buildItem(1, "item", "desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                null, null, null, null);

        when(itemService.get(1, 1)).thenReturn(item);

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
        List<Item> items = List.of(ItemBuilder.buildItem(1, "item", "desc", true,
                        UserBuilder.buildUser(1, "user", "email"),
                        null, null, null, null),
                ItemBuilder.buildItem(2, "item2", "desc2", true,
                        UserBuilder.buildUser(1, "user", "email"),
                        null, null, null, null));

        when(itemService.getByOwnerId(1, 0, 1)).thenReturn(List.of(items.get(0)));
        when(itemService.getByOwnerId(1, null, null)).thenReturn(items);

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
        List<Item> items = List.of(ItemBuilder.buildItem(1, "item", "desc", true,
                        UserBuilder.buildUser(1, "user", "email"),
                        null, null, null, null),
                ItemBuilder.buildItem(2, "other item", "other desc", true,
                        UserBuilder.buildUser(1, "user", "email"),
                        null, null, null, null));

        when(itemService.search("other", 0, 1)).thenReturn(List.of(items.get(0)));
        when(itemService.search("item", null, null)).thenReturn(items);

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
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("update name");
        request.setDescription("update desc");
        request.setAvailable(true);
        Item item = ItemBuilder.buildItem(1, "update name", "update desc", true,
                UserBuilder.buildUser(1, "user", "email"),
                null, null, null, null);

        when(itemService.update(eq(1), any())).thenReturn(item);

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

        Comment comment = CommentBuilder.buildComment(1, "comment", new Item(),
                UserBuilder.buildUser(1, "name", "email"), LocalDateTime.now());

        when(itemService.addComment(any())).thenReturn(comment);

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