package com.web.dao;

import org.springframework.web.multipart.MultipartFile;

import com.web.model.FoodImage;

public interface IFoodImageDao {
	void saveImage(MultipartFile imageFile, FoodImage foodImage) throws Exception;
}
