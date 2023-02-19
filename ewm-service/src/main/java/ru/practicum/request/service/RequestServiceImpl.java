package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.request.dto.EventRequestFullDto;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public EventRequestFullDto createRequest(Long userId, Long eventId) {
        Request eventRequest = new Request();
        eventRequest.setCreated(LocalDateTime.now().withNano(0));
        var requester = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден " + userId));
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие не найдено " + eventId));
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Не корректный инициатор " + event.getInitiator().getId());
        }
        Optional<Request> requestDb = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requestDb.isPresent()) {
            throw new ForbiddenException("Повторный запрос от пользователя " + userId + " на событие" + eventId);
        }
        if (event.getState() != State.PUBLISHED) {
            throw new ForbiddenException("Не корректный статус " + event.getState());
        }
        if (event.getParticipantLimit() == 0) {
            throw new ForbiddenException("Участников должно быть больше 0");
        } else {
            if (!event.getRequestModeration()) {
                event.setParticipantLimit(event.getParticipantLimit() - 1);
                eventRepository.save(event);
            }
        }
        eventRequest.setRequester(requester);
        eventRequest.setEvent(event);
        eventRequest.setStatus(RequestStatus.PENDING);
        Request createdCategory = requestRepository.save(eventRequest);
        return RequestMapper.toEventRequestDto(createdCategory);
    }

    @Override
    public List<EventRequestFullDto> getRequest(Long userId) {
        var requester = userRepository.findById(userId);
        if (requester.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден " + userId);
        } else {
            return requestRepository.findEventRequestsByRequester(requester.get())
                    .stream()
                    .map(RequestMapper::toEventRequestDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public EventRequestFullDto updateCancel(Long userId, Long requestId) {
        var requester = userRepository.findById(userId);
        if (requester.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден " + userId);
        }
        var result = requestRepository.findAEventRequestByIdIsAndRequesterIs(requestId, requester.get());
        result.setStatus(RequestStatus.CANCELED);
        var updated = requestRepository.save(result);
        return RequestMapper.toEventRequestDto(updated);

    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestStatus(Long userId,
                                                                 Long eventId,
                                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResultDto eventRequestListDto = new EventRequestStatusUpdateResultDto();
        var requester = userRepository.findById(userId);
        if (requester.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден " + userId);
        }
        var eventOptional = eventRepository.findByInitiatorAndId(requester.get(), eventId);
        if (eventOptional.isEmpty()) {
            throw new ObjectNotFoundException("Событие не найдено " + eventId);
        }
        var event = eventOptional.get();
        if (event.getParticipantLimit() == 0) {
            throw new ForbiddenException("ParticipantLimit is 0");
        } else {
            if (event.getRequestModeration()) {
                event.setParticipantLimit(event.getParticipantLimit() - eventRequestStatusUpdateRequest.getRequestIds().size());
                eventRepository.save(event);
            }
        }
        var result = requestRepository.findEventRequestsByIdInAndEventIs(eventRequestStatusUpdateRequest.getRequestIds(), event);
        if (result.size() == 0) {
            return eventRequestListDto;
        }
        result.forEach(eventRequest -> {
            eventRequest.setStatus(eventRequestStatusUpdateRequest.getStatus());
            requestRepository.save(eventRequest);
        });
        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.REJECTED) {
            eventRequestListDto.setRejectedRequests(requestRepository.findEventRequestsByIdInAndEventIs(eventRequestStatusUpdateRequest.getRequestIds(), event)
                    .stream()
                    .map(RequestMapper::toEventRequestDtoPart)
                    .collect(Collectors.toList()));
        } else {
            eventRequestListDto.setConfirmedRequests(requestRepository.findEventRequestsByIdInAndEventIs(eventRequestStatusUpdateRequest.getRequestIds(), event)
                    .stream()
                    .map(RequestMapper::toEventRequestDtoPart)
                    .collect(Collectors.toList()));
        }
        return eventRequestListDto;
    }
}