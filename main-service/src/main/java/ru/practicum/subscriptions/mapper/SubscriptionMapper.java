package ru.practicum.subscriptions.mapper;

import org.mapstruct.Mapper;
import ru.practicum.subscriptions.dto.SubscriptionDto;
import ru.practicum.subscriptions.model.Subscription;

@Mapper
public interface SubscriptionMapper {

    SubscriptionDto mapToSubscriptionDto(Subscription subscription);

    Subscription toEntity(SubscriptionDto dto);
}
