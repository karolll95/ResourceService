package com.computeams.controllers;

import com.computeams.models.Resource;
import com.computeams.models.ResourceDto;
import com.computeams.services.ResourceService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(path = "/resources")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto addResource(@RequestBody ResourceDto resourceDto) {
        return resourceService.save(resourceDto);
    }

    @GetMapping(path = "/resources/many/{ids}")
    public List<ResourceDto> getResourcesByIds(@PathVariable String ids) throws NotFoundException {
        List<String> idsList = Arrays.asList(ids.split(","));
        return resourceService.findByIds(idsList);
    }

    @GetMapping(path = "/resources/{id}")
    public void getResourceById(@PathVariable Long id, HttpServletResponse response) throws NotFoundException {
        Resource resource = resourceService.findById(id);
        resourceService.sendContent(resource, response);
    }

    @GetMapping(path = "/resources", params = "search")
    public List<ResourceDto> findResourceByName(@RequestParam String search) {
        return resourceService.findByName(search);
    }

    @DeleteMapping(path = "resources/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteResource(@PathVariable long id) {
        resourceService.deleteById(id);
    }
}
