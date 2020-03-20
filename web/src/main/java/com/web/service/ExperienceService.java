package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Experience;

public interface ExperienceService {
	void save(Experience experience);
	
	Optional<Experience> findById(Integer id);
	
    void delete(Experience experience);
    
    void deleteById(Integer id);
    
    Set<Experience> findAll();
}
