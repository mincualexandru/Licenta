package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Experience;


@Repository("experienceDao")
public interface ExperienceDao extends CrudRepository<Experience, Integer> {
	@Override
    Set<Experience> findAll();
}
