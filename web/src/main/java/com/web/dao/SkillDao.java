package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Skill;

@Repository("skillDao")
public interface SkillDao extends CrudRepository<Skill, Integer>{

	@Override
    Set<Skill> findAll();

	
}
