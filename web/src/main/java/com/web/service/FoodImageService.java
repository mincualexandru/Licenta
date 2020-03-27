package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.web.model.FoodImage;

public interface FoodImageService {
	void save(FoodImage foodImage);

	Optional<FoodImage> findById(Integer id);

	void delete(FoodImage foodImage);

	void deleteById(Integer id);

	Set<FoodImage> findAll();

	void savePhotoImage(FoodImage foodImage, MultipartFile imageFile) throws Exception;

	void saveImage(MultipartFile imageFile, FoodImage foodImage) throws Exception;

	void deleteByFoodImageIdAndFoodFoodId(Integer imageId, Integer foodId);
}
