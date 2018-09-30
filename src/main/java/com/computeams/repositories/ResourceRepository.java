package com.computeams.repositories;

import com.computeams.models.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Long> {

    Iterable<Resource> findAllByNameContainingIgnoreCase(String name);
}
