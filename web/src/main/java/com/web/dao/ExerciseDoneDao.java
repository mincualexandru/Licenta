package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.ExerciseDone;

@Repository("exerciseDoneDao")
public interface ExerciseDoneDao extends CrudRepository<ExerciseDone, Integer> {

	@Override
	Set<ExerciseDone> findAll();

	Set<ExerciseDone> findAllByUserAccountId(Integer accountId);

}