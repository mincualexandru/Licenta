package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.ExerciseAdvice;

@Repository("exerciseAdviceDao")
public interface ExerciseAdviceDao extends CrudRepository<ExerciseAdvice, Integer> {

	@Override
	Set<ExerciseAdvice> findAll();

	ExerciseAdvice findByExerciseAdviceIdAndExerciseExerciseId(Integer adviceId, Integer exerciseId);

}
