package ru.practicum.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.State;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setCreatedOn(LocalDateTime.now().withNano(0));
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setState(State.PENDING);
        return event;
    }

    public static Event toEvent(Event event, EventAdminRequest eventAdminRequest) {

        if (eventAdminRequest.getAnnotation() != null) event.setAnnotation(eventAdminRequest.getAnnotation());
        if (eventAdminRequest.getDescription() != null) event.setDescription(eventAdminRequest.getDescription());
        if (eventAdminRequest.getEventDate() != null) event.setEventDate(eventAdminRequest.getEventDate());
        if (eventAdminRequest.getPaid() != null) event.setPaid(eventAdminRequest.getPaid());
        if (eventAdminRequest.getParticipantLimit() != null)
            event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        if (eventAdminRequest.getRequestModeration() != null)
            event.setRequestModeration(eventAdminRequest.getRequestModeration());
        if (eventAdminRequest.getTitle() != null) event.setTitle(eventAdminRequest.getTitle());
        return event;
    }

    public static Event toEventUserUpdate(Event event, EventUserRequest eventUserRequest) {
        if (eventUserRequest.getAnnotation() != null) event.setAnnotation(eventUserRequest.getAnnotation());
        Optional.ofNullable(eventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventUserRequest.getEventDate()).ifPresent(event::setEventDate);
        if (eventUserRequest.getPaid() != null) event.setPaid(eventUserRequest.getPaid());
        Optional.ofNullable(eventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        event.setRequestModeration(eventUserRequest.getRequestModeration());
        Optional.ofNullable(eventUserRequest.getTitle()).ifPresent(event::setTitle);
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn().format(DATE_TIME_FORMATTER),
                event.getDescription(),
                event.getEventDate() != null ? event.getEventDate().format(DATE_TIME_FORMATTER) : null,
                UserMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn() != null ? event.getPublishedOn().format(DATE_TIME_FORMATTER) : null,
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews());
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate() != null ? event.getEventDate().format(DATE_TIME_FORMATTER) : null,
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews());
    }
}