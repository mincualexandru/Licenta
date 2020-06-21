package com.web.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Food;

public interface FoodService {
	void save(Food food);

	Optional<Food> findById(Integer id);

	void delete(Food food);

	void deleteById(Integer id);

	Set<Food> findAll();

	Set<Food> findAllByDietPlanHelperPlanId(Integer helperPlanId);

	Set<Food> findAllNotEatenFoodsForDietPlanId(Integer dietPlanId);

	Set<Food> findAllNotEatenFoods();

	Set<Food> findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(Integer trainingPlanId, String string);

	Optional<Food> findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId);

	Map<Food, Long> sortFoods(Map<Food, Long> unsortMap, final boolean order);

	boolean checkFood(Account account, String foodId);

}
