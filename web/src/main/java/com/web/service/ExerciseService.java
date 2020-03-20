package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Exercise;

public interface ExerciseService {
	void save(Exercise exercise);

	Optional<Exercise> findById(Integer id);

	void delete(Exercise exercise);

	void deleteById(Integer id);

	Set<Exercise> findAll();

	Set<Exercise> findAllByTrainingPlanTrainingPlanId(Integer trainingPlanId);

	Set<Exercise> findAllNotPerfomerdExercisesForTrainingPlanId(Integer trainingPlanId);

	Set<Exercise> findAllNotPerfomerdExercises();
}
