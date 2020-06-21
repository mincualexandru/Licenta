package com.web.serviceimpl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.AccountDao;
import com.web.dao.ExerciseDao;
import com.web.dao.ExerciseDoneDao;
import com.web.model.ExerciseDone;
import com.web.service.ExerciseDoneService;

@Service("exerciseDoneService")
public class ExerciseDoneServiceImpl implements ExerciseDoneService {

	@Autowired
	private ExerciseDoneDao exerciseDoneDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private ExerciseDao exerciseDao;

	@Override
	public void save(ExerciseDone exerciseImage) {
		exerciseDoneDao.save(exerciseImage);
	}

	@Override
	public Optional<ExerciseDone> findById(Integer id) {
		return exerciseDoneDao.findById(id);
	}

	@Override
	public void delete(ExerciseDone exerciseImage) {
		exerciseDoneDao.delete(exerciseImage);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseDoneDao.deleteById(id);
	}

	@Override
	public Set<ExerciseDone> findAll() {
		return exerciseDoneDao.findAll();
	}

	@Override
	public Set<ExerciseDone> findAllByUserAccountId(Integer accountId) {
		return exerciseDoneDao.findAllByUserAccountId(accountId);
	}

	@Override
	public Optional<ExerciseDone> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId) {
		return exerciseDoneDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId);
	}

	@Override
	public Set<ExerciseDone> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return exerciseDoneDao.findAllByUserAccountIdAndDateOfExecutionBetween(accountId, timestampStartDate,
				timestampEndDate);
	}

	@Override
	public boolean checkIfTenDaysExerciseHavePassed(boolean passedTenDaysExerciseDone, LocalDateTime dateTimeNow,
			Integer accountId) {
		if (exerciseDoneDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			ExerciseDone exerciseDone = exerciseDoneDao.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId)
					.get();
			Duration duration = Duration.between(exerciseDone.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				passedTenDaysExerciseDone = true;
			}
		}
		return passedTenDaysExerciseDone;
	}

	@Override
	public void addExerciseDone(Integer accountId, Integer exerciseId) {
		ExerciseDone exerciseDone = new ExerciseDone();
		exerciseDone.setExercise(exerciseDao.findById(exerciseId).get());
		exerciseDone.setUser(accountDao.findById(accountId).get());
		exerciseDoneDao.save(exerciseDone);
	}

}
