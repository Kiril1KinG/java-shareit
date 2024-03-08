package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.validation.PaginationValidator;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareIt-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> add(Integer userId, ItemCreateRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> get(Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllForOwner(Long userId, Integer from, Integer size) {
        PaginationValidator.validatePaginationParams(from, size);
        if (from != null && size != null) {
            Map<String, Object> params = Map.of(
                    "from", from,
                    "size", size
            );
            return get("?from={from}&size={size}", userId, params);
        }
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        PaginationValidator.validatePaginationParams(from, size);
        if (from != null && size != null) {
            Map<String, Object> params = Map.of(
                    "text", text,
                    "from", from,
                    "size", size
            );
            return get("/search?text={text}&from={from}&size={size}", null, params);
        }
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> update(Integer userId, Integer itemId, ItemUpdateRequest request) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> addComment(Integer itemId, Integer userId, CommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }
}
