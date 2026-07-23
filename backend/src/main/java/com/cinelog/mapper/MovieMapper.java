package com.cinelog.mapper;

import com.cinelog.dto.MovieDto;
import com.cinelog.dto.tmdb.TmdbMovieResponse;
import com.cinelog.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class MovieMapper {

    @Value("${tmdb.api.image-base-url}")
    protected String imageBaseUrl;

    @Mapping(target = "overview", ignore = true)
    @Mapping(target = "posterUrl", expression = "java(buildImageUrl(movie.getPosterPath()))")
    @Mapping(target = "backdropUrl", expression = "java(buildImageUrl(movie.getBackdropPath()))")
    public abstract MovieDto toDto(Movie movie);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tmdbId", source = "id")
    @Mapping(target = "posterUrl", expression = "java(buildImageUrl(response.posterPath()))")
    @Mapping(target = "backdropUrl", expression = "java(buildImageUrl(response.backdropPath()))")
    public abstract MovieDto toDto(TmdbMovieResponse response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tmdbId", source = "id")
    @Mapping(target = "lastSynced", ignore = true)
    public abstract Movie toEntity(TmdbMovieResponse response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tmdbId", ignore = true)
    @Mapping(target = "lastSynced", ignore = true)
    public abstract void updateEntityFromResponse(TmdbMovieResponse response, @MappingTarget Movie movie);

    @Named("buildImageUrl")
    protected String buildImageUrl(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return imageBaseUrl + path;
    }
}
