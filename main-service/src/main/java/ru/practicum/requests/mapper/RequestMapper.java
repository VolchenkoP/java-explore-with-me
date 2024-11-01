package ru.practicum.requests.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Requests;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static RequestDto toRequestDto(Requests requests) {
        return RequestDto
                .builder()
                .id(requests.getId())
                .created(requests.getCreated())
                .event(requests.getEvent().getId())
                .requester(requests.getRequester().getId())
                .status(requests.getStatus())
                .build();
    }
}
