package com.controlimage.api.model;

import com.controlimage.api.dto.constants.ImageConstants;
import com.controlimage.api.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Entity
@Table(schema = ImageConstants.SCHEMA)
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    private String filename;
    private String url;
    private String contentType;
    private Long size;
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = false;
}
