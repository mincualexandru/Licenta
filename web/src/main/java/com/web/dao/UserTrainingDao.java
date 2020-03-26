package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.UserTraining;

@Repository("userTrainingDao")
public interface UserTrainingDao extends CrudRepository<UserTraining, Integer> {

	@Override
	Set<UserTraining> findAll();

	Set<UserTraining> findAllByBoughtAndUserAccountId(boolean b, Integer accountId);

	UserTraining findByUserAccountIdAndTrainingPlanTrainingPlanId(Integer userId, Integer productId);

	Set<UserTraining> findAllByUserAccountId(Integer accountId);

	@Transactional
	@Modifying
	@Query(value = "delete from users_trainings where user_training_id = ?1", nativeQuery = true)
	void deleteByUserTrainingId(Integer userTrainingId);
}