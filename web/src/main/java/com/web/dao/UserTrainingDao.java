package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.UserTraining;

@Repository("userTrainingDao")
public interface UserTrainingDao extends CrudRepository<UserTraining, Integer> {

	@Override
	Set<UserTraining> findAll();

	Set<UserTraining> findAllByBoughtAndUserAccountId(boolean b, Integer accountId);

	UserTraining findByUserAccountIdAndTrainingPlanTrainingPlanId(Integer userId, Integer productId);
}