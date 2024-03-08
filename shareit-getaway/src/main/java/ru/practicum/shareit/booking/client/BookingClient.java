package ru.practicum.shareit.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.validation.PaginationValidator;

import java.util.Map;
import java.util.Set;

@Component
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> add(BookingRequest request, Integer userId) {
        checkBookingRequestTime(request);
        return post("", userId, request);
    }

    public ResponseEntity<Object> approveOrReject(Integer bookingId, Long userId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getById(Integer bookingId, Integer userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByState(Long userId, String state, Integer from, Integer size) {
        state = filterBookingState(state);
        PaginationValidator.validatePaginationParams(from, size);
        if (from != null && size != null) {
            Map<String, Object> params = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("?state={state}&from={from}&size={size}", userId, params);
        }
        return get("?state=" + state, userId);
    }

    public ResponseEntity<Object> getAllBookingsForItemsByState(Long userId, String state, Integer from, Integer size) {
        state = filterBookingState(state);
        PaginationValidator.validatePaginationParams(from, size);
        if (from != null && size != null) {
            Map<String, Object> params = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("/owner?state={state}&from={from}&size={size}", userId, params);
        }
        return get("/owner?state=" + state, userId);
    }


    //TODO удалить валидацию в сервере, также обработчик и сам эксепшн
    private void checkBookingRequestTime(BookingRequest bookingRequest) {
        if (bookingRequest.getEnd().isBefore(bookingRequest.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be before start");
        }
        if (bookingRequest.getEnd().equals(bookingRequest.getStart())) {
            throw new TimeValidationException("Incorrect time, end can not be equal start");
        }
    }

    //TODO удалить валидацию в сервере
    private String filterBookingState(String state) {
        Set<String> validValues = Set.of("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");
        if (state == null) {
            return "ALL";
        }
        if (!validValues.contains(state.toUpperCase())) {
            throw new UnknownStateException("Unknown state: " + state);
        }
        return state.toUpperCase();
    }
}
