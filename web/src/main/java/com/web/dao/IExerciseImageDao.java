package com.web.dao;

import org.springframework.web.multipart.MultipartFile;

import com.web.model.ExerciseImage;

public interface IExerciseImageDao {
	void saveImage(MultipartFile imageFile, ExerciseImage exerciseImage) throws Exception;
}
