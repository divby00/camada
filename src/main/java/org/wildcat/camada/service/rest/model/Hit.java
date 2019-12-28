package org.wildcat.camada.service.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    private Long id;
    private String pageURL;
    private String type;
    private String tags;
    private String previewURL;
    private Long previewWidth;
    private Long previewHeight;
    private String webformatURL;
    private Long webformatWidth;
    private Long webformatHeight;
    private String largeImageURL;
    private String fullHDURL;
    private String imageURL;
    private Long imageWidth;
    private Long imageHeight;
    private Long imageSize;
}
