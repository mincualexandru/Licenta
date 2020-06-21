package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Exercise;
import com.web.model.ExerciseFeedback;

public interface ExerciseFeedbackService {

	void save(ExerciseFeedback exerciseFeedback);

	Optional<ExerciseFeedback> findById(Integer id);

	void delete(ExerciseFeedback exerciseFeedback);

	void deleteById(Integer id);

	Set<ExerciseFeedback> findAll();

	Set<ExerciseFeedback> findAllByUserAccountId(Integer integer);

	void createFeedbackForExercise(String messageReview, Integer number, Account user, Exercise exercise);
}
