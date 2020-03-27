package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.UserPlan;

public interface UserPlanService {
	void save(UserPlan userTraining);

	Optional<UserPlan> findById(Integer id);

	void delete(UserPlan userTraining);

	void deleteById(Integer id);

	Set<UserPlan> findAll();

	UserPlan findByUserAccountIdAndHelperPlanHelperPlanId(Integer userId, Integer helperPlanId);

	Set<UserPlan> findAllByUserAccountId(Integer accountId);

	void deleteByUserPlanId(Integer userPlanId);

	Set<UserPlan> findAllByHelperPlanTypeOfPlan(String string);

	Set<UserPlan> findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(boolean b, Integer accountId, String string);

	Set<UserPlan> findAllByBoughtAndUserAccountId(boolean b, Integer accountId);
}