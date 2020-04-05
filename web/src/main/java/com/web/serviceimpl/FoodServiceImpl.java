package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodDao;
import com.web.model.Food;
import com.web.service.FoodService;

@Service("foodService")
public class FoodServiceImpl implements FoodService {

	@Autowired
	private FoodDao foodDao;

	@Override
	public void save(Food food) {
		foodDao.save(food);
	}

	@Override
	public Optional<Food> findById(Integer id) {
		return foodDao.findById(id);
	}

	@Override
	public void delete(Food food) {
		foodDao.delete(food);

	}

	@Override
	public void deleteById(Integer id) {
		foodDao.deleteById(id);
	}

	@Override
	public Set<Food> findAll() {
		return foodDao.findAll();
	}

	@Override
	public Set<Food> findAllByDietPlanHelperPlanId(Integer helperPlanId) {
		return foodDao.findAllByDietPlanHelperPlanId(helperPlanId);
	}

	@Override
	public Set<Food> findAllNotEatenFoodsForDietPlanId(Integer dietPlanId) {
		return foodDao.findAllNotEatenFoodsForDietPlanId(dietPlanId);
	}

	@Override
	public Set<Food> findAllNotEatenFoods() {
		return foodDao.findAllNotEatenFoods();
	}

	@Override
	public Set<Food> findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(Integer dietPlanId, String string) {
		return foodDao.findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(dietPlanId, string);
	}

	@Override
	public Optional<Food> findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId) {
		return foodDao.findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId);
	}

}
