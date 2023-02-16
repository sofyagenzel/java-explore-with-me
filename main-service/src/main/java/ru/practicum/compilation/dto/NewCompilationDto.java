package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

public class NewCompilationDto {
    Long id;
    List<Long> events;
    Boolean pinned;
    @NotBlank
    String title;
}