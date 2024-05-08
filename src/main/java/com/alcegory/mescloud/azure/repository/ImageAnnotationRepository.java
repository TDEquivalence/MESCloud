package com.alcegory.mescloud.azure.repository;

import com.alcegory.mescloud.azure.model.ImageAnnotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageAnnotationRepository extends JpaRepository<ImageAnnotationEntity, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM image_annotation WHERE user_id = :userId AND image LIKE %:image%", nativeQuery = true)
    boolean existsByUserIdAndImage(@Param("userId") Long userId, @Param("image") String image);

    @Query(value = "SELECT COUNT(*) FROM image_annotation WHERE image LIKE %:image%", nativeQuery = true)
    int countByImage(@Param("image") String image);

    @Query(value = "SELECT COUNT(*) FROM image_annotation WHERE image LIKE CONCAT(:imageBase, '%') AND status != 'INITIAL'", nativeQuery = true)
    int countByImageAndStatusNotInitial(@Param("imageBase") String imageBase);
}
