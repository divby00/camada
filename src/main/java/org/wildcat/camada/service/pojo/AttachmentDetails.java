package org.wildcat.camada.service.pojo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AttachmentDetails {
    private long size;
    private byte[] bytes;
    private String filename;
}
