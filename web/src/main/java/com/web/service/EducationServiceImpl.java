package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.EducationDao;
import com.web.model.Education;

@Service("educationService")
public class EducationServiceImpl implements EducationService {
	
	@Autowired
	private EducationDao educationDao;

	@Override
	public void save(Education education) {
		educationDao.save(education);
	}

	@Override
	public Optional<Education> findById(Integer id) {
		return educationDao.findById(id);
	}

	@Override
	public void delete(Education education) {
		educationDao.delete(education);
	}

	@Override
	public void deleteById(Integer id) {
		educationDao.deleteById(id);
	}

	@Override
	public Set<Education> findAll() {
		return educationDao.findAll();
	}

}
