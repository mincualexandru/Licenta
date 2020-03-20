package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.TrainingPlan;

@Repository("trainingPlanDao")
public interface TrainingPlanDao extends CrudRepository<TrainingPlan, Integer> {

	@Override
	Set<TrainingPlan> findAll();

	@Query(value = "select * from trainings_plans where trainer_id = ?1 and training_plan_id not in (select ut.training_plan_id from users_trainings ut);", nativeQuery = true)
	Set<TrainingPlan> findAllByTrainingPlanNotAssociated(Integer trainerId);
}
