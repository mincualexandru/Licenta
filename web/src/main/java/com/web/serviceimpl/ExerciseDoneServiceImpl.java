package com.web.serviceimpl;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.ExerciseDoneDao;
import com.web.model.ExerciseDone;
import com.web.service.ExerciseDoneService;

@Service("exerciseDoneService")
public class ExerciseDoneServiceImpl implements ExerciseDoneService {

	@Autowired
	private ExerciseDoneDao exerciseServiceDao;

	@Override
	public void save(ExerciseDone exerciseImage) {
		exerciseServiceDao.save(exerciseImage);
	}

	@Override
	public Optional<ExerciseDone> findById(Integer id) {
		return exerciseServiceDao.findById(id);
	}

	@Override
	public void delete(ExerciseDone exerciseImage) {
		exerciseServiceDao.delete(exerciseImage);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseServiceDao.deleteById(id);
	}

	@Override
	public Set<ExerciseDone> findAll() {
		return exerciseServiceDao.findAll();
	}

	@Override
	public Set<ExerciseDone> findAllByUserAccountId(Integer accountId) {
		return exerciseServiceDao.findAllByUserAccountId(accountId);
	}

	@Override
	public Optional<ExerciseDone> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId) {
		return exerciseServiceDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId);
	}

	@Override
	public Set<ExerciseDone> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return exerciseServiceDao.findAllByUserAccountIdAndDateOfExecutionBetween(accountId, timestampStartDate,
				timestampEndDate);
	}

}
