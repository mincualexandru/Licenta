package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.HelperFeedback;

public interface HelperFeedbackService {

	void save(HelperFeedback helperFeedback);

	Optional<HelperFeedback> findById(Integer id);

	void delete(HelperFeedback helperFeedback);

	void deleteById(Integer id);

	Set<HelperFeedback> findAll();

	Optional<HelperFeedback> findByLearnerAccountId(Integer accountId);

	Set<HelperFeedback> findAllByLearnerAccountId(Integer accountId);
}
