package com.computeams.services;

import com.computeams.dao.ResourceDao;
import com.computeams.mappers.ResourceDtoMapper;
import com.computeams.mappers.ResourceMapper;
import com.computeams.models.Resource;
import com.computeams.models.ResourceDto;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class ResourceService {

    private final ResourceDao resourceDao;
    private final ResourceMapper resourceMapper;
    private final ResourceDtoMapper resourceDtoMapper;

    @Autowired
    public ResourceService(ResourceDao resourceDao, ResourceMapper resourceMapper, ResourceDtoMapper resourceDtoMapper) {
        this.resourceDao = resourceDao;
        this.resourceMapper = resourceMapper;
        this.resourceDtoMapper = resourceDtoMapper;
    }

    public List<ResourceDto> findByIds(List<String> ids) throws NotFoundException {
        return ids.stream()
                  .map(Long::valueOf)
                  .distinct()
                  .map(id -> {
                      try {
                          return resourceDao.findById(id);
                      } catch (NotFoundException e) {
                          e.printStackTrace();
                      }
                      return new Resource();
                  })
                  .map(resourceDtoMapper)
                  .collect(toList());
    }

    public ResourceDto save(ResourceDto resourceDto) {
        return resourceDtoMapper.apply(resourceDao.save(resourceMapper.apply(resourceDto)));
    }

    public List<ResourceDto> findByName(String query) {
        return StreamSupport.stream(resourceDao.findByName(query).spliterator(), true)
                            .map(resourceDtoMapper)
                            .collect(toList());
    }

    public Resource findById(Long id) throws NotFoundException {
        return resourceDao.findById(id);
    }

    @Transactional
    public void sendContent(Resource resource, HttpServletResponse response) {
        try {
            InputStream is = resource.getContent().getBinaryStream();

            TikaConfig config = TikaConfig.getDefaultConfig();
            MimeType mimeType = config.getMimeRepository().forName(resource.getContentType());
            String extension = mimeType.getExtension();

            log.info("Setting content-type for {}", resource.getContentType());
            response.setContentType(mimeType.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getName() + extension + "\"");

            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (MimeTypeException e) {
            log.error("Can't find given mime type.");
            e.printStackTrace();
        } catch (IOException | SQLException e) {
            log.error("Problem with file I/O. ");
            e.printStackTrace();
        }
    }

    public void deleteById(long id) {
        resourceDao.deleteById(id);
    }
}
