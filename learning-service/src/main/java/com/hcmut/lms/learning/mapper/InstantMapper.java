package com.hcmut.lms.learning.mapper;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * MapStruct helper for converting {@link Instant} fields to ISO-8601 strings.
 * Used via {@code uses = InstantMapper.class} in other mappers.
 */
@Mapper(componentModel = "spring")
public interface InstantMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    default String toString(Instant instant) {
        return instant != null ? FORMATTER.format(instant) : null;
    }
}
