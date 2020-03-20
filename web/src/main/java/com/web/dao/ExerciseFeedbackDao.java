package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.ExerciseFeedback;

@Repository("exerciseFeedbackDao")
public interface ExerciseFeedbackDao extends CrudRepository<ExerciseFeedback, Integer> {

	@Override
	Set<ExerciseFeedback> findAll();

	Set<ExerciseFeedback> findAllByUserAccountId(Integer integer);
}