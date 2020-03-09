package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.ExerciseAdviceDao;
import com.web.model.ExerciseAdvice;
import com.web.service.ExerciseAdviceService;

@Service("exerciseAdviceService")
public class ExerciseAdviceServiceImpl implements ExerciseAdviceService {

	@Autowired
	private ExerciseAdviceDao exerciseAdviceDao;

	@Override
	public void save(ExerciseAdvice exerciseAdvice) {
		exerciseAdviceDao.save(exerciseAdvice);
	}

	@Override
	public Optional<ExerciseAdvice> findById(Integer id) {
		return exerciseAdviceDao.findById(id);
	}

	@Override
	public void delete(ExerciseAdvice exerciseAdvice) {
		exerciseAdviceDao.delete(exerciseAdvice);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseAdviceDao.deleteById(id);
	}

	@Override
	public Set<ExerciseAdvice> findAll() {
		return exerciseAdviceDao.findAll();
	}

	@Override
	public ExerciseAdvice findByExerciseAdviceIdAndExerciseExerciseId(Integer adviceId, Integer exerciseId) {
		return exerciseAdviceDao.findByExerciseAdviceIdAndExerciseExerciseId(adviceId, exerciseId);
	}

}
