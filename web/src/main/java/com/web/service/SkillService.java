package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Skill;

public interface SkillService {
	
	void save(Skill skill);
	
	Optional<Skill> findById(Integer id);
	
    void delete(Skill skill);
    
    void deleteById(Integer id);
    
    Set<Skill> findAll();
    
}
