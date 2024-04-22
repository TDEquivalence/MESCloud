package com.alcegory.mescloud.azure.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "image_annotation")
public class ImageAnnotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageAnnotation")
    private List<AnnotationEntity> annotations;

    private String modelDecision;

    private String userDecision;

    private String classification;

    private boolean userApproval;

    private List<String> rejection;

    private String comments;

    private String logDecision;
}
