package com.openelements.opendata.metrics;

import com.openelements.opendata.base.DtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContributorMetricMapper extends DtoMapper<ContributorMetricDTO, ContributorMetric> {

    @Override
    @Mapping(target = "uuid", source = "id")
    ContributorMetricDTO entityToDto(ContributorMetric entity);

    @Override
    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "createdAt", ignore = true)
    ContributorMetric dtoToEntity(ContributorMetricDTO dto);
    
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ContributorMetric updateEntityFromDto(ContributorMetricDTO dto, @MappingTarget ContributorMetric entity);
    
    @Override
    ContributorMetric updateEntity(ContributorMetric updated, @MappingTarget ContributorMetric toUpdate);
}
