package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Education;

@Repository("educationDao")
public interface EducationDao extends CrudRepository<Education, Integer>{

	@Override
	Set<Education> findAll();
}
