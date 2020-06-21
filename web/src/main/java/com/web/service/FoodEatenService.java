package com.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Food;
import com.web.model.FoodEaten;

public interface FoodEatenService {
	void save(FoodEaten foodEaten);

	Optional<FoodEaten> findById(Integer id);

	void delete(FoodEaten foodEaten);

	void deleteById(Integer id);

	Set<FoodEaten> findAll();

	Set<FoodEaten> findAllByUserAccountId(Integer accountId);

	Set<FoodEaten> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId, Timestamp timestampStartDate,
			Timestamp timestampEndDate);

	Optional<FoodEaten> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId);

	boolean checkIfTenDaysFoodHavePassed(boolean passedTenDaysFoodEaten, LocalDateTime dateTimeNow, Integer accountId);

	void addFoodToEat(Account account, Food food);
}
