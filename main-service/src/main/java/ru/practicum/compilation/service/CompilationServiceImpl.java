package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(new Compilation(), newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        Compilation createdCompilation = repository.save(compilation);
        return CompilationMapper.toCompilationDto(createdCompilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long id, NewCompilationDto newCompilationDto) {
        var compilationOptional = repository.findById(id);
        if (compilationOptional.isPresent()) {
            var compilation = compilationOptional.get();
            CompilationMapper.toCompilation(compilation, newCompilationDto);
            compilation.setId(id);
            if (newCompilationDto.getEvents() != null) {
                compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
            }
            return CompilationMapper.toCompilationDto(compilation);
        } else {
            throw new ObjectNotFoundException("Подборка не найдена");
        }
    }

    @Transactional
    @Override
    public void removeCompilation(Long id) {
        repository.deleteAllById(Collections.singleton(id));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAllByPinnedIs(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.toCompilationDto(repository.findCompilationById(compId));
    }
}