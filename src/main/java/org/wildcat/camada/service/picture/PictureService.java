package org.wildcat.camada.service.picture;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.wildcat.camada.service.pojo.PictureData;

import java.util.List;

public interface PictureService {

    List<PictureData> getPicturesData(double width, int amount, int sample);

    void setPictureData(double destWidth, int amount, int sample, ImageView imageBanner, AnchorPane secondaryBar, AnchorPane backgroundPane);
}
