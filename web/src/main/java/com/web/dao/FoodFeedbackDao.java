package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.FoodFeedback;

@Repository("foodFeedbackDao")
public interface FoodFeedbackDao extends CrudRepository<FoodFeedback, Integer> {

	@Override
	Set<FoodFeedback> findAll();

	Set<FoodFeedback> findAllByUserAccountId(Integer integer);
}