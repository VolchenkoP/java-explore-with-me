package ru.practicum;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BaseClient {
    private static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;
    protected final RestTemplate restTemplate;

    protected ResponseEntity<Object> get(String uri, @Nullable Map<String, Object> parameters) {
        return sendRequest(uri, HttpMethod.GET, null, parameters);
    }

    protected <F> ResponseEntity<F> getList(String uri, @Nullable Map<String, Object> parameters,
                                            ParameterizedTypeReference<F> type) {
        return sendRequestForList(uri, HttpMethod.GET, null, parameters, type);
    }

    protected <T> ResponseEntity<Object> post(String uri, @Nullable T body,
                                              @Nullable Map<String, Object> parameters) {
        return sendRequest(uri, HttpMethod.POST, body, parameters);
    }

    private <T, F> ResponseEntity<F> sendRequestForList(String uri, HttpMethod method,
                                                        @Nullable T body,
                                                        @Nullable Map<String, Object> parameters,
                                                        ParameterizedTypeReference<F> type) {
        HttpEntity<T> request = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<F> response;
        if (parameters == null) {
            response = restTemplate.exchange(uri, method, request, type);
        } else {
            response = restTemplate.exchange(uri, method, request, type, parameters);
        }
        return response;
    }

    private <T> ResponseEntity<Object> sendRequest(String uri, HttpMethod method,
                                                   @Nullable T body,
                                                   @Nullable Map<String, Object> parameters) {
        HttpEntity<T> request = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> response;
        if (parameters == null) {
            response = restTemplate.exchange(uri, method, request, Object.class);
        } else {
            response = restTemplate.exchange(uri, method, request, Object.class, parameters);
        }
        return response;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MEDIA_TYPE);
        headers.setAccept(List.of(MEDIA_TYPE));
        return headers;
    }
}

