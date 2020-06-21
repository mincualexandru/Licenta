package com.web.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.web.model.ExerciseDone;

public interface ExerciseDoneService {

	void save(ExerciseDone exerciseDone);

	Optional<ExerciseDone> findById(Integer id);

	void delete(ExerciseDone exerciseDone);

	void deleteById(Integer id);

	Set<ExerciseDone> findAll();

	Set<ExerciseDone> findAllByUserAccountId(Integer accountId);

	Optional<ExerciseDone> findTopByUserAccountIdOrderByDateOfExecutionDesc(Integer accountId);

	Set<ExerciseDone> findAllByUserAccountIdAndDateOfExecutionBetween(Integer accountId, Timestamp timestampStartDate,
			Timestamp timestampEndDate);

	boolean checkIfTenDaysExerciseHavePassed(boolean passedTenDaysExerciseDone, LocalDateTime dateTimeNow,
			Integer accountId);

	void addExerciseDone(Integer accountId, Integer exerciseId);
}
