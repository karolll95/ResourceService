package com.computeams.mappers;

import com.computeams.models.Resource;
import com.computeams.models.ResourceDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ResourceDtoMapper implements Function<Resource, ResourceDto> {

    @Override
    public ResourceDto apply(Resource resource) {
        ResourceDto resourceDto = new ResourceDto();

        resourceDto.setName(resource.getName());
        resourceDto.setResourceUrl(resource.getSiteUrl());
        resourceDto.setId(resource.getId());

        return resourceDto;
    }
}
