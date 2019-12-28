package org.wildcat.camada.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.wildcat.camada.service.rest.model.Hit;
import org.wildcat.camada.service.rest.model.PictureResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
public class PictureRestClientImpl {

    @Value("${api.key}")
    private String apiKey;

    private static final String USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36";

    public BufferedImage getPicture(double destWidth) {
        BufferedImage image = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String photosUrl = "https://pixabay.com/api/?key=" + apiKey + "&q=nature&image_type=photo&pretty=true&min_width=1280&per_page=75";
            ResponseEntity<PictureResponse> response = restTemplate.getForEntity(photosUrl, PictureResponse.class);
            PictureResponse body = response.getBody();
            if (body != null) {
                List<Hit> hits = body.getHits();
                if (!CollectionUtils.isEmpty(hits)) {
                    int pictureIndex = RandomUtils.nextInt(0, hits.size());
                    String largeImageURL = hits.get(pictureIndex).getLargeImageURL();
                    URL url = new URL(largeImageURL);
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", USER_AGENT_STRING);
                    BufferedImage fullImage = ImageIO.read(connection.getInputStream());
                    int height = fullImage.getHeight();
                    int width = fullImage.getWidth();
                    double aspectRatioW = (double) destWidth / (double) width;
                    int newHeight = (int) (height * aspectRatioW);
                    BufferedImage scaledImage = new BufferedImage((int) destWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaledImage.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(fullImage, 0, 0, (int) destWidth, newHeight, 0, 0, width, height, null);
                    g.dispose();
                    image = scaledImage.getSubimage(0, (height / 2) - 56, (int) destWidth, 113);
                    ImageIO.write(image, "png", new File(hits.get(pictureIndex).getId() + ".png"));
                }
            }
        } catch (Exception ex) {
            log.warn(ExceptionUtils.getStackTrace(ex));
        }
        return image;
    }

}
