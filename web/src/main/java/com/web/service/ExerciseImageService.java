package com.web.service;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.web.multipart.MultipartFile;

import com.web.model.ExerciseImage;
import com.web.model.HelperPlan;

public interface ExerciseImageService {

	void save(ExerciseImage exerciseImage);

	Optional<ExerciseImage> findById(Integer id);

	void delete(ExerciseImage exerciseImage);

	void deleteById(Integer id);

	Set<ExerciseImage> findAll();

	void savePhotoImage(ExerciseImage exerciseImage, MultipartFile imageFile, HelperPlan helperPlan) throws Exception;

	void saveImage(MultipartFile imageFile, ExerciseImage exerciseImage, HelperPlan helperPlan) throws Exception;

	void deleteByExerciseImagesIdAndExerciseExerciseId(Integer photoId, Integer exerciseId);

	@Transactional
	void deleteImage(ExerciseImage exerciseImage);
}
