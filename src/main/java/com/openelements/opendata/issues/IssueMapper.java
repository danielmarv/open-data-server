package com.openelements.opendata.issues;

import com.openelements.opendata.base.DtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssueMapper extends DtoMapper<IssueDTO, Issue> {

    @Override
    @Mapping(target = "uuid", source = "id")
    IssueDTO entityToDto(Issue entity);

    @Override
    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "createdAt", ignore = true)
    Issue dtoToEntity(IssueDTO dto);
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "uuid", ignore = true)  // Ignore uuid to avoid ambiguity
    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "body", source = "body")
    @Mapping(target = "closedAtInGitHub", source = "closedAtInGitHub")
    @Mapping(target = "commentCount", source = "commentCount")
    @Mapping(target = "createdAtInGitHub", source = "createdAtInGitHub")
    @Mapping(target = "gitHubId", source = "gitHubId")
    @Mapping(target = "labels", source = "labels")
    @Mapping(target = "lastUpdateInGitHub", source = "lastUpdateInGitHub")
    @Mapping(target = "open", source = "open")
    @Mapping(target = "org", source = "org")
    @Mapping(target = "repository", source = "repository")
    @Mapping(target = "title", source = "title")
    Issue updateEntityFromDto(IssueDTO dto, @MappingTarget Issue entity);
    
    @Override
    Issue updateEntity(Issue updated, @MappingTarget Issue toUpdate);
}
