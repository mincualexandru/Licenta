package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Education;

public interface EducationService {
	
	void save(Education education);
	
	Optional<Education> findById(Integer id);
	
    void delete(Education education);
    
    void deleteById(Integer id);
    
    Set<Education> findAll();
    
}
