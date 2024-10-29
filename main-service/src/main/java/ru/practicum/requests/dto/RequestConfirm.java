package ru.practicum.requests.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestConfirm {

    private List<Long> requestsId;
    private String status;
}
