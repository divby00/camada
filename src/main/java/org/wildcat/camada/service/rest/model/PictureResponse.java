package org.wildcat.camada.service.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PictureResponse {

    private String total;
    private String totalHits;
    private List<Hit> hits;
}
