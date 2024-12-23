package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Requests;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    RequestDto mapToRequestDto(Requests request);

}
