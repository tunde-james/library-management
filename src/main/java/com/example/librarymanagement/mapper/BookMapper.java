package com.example.librarymanagement.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.example.librarymanagement.dto.book.BookRequestDto;
import com.example.librarymanagement.dto.book.BookResponseDto;
import com.example.librarymanagement.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookResponseDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Book toEntity(BookRequestDto bookRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(BookRequestDto dto, @MappingTarget Book book);
}
