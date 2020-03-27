package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodEatenDao;
import com.web.model.FoodEaten;
import com.web.service.FoodEatenService;

@Service("foodEatenService")
public class FoodEatenServiceImpl implements FoodEatenService {

	@Autowired
	private FoodEatenDao foodEatenDao;

	@Override
	public void save(FoodEaten foodEaten) {
		foodEatenDao.save(foodEaten);
	}

	@Override
	public Optional<FoodEaten> findById(Integer id) {
		return foodEatenDao.findById(id);
	}

	@Override
	public void delete(FoodEaten foodEaten) {
		foodEatenDao.delete(foodEaten);
	}

	@Override
	public void deleteById(Integer id) {
		foodEatenDao.deleteById(id);
	}

	@Override
	public Set<FoodEaten> findAll() {
		return foodEatenDao.findAll();
	}

	@Override
	public Set<FoodEaten> findAllByUserAccountId(Integer accountId) {
		return foodEatenDao.findAllByUserAccountId(accountId);
	}

}
