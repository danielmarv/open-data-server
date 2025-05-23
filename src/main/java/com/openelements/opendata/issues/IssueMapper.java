package com.openelements.opendata.issues;

import com.openelements.opendata.base.DtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssueMapper extends DtoMapper<IssueDTO, Issue> {

    @Override
    @Mapping(target = "uuid", source = "id")
    IssueDTO toDto(Issue entity);

    @Override
    @Mapping(target = "id", source = "uuid")
    Issue toEntity(IssueDTO dto);
}
