package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodFeedbackDao;
import com.web.model.Account;
import com.web.model.Food;
import com.web.model.FoodFeedback;
import com.web.service.FoodFeedbackService;

@Service("foodFeedbackService")
public class FoodFeedbackServiceImpl implements FoodFeedbackService {

	@Autowired
	private FoodFeedbackDao foodFeedbackDao;

	@Override
	public void save(FoodFeedback foodFeedback) {
		foodFeedbackDao.save(foodFeedback);
	}

	@Override
	public Optional<FoodFeedback> findById(Integer id) {
		return foodFeedbackDao.findById(id);
	}

	@Override
	public void delete(FoodFeedback foodFeedback) {
		foodFeedbackDao.delete(foodFeedback);
	}

	@Override
	public void deleteById(Integer id) {
		foodFeedbackDao.deleteById(id);
	}

	@Override
	public Set<FoodFeedback> findAll() {
		return foodFeedbackDao.findAll();
	}

	@Override
	public Set<FoodFeedback> findAllByUserAccountId(Integer integer) {
		return foodFeedbackDao.findAllByUserAccountId(integer);
	}

	@Override
	public void createFeedbackForFood(String messageReview, Integer number, Account user, Food food) {
		FoodFeedback foodFeedback = new FoodFeedback();
		foodFeedback.setFood(food);
		foodFeedback.setMessage(messageReview);
		foodFeedback.setRating(number);
		foodFeedback.setUser(user);
		foodFeedbackDao.save(foodFeedback);
	}

}
