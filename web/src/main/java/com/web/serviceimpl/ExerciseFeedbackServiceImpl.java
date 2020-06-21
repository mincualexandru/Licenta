package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.ExerciseFeedbackDao;
import com.web.model.Account;
import com.web.model.Exercise;
import com.web.model.ExerciseFeedback;
import com.web.service.ExerciseFeedbackService;

@Service("exerciseFeedbackService")
public class ExerciseFeedbackServiceImpl implements ExerciseFeedbackService {

	@Autowired
	private ExerciseFeedbackDao exerciseFeedbackDao;

	@Override
	public void save(ExerciseFeedback exerciseFeedback) {
		exerciseFeedbackDao.save(exerciseFeedback);
	}

	@Override
	public Optional<ExerciseFeedback> findById(Integer id) {
		return exerciseFeedbackDao.findById(id);
	}

	@Override
	public void delete(ExerciseFeedback exerciseFeedback) {
		exerciseFeedbackDao.delete(exerciseFeedback);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseFeedbackDao.deleteById(id);
	}

	@Override
	public Set<ExerciseFeedback> findAll() {
		return exerciseFeedbackDao.findAll();
	}

	@Override
	public Set<ExerciseFeedback> findAllByUserAccountId(Integer integer) {
		return exerciseFeedbackDao.findAllByUserAccountId(integer);
	}

	@Override
	public void createFeedbackForExercise(String messageReview, Integer number, Account user, Exercise exercise) {
		ExerciseFeedback exerciseFeedback = new ExerciseFeedback();
		exerciseFeedback.setExercise(exercise);
		exerciseFeedback.setUser(user);
		exerciseFeedback.setMessage(messageReview);
		exerciseFeedback.setRating(number);
		exerciseFeedbackDao.save(exerciseFeedback);
	}
}
