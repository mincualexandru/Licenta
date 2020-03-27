package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.FoodEaten;

@Repository("foodEatenDao")
public interface FoodEatenDao extends CrudRepository<FoodEaten, Integer> {

	@Override
	Set<FoodEaten> findAll();

	Set<FoodEaten> findAllByUserAccountId(Integer accountId);

}