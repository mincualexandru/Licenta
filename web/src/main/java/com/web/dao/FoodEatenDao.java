package com.web.dao;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.FoodEaten;

@Repository("foodEatenDao")
public interface FoodEatenDao extends CrudRepository<FoodEaten, Integer> {

	@Override
	Set<FoodEaten> findAll();

	Set<FoodEaten> findAllByUserAccountId(Integer accountId);

	Set<FoodEaten> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId, Timestamp timestampStartDate,
			Timestamp timestampEndDate);

	Optional<FoodEaten> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId);
}