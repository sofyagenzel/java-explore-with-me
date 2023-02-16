package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EventRequestFullDto {
    Long id;
    Long requester;
    Long event;
    RequestStatus status;
    LocalDateTime created;
}