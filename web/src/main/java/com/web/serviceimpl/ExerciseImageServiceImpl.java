package com.web.serviceimpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.dao.ExerciseImageDao;
import com.web.model.ExerciseImage;
import com.web.model.HelperPlan;
import com.web.service.ExerciseImageService;
import com.web.utils.Gender;

@Service("exerciseImageService")
public class ExerciseImageServiceImpl implements ExerciseImageService {

	@Autowired
	private ExerciseImageDao exerciseImageDao;

	@Override
	public void save(ExerciseImage exerciseImage) {
		exerciseImageDao.save(exerciseImage);
	}

	@Override
	public Optional<ExerciseImage> findById(Integer id) {
		return exerciseImageDao.findById(id);
	}

	@Override
	public void delete(ExerciseImage exerciseImage) {
		exerciseImageDao.delete(exerciseImage);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseImageDao.deleteById(id);
	}

	@Override
	public Set<ExerciseImage> findAll() {
		return exerciseImageDao.findAll();
	}

	@Override
	public void savePhotoImage(ExerciseImage exerciseImage, MultipartFile imageFile, HelperPlan helperPlan)
			throws Exception {
		Path currentPath = Paths.get(".");
		Path absolutePath = currentPath.toAbsolutePath();
		if (helperPlan.getForWho().equals(Gender.BARBAT)) {
			exerciseImage.setPath(absolutePath + "/src/main/resources/static/images/plans/trainings/man/exercise/");
		} else if (helperPlan.getForWho().equals(Gender.FEMEIE)) {
			exerciseImage.setPath(absolutePath + "/src/main/resources/static/images/plans/trainings/woman/exercise/");
		}
		byte[] bytes = imageFile.getBytes();
		Path path = Paths.get(exerciseImage.getPath() + imageFile.getOriginalFilename());
		Files.write(path, bytes);
	}

	@Override
	public void saveImage(MultipartFile imageFile, ExerciseImage exerciseImage, HelperPlan helperPlan)
			throws Exception {
		savePhotoImage(exerciseImage, imageFile, helperPlan);
		exerciseImageDao.save(exerciseImage);
	}

	@Override
	public void deleteByExerciseImagesIdAndExerciseExerciseId(Integer photoId, Integer exerciseId) {
		exerciseImageDao.deleteByExerciseImagesIdAndExerciseExerciseId(photoId, exerciseId);
	}

}
