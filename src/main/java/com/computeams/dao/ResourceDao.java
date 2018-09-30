package com.computeams.dao;

import com.computeams.models.Resource;
import com.computeams.repositories.ResourceRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ResourceDao {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceDao(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource findById(long id) throws NotFoundException {
        Optional<Resource> resourceOpt = resourceRepository.findById(id);

        if (resourceOpt.isPresent()) {
            return resourceOpt.get();
        } else {
            log.error("Resource with id = {} not found.", id);
            throw new NotFoundException("Resource with id = " + id + " not found.");
        }
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Iterable<Resource> findByName(String name) {
        return resourceRepository.findAllByNameContainingIgnoreCase(name);
    }

    public void deleteById(long id) {
        resourceRepository.deleteById(id);
    }
}
