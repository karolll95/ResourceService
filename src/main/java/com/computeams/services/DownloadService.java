package com.computeams.services;

import com.computeams.util.HibernateUtil;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.util.Optional;


@Service
@Slf4j
public class DownloadService {

    private void checkConnection(String url) {
        int statusCode;

        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);

            log.info("Connecting with {}", url);
            connection.connect();
            statusCode = connection.getResponseCode();

            if (statusCode == 200) {
                log.info("Connection ok.");
            } else {
                log.info("Can't establish connection with request {} ",url);
                throw new NotFoundException("Can't establish connection with given address: " + url);
            }
        } catch (NotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getContentType(String url) throws IOException {
        URL siteURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
        connection.setRequestMethod("HEAD");

        if (isRedirect(connection.getResponseCode())) {
            String newUrl = connection.getHeaderField("Location");
            log.info("Original request URL: '{}' redirected to: '{}'", siteURL, newUrl);
            return getContentType(newUrl);
        }
        return StringUtils.substringBefore(connection.getContentType(), ";");
    }

    private boolean isRedirect(int statusCode) {
        if (statusCode != HttpURLConnection.HTTP_OK) {
            return statusCode == HttpURLConnection.HTTP_MOVED_TEMP
                   || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                   || statusCode == HttpURLConnection.HTTP_SEE_OTHER;
        }
        return false;
    }

    public Optional<Blob> getContent(String url, String contentType) {
        if (!contentType.equals(MimeTypeUtils.TEXT_HTML_VALUE)) {
            synchronized (this) {
                log.info("Synchronized download");
                return downloadContent(url);
            }
        } else {
            log.info("Asynchronous download");
            return downloadContent(url);
        }
    }

    private Optional<Blob> downloadContent(String url) {
        checkConnection(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse;

        Optional<Blob> blob = Optional.empty();

        try {
            httpResponse = client.execute(httpGet);
            HttpEntity fileEntity = httpResponse.getEntity();

            blob = Optional.ofNullable(Hibernate.getLobCreator(HibernateUtil.getSessionFactory()
                                                                            .openSession())
                                                .createBlob(fileEntity.getContent()
                                                                      .readAllBytes()));
        } catch (IOException e) {
            log.error("Can't find content of {}", url);
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }
        return blob;
    }


}
