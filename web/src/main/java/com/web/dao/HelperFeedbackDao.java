package com.web.dao;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.HelperFeedback;

@Repository("helperFeedbackDao")
public interface HelperFeedbackDao extends CrudRepository<HelperFeedback, Integer> {

	@Override
	Set<HelperFeedback> findAll();

	Optional<HelperFeedback> findByLearnerAccountId(Integer accountId);

	Set<HelperFeedback> findAllByLearnerAccountId(Integer accountId);

	Optional<HelperFeedback> findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(Integer helperId,
			Timestamp startDate, Timestamp endDate);
}