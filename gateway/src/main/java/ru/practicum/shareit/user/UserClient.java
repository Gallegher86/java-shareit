package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDtoCreated;
import ru.practicum.shareit.user.dto.UserDtoUpdated;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(UserDtoCreated dto) {
        return post("", dto);
    }

    public ResponseEntity<Object> update(Long id, UserDtoUpdated dto) {
        Map<String, Object> params = Map.of("id", id);
        return patch("/{id}", null, params, dto);
    }

    public ResponseEntity<Object> findById(Long id) {
        Map<String, Object> params = Map.of("id", id);
        return get("/{id}", null, params);
    }

    public ResponseEntity<Object> delete(Long id) {
        Map<String, Object> params = Map.of("id", id);
        return delete("/{id}", null, params);
    }
}
