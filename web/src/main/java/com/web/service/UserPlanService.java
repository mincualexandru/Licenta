package com.web.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.HelperPlan;
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

	Optional<UserPlan> findTopByOrderByDateOfPurchaseDesc();

	Set<UserPlan> findAllByHelperPlanHelperAccountIdAndDateOfPurchaseBetween(Integer accountId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	Set<UserPlan> findAllByHelperPlanHelperPlanId(int helperPlanId);

	void createUserPlan(Account account, HelperPlan dietPlan);
}