package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.FoodEaten;

public interface FoodEatenService {
	void save(FoodEaten foodEaten);

	Optional<FoodEaten> findById(Integer id);

	void delete(FoodEaten foodEaten);

	void deleteById(Integer id);

	Set<FoodEaten> findAll();

	Set<FoodEaten> findAllByUserAccountId(Integer accountId);
}
