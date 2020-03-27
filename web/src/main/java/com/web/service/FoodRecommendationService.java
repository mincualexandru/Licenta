package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.FoodRecommendation;

public interface FoodRecommendationService {
	void save(FoodRecommendation foodRecommendation);

	Optional<FoodRecommendation> findById(Integer id);

	void delete(FoodRecommendation foodRecommendation);

	void deleteById(Integer id);

	Set<FoodRecommendation> findAll();

	FoodRecommendation findByFoodRecommendationIdAndFoodFoodId(Integer recommendationId, Integer foodId);
}
