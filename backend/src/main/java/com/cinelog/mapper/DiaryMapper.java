package com.cinelog.mapper;

import com.cinelog.dto.DiaryEntryDto;
import com.cinelog.entity.DiaryEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MovieMapper.class)
public interface DiaryMapper {

    DiaryEntryDto toDto(DiaryEntry entry);
}
