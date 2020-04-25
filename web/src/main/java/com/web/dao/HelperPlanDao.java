package com.web.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.HelperPlan;

@Repository("helperPlanDao")
public interface HelperPlanDao extends CrudRepository<HelperPlan, Integer> {

	@Override
	Set<HelperPlan> findAll();

	@Query(value = "select * from helpers_plans where helper_id = ?1 and type_of_plan = 'Antrenament' and helper_plan_id not in (select up.helper_plan_id from users_plans up where up.user_id = ?2);", nativeQuery = true)
	Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId, Integer userId);

	@Query(value = "select * from helpers_plans where helper_id = ?1 and type_of_plan = 'Antrenament' and helper_plan_id not in (select up.helper_plan_id from users_plans up);", nativeQuery = true)
	Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId);

	@Query(value = "select * from helpers_plans where helper_id = ?1 and type_of_plan = 'Dieta' and helper_plan_id not in (select up.helper_plan_id from users_plans up where up.user_id = ?2);", nativeQuery = true)
	Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId, Integer userId);

	@Query(value = "select * from helpers_plans where helper_id = ?1 and type_of_plan = 'Dieta' and helper_plan_id not in (select up.helper_plan_id from users_plans up);", nativeQuery = true)
	Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId);

	@Query(value = "select * from helpers_plans where helper_id = ?1 and helper_plan_id not in (select up.helper_plan_id from users_plans up where up.user_id = ?2);", nativeQuery = true)
	Set<HelperPlan> findAllPlansByHelperIdAndUserIdNotAssociated(Integer helperId, Integer userId);

	Optional<HelperPlan> findByHelperPlanIdAndTypeOfPlan(int parseInt, String string);
}
