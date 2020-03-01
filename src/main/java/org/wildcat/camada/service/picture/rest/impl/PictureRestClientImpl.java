package org.wildcat.camada.service.picture.rest.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wildcat.camada.service.picture.rest.PictureRestClient;
import org.wildcat.camada.service.picture.rest.model.PictureResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class PictureRestClientImpl implements PictureRestClient {

    @Value("${pixabay.api.key}")
    private String apiKey;

    private static final String USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";

    @Override
    public PictureResponse fetchPictures(int sample) {
        RestTemplate restTemplate = new RestTemplate();
        String photosUrl = "https://pixabay.com/api/?key=" + apiKey + "&q=nature&image_type=photo&pretty=true&min_width=1280&per_page=" + sample;
        ResponseEntity<PictureResponse> response = restTemplate.getForEntity(photosUrl, PictureResponse.class);
        return response.getBody();
    }

    @Override
    public BufferedImage fetchPicture(String largeImageURL) {
        BufferedImage fullImage = null;
        try {
            URL url = new URL(largeImageURL);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT_STRING);
            fullImage = ImageIO.read(connection.getInputStream());
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return fullImage;
    }

}
