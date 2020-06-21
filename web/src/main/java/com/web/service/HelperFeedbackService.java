package com.web.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.HelperFeedback;
import com.web.utils.Qualifying;

public interface HelperFeedbackService {

	void save(HelperFeedback helperFeedback);

	Optional<HelperFeedback> findById(Integer id);

	void delete(HelperFeedback helperFeedback);

	void deleteById(Integer id);

	Set<HelperFeedback> findAll();

	Optional<HelperFeedback> findByLearnerAccountId(Integer accountId);

	Set<HelperFeedback> findAllByLearnerAccountId(Integer accountId);

	Optional<HelperFeedback> findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(Integer helperId,
			Timestamp startDate, Timestamp endDate);

	void helperCreateFeedbackForLearner(Account helper, Integer learnerId, String reason, Qualifying qualifying);
}
