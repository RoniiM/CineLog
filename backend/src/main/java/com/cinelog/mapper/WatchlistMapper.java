package com.cinelog.mapper;

import com.cinelog.dto.WatchlistItemDto;
import com.cinelog.entity.WatchlistItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MovieMapper.class)
public interface WatchlistMapper {

    WatchlistItemDto toDto(WatchlistItem item);
}
