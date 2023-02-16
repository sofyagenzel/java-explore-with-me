package ru.practicum.mapper;


import ru.practicum.StatisticRequestDto;
import ru.practicum.StatisticResponseDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatMapper {
    public static final DateTimeFormatter DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static StatisticResponseDto toStatDto(Stat stat) {
        return StatisticResponseDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .build();
    }

    public static Stat toStat(StatisticRequestDto statDto) {
        return new Stat(
                statDto.getId(),
                statDto.getApp() != null ? statDto.getApp() : null,
                statDto.getUri() != null ? statDto.getUri() : null,
                statDto.getIp() != null ? statDto.getIp() : null,
                LocalDateTime.now().withNano(0));
    }
}