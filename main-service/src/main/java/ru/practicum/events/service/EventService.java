package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.request.dto.EventRequestFullDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    EventFullDto updateEventUser(Long userId, Long eventId, EventUserRequest eventDtoUser);

    List<EventShortDto> getAllByUserId(Long userId, int from, int size);

    EventFullDto getAllByUserIdAndEventId(Long userId, Long eventId);

    List<EventRequestFullDto> getAllRequests(Long userId, Long eventId);

    List<EventFullDto> getEventAdmin(AdminSearch adminSearch, int from, int size);

    EventFullDto updateEventAdmin(Long eventId, EventAdminRequest eventDtoRequest);

    List<EventShortDto> getAllEventsPublic(UserSearch userSearch, int from, int size, HttpServletRequest request);

    EventFullDto getEventPublicById(Long id, HttpServletRequest request);
}