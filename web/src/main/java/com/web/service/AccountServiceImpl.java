package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.dao.RoleDao;
import com.web.dao.AccountDao;
import com.web.model.Account;

@Service("userService")
public class AccountServiceImpl implements AccountService{
	
	@Autowired
	private AccountDao userDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(Account user) {
		userDao.save(user);
	}

	@Override
	public Optional<Account> findById(Integer id) {
		return userDao.findById(id);
	}

	@Override
	public void delete(Account user) {
		userDao.delete(user);
	}

	@Override
	public void deleteById(Integer id) {
		userDao.deleteById(id);
	}

	@Override
	public Set<Account> findAll() {
		return userDao.findAll();
	}

	@Override
	public void saveUser(Account user, String chooseRoleName) {
		user.getRoles().add(roleDao.findByName(chooseRoleName));
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userDao.save(user);
	}

	@Override
	public Account findByUsername(String name) {
		return userDao.findByUsername(name);
	}

	@Override
	public Set<Account> findAllByActive(boolean b) {
		return userDao.findAllByActive(b);
	}

}