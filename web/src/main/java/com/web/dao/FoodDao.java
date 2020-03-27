package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Food;

@Repository("foodDao")
public interface FoodDao extends CrudRepository<Food, Integer> {

	@Override
	Set<Food> findAll();

	Set<Food> findAllByDietPlanHelperPlanId(Integer helperPlanId);

	@Query(value = "select * from foods where diet_plan_id = ?1 and food_id not in (select fe.food_id from foods_eaten fe);", nativeQuery = true)
	Set<Food> findAllNotEatenFoodsForDietPlanId(Integer dietPlanId);

	@Query(value = "select * from foods where food_id not in (select fe.food_id from foods_eaten fe);", nativeQuery = true)
	Set<Food> findAllNotEatenFoods();

	Set<Food> findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(Integer dietPlanId, String string);
}
