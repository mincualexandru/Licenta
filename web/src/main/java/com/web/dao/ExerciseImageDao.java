package com.web.dao;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.ExerciseImage;

@Repository("excerciseImageDao")
public interface ExerciseImageDao extends CrudRepository<ExerciseImage, Integer> {

	@Override
	Set<ExerciseImage> findAll();

	@Transactional
	void deleteByExerciseImagesIdAndExerciseExerciseId(Integer photoId, Integer exerciseId);

}