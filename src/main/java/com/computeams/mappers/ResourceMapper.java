package com.computeams.mappers;

import com.computeams.models.Resource;
import com.computeams.models.ResourceDto;
import com.computeams.services.DownloadService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Blob;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class ResourceMapper implements Function<ResourceDto, Resource> {

    private final DownloadService downloadService;

    @Autowired
    public ResourceMapper(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Override
    public Resource apply(ResourceDto resourceDto) {
        Resource resource = new Resource();
        String resourceUrl = resourceDto.getResourceUrl();
        String contentType = "";

        resource.setId(resourceDto.getId());
        resource.setSiteUrl(resourceUrl);
        try {
            resource.setName(resourceDto.getName());
            contentType = downloadService.getContentType(resourceUrl);
            resource.setContentType(contentType);
        } catch (IOException e) {
            log.error("I/O exception.");
            e.printStackTrace();
        }

        Optional<Blob> contentOpt = downloadService.getContent(resourceUrl, contentType);
        contentOpt.ifPresentOrElse(resource::setContent, () -> {
            try {
                log.error("Content of {} request not found", resourceUrl);
                throw new NotFoundException("Content of " + resourceUrl + " not found.");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });
        return resource;
    }
}
