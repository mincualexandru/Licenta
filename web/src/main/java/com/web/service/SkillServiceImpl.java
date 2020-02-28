package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.SkillDao;
import com.web.model.Skill;

@Service("skillService")
public class SkillServiceImpl implements SkillService {
	
	@Autowired
	private SkillDao skillDao;
	
	@Override
	public void save(Skill skill) {
		skillDao.save(skill);
	}

	@Override
	public Optional<Skill> findById(Integer id) {
		return skillDao.findById(id);
	}

	@Override
	public void delete(Skill skill) {
		skillDao.delete(skill);
	}

	@Override
	public void deleteById(Integer id) {
		skillDao.deleteById(id);
	}

	@Override
	public Set<Skill> findAll() {
		return skillDao.findAll();
	}

}
