package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.HelperPlan;

public interface HelperPlanService {

	void save(HelperPlan trainingPlan);

	Optional<HelperPlan> findById(Integer id);

	void delete(HelperPlan trainingPlan);

	void deleteById(Integer id);

	Set<HelperPlan> findAll();

	Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId);

	Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId, Integer userId);

	Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId);

	Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId, Integer userId);

	Optional<HelperPlan> findByHelperPlanIdAndTypeOfPlan(int parseInt, String string);

}
