package com.web.dao;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.FoodImage;

@Repository("foodImageDao")
public interface FoodImageDao extends CrudRepository<FoodImage, Integer> {

	@Override
	Set<FoodImage> findAll();

	@Transactional
	void deleteByFoodImageIdAndFoodFoodId(Integer imageId, Integer foodId);

}