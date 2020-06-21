package com.web.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Exercise;

public interface ExerciseService {
	void save(Exercise exercise);

	Optional<Exercise> findById(Integer id);

	void delete(Exercise exercise);

	void deleteById(Integer id);

	Set<Exercise> findAll();

	Set<Exercise> findAllByTrainingPlanHelperPlanId(Integer helperPlanId);

	Set<Exercise> findAllNotPerfomerdExercisesForTrainingPlanId(Integer trainingPlanId);

	Set<Exercise> findAllNotPerfomerdExercises();

	Set<Exercise> findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(Integer trainingPlanId, String string);

	Optional<Exercise> findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId);

	Map<Exercise, Long> sortExercises(Map<Exercise, Long> unsortMap, final boolean order);

	boolean checkExercise(Account account, String exerciseId);
}
