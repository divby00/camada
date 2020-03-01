package org.wildcat.camada.service.picture.rest;

import org.wildcat.camada.service.picture.rest.model.PictureResponse;

import java.awt.image.BufferedImage;

public interface PictureRestClient {

    PictureResponse fetchPictures(int sample);

    BufferedImage fetchPicture(String largeImageURL);
}
