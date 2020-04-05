package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.RoleDao;
import com.web.model.Role;
import com.web.service.RoleService;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public void save(Role user) {
		roleDao.save(user);
	}

	@Override
	public Optional<Role> findById(Integer id) {
		return roleDao.findById(id);
	}

	@Override
	public void delete(Role role) {
		roleDao.delete(role);
	}

	@Override
	public void deleteById(Integer id) {
		roleDao.deleteById(id);
	}

	@Override
	public Set<Role> findAll() {
		return roleDao.findAll();
	}

	@Override
	public Optional<Role> findByName(String string) {
		return roleDao.findByName(string);
	}

}
