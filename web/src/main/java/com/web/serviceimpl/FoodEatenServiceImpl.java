package com.web.serviceimpl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodEatenDao;
import com.web.model.Account;
import com.web.model.Food;
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

	@Override
	public Set<FoodEaten> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return foodEatenDao.findAllByUserAccountIdAndDateOfExecutionBetween(accountId, timestampStartDate,
				timestampEndDate);
	}

	@Override
	public Optional<FoodEaten> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId) {
		return foodEatenDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId);
	}

	@Override
	public boolean checkIfTenDaysFoodHavePassed(boolean passedTenDaysFoodEaten, LocalDateTime dateTimeNow,
			Integer accountId) {
		if (foodEatenDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			FoodEaten foodEaten = foodEatenDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).get();
			Duration duration = Duration.between(foodEaten.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				passedTenDaysFoodEaten = true;
			}
		}
		return passedTenDaysFoodEaten;
	}

	@Override
	public void addFoodToEat(Account account, Food food) {
		FoodEaten foodEaten = new FoodEaten();
		foodEaten.setFood(food);
		foodEaten.setUser(account);
		foodEatenDao.save(foodEaten);
	}

}
