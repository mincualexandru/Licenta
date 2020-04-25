package com.web.serviceimpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.dao.FoodImageDao;
import com.web.model.FoodImage;
import com.web.service.FoodImageService;

@Service("foodImageService")
public class FoodImageServiceImpl implements FoodImageService {

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
		foodImage.setPath(absolutePath + "/src/main/resources/static/images/plans/diets/");
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

}
