package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Exercise;

@Repository("exerciseDao")
public interface ExerciseDao extends CrudRepository<Exercise, Integer> {

	@Override
	Set<Exercise> findAll();

	Set<Exercise> findAllByTrainingPlanTrainingPlanId(Integer trainingPlanId);

	@Query(value = "select * from exercises where training_plan_id = ?1 and exercise_id not in (select ed.exercise_id from exercises_done ed);", nativeQuery = true)
	Set<Exercise> findAllNotPerfomerdExercisesForTrainingPlanId(Integer trainingPlanId);

}
