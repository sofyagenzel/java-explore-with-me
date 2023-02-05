package ru.practicum.mapper;


import ru.practicum.StatisticRequestDto;
import ru.practicum.StatisticResponseDto;
import ru.practicum.model.Stat;

import java.util.Optional;

public class StatMapper {
    public static StatisticResponseDto toStatDto(Stat stat) {
        return StatisticResponseDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .build();
    }

    public static void toStat(Stat stat, StatisticRequestDto statDto) {
        stat.setId(statDto.getId());
        Optional.ofNullable(statDto.getApp()).ifPresent(stat::setApp);
        Optional.ofNullable(statDto.getUri()).ifPresent(stat::setUri);
        Optional.ofNullable(statDto.getIp()).ifPresent(stat::setIp);
        Optional.ofNullable(statDto.getTimeStamp()).ifPresent(stat::setTimeStamp);
    }
}