package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.events.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

public class CompilationDto {
    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    @NotBlank
    String title;
}