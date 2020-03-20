package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.UserTraining;

public interface UserTrainingService {
	void save(UserTraining userTraining);

	Optional<UserTraining> findById(Integer id);

	void delete(UserTraining userTraining);

	void deleteById(Integer id);

	Set<UserTraining> findAll();

	Set<UserTraining> findAllByBoughtAndUserAccountId(boolean b, Integer accountId);

	UserTraining findByUserAccountIdAndTrainingPlanTrainingPlanId(Integer userId, Integer productId);

	Set<UserTraining> findAllByUserAccountId(Integer accountId);
}