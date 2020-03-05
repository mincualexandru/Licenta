package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.TrainingPlanDao;
import com.web.model.TrainingPlan;

@Service("trainingPlanService")
public class TrainingPlanServiceImpl implements TrainingPlanService {

	@Autowired
	private TrainingPlanDao trainingPlanDao;

	@Override
	public void save(TrainingPlan trainingPlan) {
		trainingPlanDao.save(trainingPlan);
	}

	@Override
	public Optional<TrainingPlan> findById(Integer id) {
		return trainingPlanDao.findById(id);
	}

	@Override
	public void delete(TrainingPlan trainingPlan) {
		trainingPlanDao.delete(trainingPlan);
	}

	@Override
	public void deleteById(Integer id) {
		trainingPlanDao.deleteById(id);
	}

	@Override
	public Set<TrainingPlan> findAll() {
		return trainingPlanDao.findAll();
	}

	@Override
	public Set<TrainingPlan> findAllByTrainingPlanNotAssociated(Integer trainerId) {
		return trainingPlanDao.findAllByTrainingPlanNotAssociated(trainerId);
	}

}
