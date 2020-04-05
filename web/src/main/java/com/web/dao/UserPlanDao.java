package com.web.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.UserPlan;

@Repository("userPlanDao")
public interface UserPlanDao extends CrudRepository<UserPlan, Integer> {

	@Override
	Set<UserPlan> findAll();

	Set<UserPlan> findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(boolean b, Integer accountId, String string);

	UserPlan findByUserAccountIdAndHelperPlanHelperPlanId(Integer userId, Integer helperPlanId);

	Set<UserPlan> findAllByUserAccountId(Integer accountId);

	@Transactional
	@Modifying
	@Query(value = "delete from users_plans where user_plan_id = ?1", nativeQuery = true)
	void deleteByUserPlanId(Integer userPlanId);

	Set<UserPlan> findAllByHelperPlanTypeOfPlan(String string);

	Set<UserPlan> findAllByBoughtAndUserAccountId(boolean b, Integer accountId);

	Optional<UserPlan> findTopByOrderByDateOfPurchaseDesc();
}