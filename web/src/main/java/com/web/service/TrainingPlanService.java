package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.TrainingPlan;

public interface TrainingPlanService {

	void save(TrainingPlan trainingPlan);

	Optional<TrainingPlan> findById(Integer id);

	void delete(TrainingPlan trainingPlan);

	void deleteById(Integer id);

	Set<TrainingPlan> findAll();

	Set<TrainingPlan> findAllByTrainingPlanNotAssociated(Integer trainerId);
}
