package com.web.serviceimpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.dao.FoodImageDao;
import com.web.model.FoodImage;
import com.web.service.FoodImageService;

@Service("foodImageService")
public class FoodImageServiceImpl implements FoodImageService {

	private Logger LOGGER = Logger.getLogger(FoodImageServiceImpl.class);

	@Autowired
	private FoodImageDao foodImageDao;

	@Override
	public void save(FoodImage foodImage) {
		foodImageDao.save(foodImage);
	}

	@Override
	public Optional<FoodImage> findById(Integer id) {
		return foodImageDao.findById(id);
	}

	@Override
	public void delete(FoodImage foodImage) {
		foodImageDao.delete(foodImage);
	}

	@Override
	public void deleteById(Integer id) {
		foodImageDao.deleteById(id);
	}

	@Override
	public Set<FoodImage> findAll() {
		return foodImageDao.findAll();
	}

	@Override
	public void savePhotoImage(FoodImage foodImage, MultipartFile imageFile) throws Exception {
		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		foodImage.setPath(absolutePath + "/uploadedimages/foods/");
		byte[] bytes = imageFile.getBytes();
		Path path = Paths.get(foodImage.getPath() + imageFile.getOriginalFilename());
		Files.write(path, bytes);
	}

	@Override
	public void saveImage(MultipartFile imageFile, FoodImage foodImage) throws Exception {
		savePhotoImage(foodImage, imageFile);
		foodImageDao.save(foodImage);
	}

	@Override
	public void deleteByFoodImageIdAndFoodFoodId(Integer imageId, Integer foodId) {
		foodImageDao.deleteByFoodImageIdAndFoodFoodId(imageId, foodId);
	}

	@Override
	public void deleteImage(FoodImage foodImage) {
		try {
			File file = new File("C:\\Users\\alexandru.mincu\\git\\Licenta\\web" + foodImage.getPath() + "\\"
					+ foodImage.getFileName());
			if (file.delete()) {
				LOGGER.info(file.getName() + " is deleted.");
			} else {
				LOGGER.info("Not working");
			}
		} catch (Exception e) {
			LOGGER.info("Failed delete image.");
		}
	}

}
