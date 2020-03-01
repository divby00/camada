package org.wildcat.camada.service.picture.impl;

import de.androidpit.colorthief.ColorThief;
import de.androidpit.colorthief.RGBUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.wildcat.camada.service.picture.PictureService;
import org.wildcat.camada.service.picture.rest.impl.PictureRestClientImpl;
import org.wildcat.camada.service.picture.rest.model.Hit;
import org.wildcat.camada.service.picture.rest.model.PictureResponse;
import org.wildcat.camada.service.pojo.PictureData;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PictureServiceImpl implements PictureService {

    @Resource
    private final PictureRestClientImpl pictureRestClient;

    private List<PictureData> pictureDataList = new LinkedList<>();

    public PictureServiceImpl(PictureRestClientImpl pictureRestClient) {
        this.pictureRestClient = pictureRestClient;
    }

    @Override
    public List<PictureData> getPicturesData(double destWidth, int amount, int sample) {
        if (this.pictureDataList.size() == 0) {
            this.pictureDataList = getPicturesDataFromRestAPI(destWidth, amount, sample);
            if (this.pictureDataList.size() == 0) {
                this.pictureDataList = getPicturesDataFromBundle(amount);
            }
        }
        return this.pictureDataList;
    }

    @Override
    public void setPictureData(double destWidth, int amount, int sample, ImageView imageBanner, AnchorPane secondaryBar, AnchorPane backgroundPane) {
        List<PictureData> picturesData = getPicturesData(destWidth, amount, sample);
        int pictureIndex = RandomUtils.nextInt(0, picturesData.size());
        PictureData pictureData = picturesData.get(pictureIndex);
        String hexColor = Integer.toHexString(RGBUtil.packRGB(pictureData.getColor()));
        if (imageBanner != null) {
            imageBanner.setImage(pictureData.getImage());
        }
        if (secondaryBar != null) {
            secondaryBar.setStyle("-fx-background-color: #" + hexColor); // #f3daaa is the fallback color
        }
        if (backgroundPane != null) {
            backgroundPane.setStyle(" -fx-background-color: linear-gradient(white 10%, #" + hexColor + " 90%)");
        }
    }

    private List<PictureData> getPicturesDataFromRestAPI(double destWidth, int amount, int sample) {
        Set<PictureData> pictures = new HashSet<>();
        try {
            PictureResponse pictureResponse = pictureRestClient.fetchPictures(sample);
            List<Hit> hits = pictureResponse.getHits();
            if (!CollectionUtils.isEmpty(hits)) {
                do {
                    int pictureIndex = RandomUtils.nextInt(0, hits.size());
                    String largeImageURL = hits.get(pictureIndex).getLargeImageURL();
                    BufferedImage fullImage = pictureRestClient.fetchPicture(largeImageURL);
                    log.info(largeImageURL);
                    BufferedImage image = processPicture(fullImage, destWidth);
                    pictures.add(buildPictureData(image));
                    // TODO: Remove this line to avoid saving the picture in the hard disk
                    ImageIO.write(image, "png", new File(hits.get(pictureIndex).getId() + ".png"));
                } while (pictures.size() < amount);
            }
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return new LinkedList<>(pictures);
    }

    private List<PictureData> getPicturesDataFromBundle(int amount) {
        Set<PictureData> pictures = new HashSet<>();
        try {
            do {
                int pictureIndex = RandomUtils.nextInt(0, 10);
                String name = "back0" + pictureIndex + ".png";
                Image image = new Image(name, false);
                pictures.add(buildPictureData(image));
            } while (pictures.size() < amount);
        } catch (Exception ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return new LinkedList<>(pictures);
    }

    private BufferedImage processPicture(BufferedImage fullImage, double destWidth) {
        int height = fullImage.getHeight();
        int width = fullImage.getWidth();
        double aspectRatioW = destWidth / (double) width;
        int newHeight = (int) (height * aspectRatioW);
        BufferedImage scaledImage = new BufferedImage((int) destWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(fullImage, 0, 0, (int) destWidth, newHeight, 0, 0, width, height, null);
        g.dispose();
        return scaledImage.getSubimage(0, (height / 2) - 56, (int) destWidth, 113);
    }

    private PictureData buildPictureData(BufferedImage image) {
        Image fxImage = SwingFXUtils.toFXImage(image, null);
        int[] color = ColorThief.getColor(image);
        return PictureData.builder()
                .image(fxImage)
                .color(color)
                .build();
    }

    private PictureData buildPictureData(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        int[] color = ColorThief.getColor(bufferedImage);
        return PictureData.builder()
                .image(image)
                .color(color)
                .build();
    }

}
