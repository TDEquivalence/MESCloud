package com.alcegory.mescloud.azure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "annotation")
public class AnnotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromName;

    private String toName;

    private String type;

    private Integer originalWidth;

    private Integer originalHeight;

    private Double x;

    private Double y;

    private Double width;

    private Double height;

    private Double rotation;

    private List<String> rectangleLabels;

    @ManyToOne
    @JoinColumn(name = "image_annotation_id")
    private ImageAnnotationEntity imageAnnotation;
}
