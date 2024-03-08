package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.validation.PaginationValidator;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestRequest request, Integer userId) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getAllForUser(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAll(Integer from, Integer size, Long userId) {
        PaginationValidator.validatePaginationParams(from, size);
        if (from != null && size != null) {
            Map<String, Object> params = Map.of(
                    "from", from,
                    "size", size
            );
            return get("/all?from={from}&size={size}", userId, params);
        }
        return get("/all", userId);
    }

    public ResponseEntity<Object> getById(Integer requestId, Integer userId) {
        return get("/" + requestId, userId);
    }
}
