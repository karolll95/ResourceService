package com.computeams.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DownloadServiceTest {

    private DownloadService downloadService = new DownloadService();

    @Test
    public void getContentType_correctUrl_success() throws IOException {
        // given
        String url = "https://www.google.pl/";

        // when
        String contentType = downloadService.getContentType(url);

        // then
        assertEquals(MimeTypeUtils.TEXT_HTML_VALUE, contentType);
    }

    @Test(expected = UnknownHostException.class)
    public void getContentType_wrongUrl_throwException() throws IOException {
        // given
        String url = "https://www.googlee.pl/";

        // when
        String contentType = downloadService.getContentType(url);

        // then
        assertEquals(new UnknownHostException(), contentType);
    }

    @Test
    public void getContent_correctUrl_success() throws IOException {
        // given
        String url = "https://www.google.pl/";

        // when
        String contentType = downloadService.getContentType(url);
        Optional<Blob> content = downloadService.getContent(url, contentType);

        //then
        assertTrue(content.isPresent());
    }

}
