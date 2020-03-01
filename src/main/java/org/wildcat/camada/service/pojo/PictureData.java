package org.wildcat.camada.service.pojo;

import javafx.scene.image.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PictureData {
    private Image image;
    private int[] color;
}
