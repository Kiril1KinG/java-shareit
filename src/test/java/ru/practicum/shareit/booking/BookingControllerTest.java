package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookingController.class, BookingMapper.class, ErrorHandler.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;


    @Test
    void add() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequest bookingRequest = new BookingRequest(7, start, end);
        Booking booking = new Booking(1, start, end,
                new Item(2, "item", "desc", true, new User(), null, null, null, new ArrayList<>()),
                new User(3, "name", "email"),
                BookingStatus.WAITING);

        Mockito.when(bookingService.add(Mockito.any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("$.booker.id").value(3))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("item"));
    }

    @Test
    void approveOrReject() throws Exception {
        Booking booking = new Booking(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                new Item(1, "item", "desc", true, new User(), null, null, null, new ArrayList<>()),
                new User(1, "name", "email"),
                BookingStatus.APPROVED);

        Mockito.when(bookingService.approveBooking(1, 1, true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                new Item(2, "item", "desc", true, new User(), null, null, null, new ArrayList<>()),
                new User(3, "name", "email"),
                BookingStatus.WAITING);

        Mockito.when(bookingService.getById(1, 3)).thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("$.booker.id").value(3))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("item"));
    }

    @Test
    void getAllByState() throws Exception {
        List<Booking> bookings = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        new Item(2, "item", "desc", true, new User(), null, null, null, new ArrayList<>()),
                        new User(3, "name", "email"),
                        BookingStatus.WAITING),
                new Booking(4, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        new Item(3, "item2", "desc2", true, new User(), null, null, null, new ArrayList<>()),
                        new User(5, "name2", "email2"),
                        BookingStatus.WAITING));

        Mockito.when(bookingService.getAllBookingsByState(3, "ALL", 0, 10)).thenReturn(bookings);
        Mockito.when(bookingService.getAllBookingsByState(3, null, null, null)).thenReturn(bookings);

        mvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].start").exists())
                .andExpect(jsonPath("[0].end").exists())
                .andExpect(jsonPath("[0].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[0].booker.id").value(3))
                .andExpect(jsonPath("[0].item.id").value(2))
                .andExpect(jsonPath("[0].item.name").value("item"))
                .andExpect(jsonPath("[1].id").value(4))
                .andExpect(jsonPath("[1].start").exists())
                .andExpect(jsonPath("[1].end").exists())
                .andExpect(jsonPath("[1].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[1].booker.id").value(5))
                .andExpect(jsonPath("[1].item.id").value(3))
                .andExpect(jsonPath("[1].item.name").value("item2"));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].start").exists())
                .andExpect(jsonPath("[0].end").exists())
                .andExpect(jsonPath("[0].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[0].booker.id").value(3))
                .andExpect(jsonPath("[0].item.id").value(2))
                .andExpect(jsonPath("[0].item.name").value("item"))
                .andExpect(jsonPath("[1].id").value(4))
                .andExpect(jsonPath("[1].start").exists())
                .andExpect(jsonPath("[1].end").exists())
                .andExpect(jsonPath("[1].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[1].booker.id").value(5))
                .andExpect(jsonPath("[1].item.id").value(3))
                .andExpect(jsonPath("[1].item.name").value("item2"));
    }

    @Test
    void getAllBookingsForItemsByState() throws Exception {
        List<Booking> bookings = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        new Item(2, "item", "desc", true, new User(), null, null, null, new ArrayList<>()),
                        new User(3, "name", "email"),
                        BookingStatus.WAITING),
                new Booking(4, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                        new Item(3, "item2", "desc2", true, new User(), null, null, null, new ArrayList<>()),
                        new User(5, "name2", "email2"),
                        BookingStatus.WAITING));

        Mockito.when(bookingService.getAllBookingsForItemsByState(3, "ALL", 0, 10)).thenReturn(bookings);
        Mockito.when(bookingService.getAllBookingsForItemsByState(3, null, null, null)).thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].start").exists())
                .andExpect(jsonPath("[0].end").exists())
                .andExpect(jsonPath("[0].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[0].booker.id").value(3))
                .andExpect(jsonPath("[0].item.id").value(2))
                .andExpect(jsonPath("[0].item.name").value("item"))
                .andExpect(jsonPath("[1].id").value(4))
                .andExpect(jsonPath("[1].start").exists())
                .andExpect(jsonPath("[1].end").exists())
                .andExpect(jsonPath("[1].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[1].booker.id").value(5))
                .andExpect(jsonPath("[1].item.id").value(3))
                .andExpect(jsonPath("[1].item.name").value("item2"));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].start").exists())
                .andExpect(jsonPath("[0].end").exists())
                .andExpect(jsonPath("[0].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[0].booker.id").value(3))
                .andExpect(jsonPath("[0].item.id").value(2))
                .andExpect(jsonPath("[0].item.name").value("item"))
                .andExpect(jsonPath("[1].id").value(4))
                .andExpect(jsonPath("[1].start").exists())
                .andExpect(jsonPath("[1].end").exists())
                .andExpect(jsonPath("[1].status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("[1].booker.id").value(5))
                .andExpect(jsonPath("[1].item.id").value(3))
                .andExpect(jsonPath("[1].item.name").value("item2"));
    }
}