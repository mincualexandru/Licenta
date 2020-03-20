package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.ExerciseAdvice;

public interface ExerciseAdviceService {

	void save(ExerciseAdvice exerciseAdvice);

	Optional<ExerciseAdvice> findById(Integer id);

	void delete(ExerciseAdvice exerciseAdvice);

	void deleteById(Integer id);

	Set<ExerciseAdvice> findAll();

	ExerciseAdvice findByExerciseAdviceIdAndExerciseExerciseId(Integer adviceId, Integer exerciseId);
}
