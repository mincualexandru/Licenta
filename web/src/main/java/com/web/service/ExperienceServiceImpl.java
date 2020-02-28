package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.ExperienceDao;
import com.web.model.Experience;

@Service("experienceService")
public class ExperienceServiceImpl implements ExperienceService {

	@Autowired
	private ExperienceDao experienceDao;

	@Override
	public void save(Experience experience) {
		experienceDao.save(experience);
		
	}

	@Override
	public Optional<Experience> findById(Integer id) {
		return experienceDao.findById(id);
	}

	@Override
	public void delete(Experience experience) {
		experienceDao.delete(experience);
	}

	@Override
	public void deleteById(Integer id) {
		experienceDao.deleteById(id);
	}

	@Override
	public Set<Experience> findAll() {
		return experienceDao.findAll();
	}
	
}
