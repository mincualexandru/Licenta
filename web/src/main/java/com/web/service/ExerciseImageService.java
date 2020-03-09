package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.web.model.ExerciseImage;

public interface ExerciseImageService {

	void save(ExerciseImage exerciseImage);

	Optional<ExerciseImage> findById(Integer id);

	void delete(ExerciseImage exerciseImage);

	void deleteById(Integer id);

	Set<ExerciseImage> findAll();

	void savePhotoImage(ExerciseImage exerciseImage, MultipartFile imageFile) throws Exception;

	void saveImage(MultipartFile imageFile, ExerciseImage exerciseImage) throws Exception;

	void deleteByExerciseImagesIdAndExerciseExerciseId(Integer photoId, Integer exerciseId);
}
