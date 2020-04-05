package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.ExerciseDao;
import com.web.model.Exercise;
import com.web.service.ExerciseService;

@Service("exerciseService")
public class ExerciseServiceImpl implements ExerciseService {

	@Autowired
	private ExerciseDao exerciseDao;

	@Override
	public void save(Exercise exercise) {
		exerciseDao.save(exercise);

	}

	@Override
	public Optional<Exercise> findById(Integer id) {
		return exerciseDao.findById(id);
	}

	@Override
	public void delete(Exercise exercise) {
		exerciseDao.delete(exercise);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseDao.deleteById(id);
	}

	@Override
	public Set<Exercise> findAll() {
		return exerciseDao.findAll();
	}

	@Override
	public Set<Exercise> findAllNotPerfomerdExercisesForTrainingPlanId(Integer trainingPlanId) {
		return exerciseDao.findAllNotPerfomerdExercisesForTrainingPlanId(trainingPlanId);
	}

	@Override
	public Set<Exercise> findAllNotPerfomerdExercises() {
		return exerciseDao.findAllNotPerfomerdExercises();
	}

	@Override
	public Set<Exercise> findAllByTrainingPlanHelperPlanId(Integer helperPlanId) {
		return exerciseDao.findAllByTrainingPlanHelperPlanId(helperPlanId);
	}

	@Override
	public Set<Exercise> findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(Integer trainingPlanId,
			String string) {
		return exerciseDao.findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(trainingPlanId, string);
	}

	@Override
	public Optional<Exercise> findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId) {
		return exerciseDao.findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId);
	}
}
