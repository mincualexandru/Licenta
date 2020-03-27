package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.FoodRecommendation;

@Repository("foodRecommendationDao")
public interface FoodRecommendationDao extends CrudRepository<FoodRecommendation, Integer> {

	@Override
	Set<FoodRecommendation> findAll();

	FoodRecommendation findByFoodRecommendationIdAndFoodFoodId(Integer recommendationId, Integer foodId);

}
