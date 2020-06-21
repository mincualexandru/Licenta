package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Food;
import com.web.model.FoodFeedback;

public interface FoodFeedbackService {
	void save(FoodFeedback foodFeedback);

	Optional<FoodFeedback> findById(Integer id);

	void delete(FoodFeedback foodFeedback);

	void deleteById(Integer id);

	Set<FoodFeedback> findAll();

	Set<FoodFeedback> findAllByUserAccountId(Integer integer);

	void createFeedbackForFood(String messageReview, Integer number, Account user, Food food);
}
