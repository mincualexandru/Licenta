package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodRecommendationDao;
import com.web.model.FoodRecommendation;
import com.web.service.FoodRecommendationService;

@Service("foodRecommendationService")
public class FoodRecommendationServiceImpl implements FoodRecommendationService {

	@Autowired
	private FoodRecommendationDao foodRecommendationDao;

	@Override
	public void save(FoodRecommendation foodRecommendation) {
		foodRecommendationDao.save(foodRecommendation);
	}

	@Override
	public Optional<FoodRecommendation> findById(Integer id) {
		return foodRecommendationDao.findById(id);
	}

	@Override
	public void delete(FoodRecommendation foodRecommendation) {
		foodRecommendationDao.delete(foodRecommendation);
	}

	@Override
	public void deleteById(Integer id) {
		foodRecommendationDao.deleteById(id);
	}

	@Override
	public Set<FoodRecommendation> findAll() {
		return foodRecommendationDao.findAll();
	}

	@Override
	public FoodRecommendation findByFoodRecommendationIdAndFoodFoodId(Integer recommendationId, Integer foodId) {
		return foodRecommendationDao.findByFoodRecommendationIdAndFoodFoodId(recommendationId, foodId);
	}

}
