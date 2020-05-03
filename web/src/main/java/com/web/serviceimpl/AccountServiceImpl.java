package com.web.serviceimpl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.dao.AccountDao;
import com.web.dao.RoleDao;
import com.web.model.Account;
import com.web.service.AccountService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(Account user) {
		accountDao.save(user);
	}

	@Override
	public Optional<Account> findById(Integer id) {
		return accountDao.findById(id);
	}

	@Override
	public void delete(Account user) {
		accountDao.delete(user);
	}

	@Override
	public void deleteById(Integer id) {
		accountDao.deleteById(id);
	}

	@Override
	public Set<Account> findAll() {
		return accountDao.findAll();
	}

	@Override
	public void saveUser(Account user, String chooseRoleName) {
		user.getRoles().add(roleDao.findByName(chooseRoleName).get());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setDateOfCreation(new Timestamp(System.currentTimeMillis()));
		accountDao.save(user);
	}

	@Override
	public Account findByUsername(String name) {
		return accountDao.findByUsername(name);
	}

	@Override
	public Set<Account> findAllByActive(boolean b) {
		return accountDao.findAllByActive(b);
	}

	@Override
	public Set<Account> findAllByRolesNameOrRolesName(String trainer, String nutritionist) {
		return accountDao.findAllByRolesNameOrRolesName(trainer, nutritionist);
	}

	@Override
	public Set<Integer> findAllLearnersByHelperId(Integer accountId) {
		return accountDao.findAllLearnersByHelperId(accountId);
	}

	@Override
	public Account getAccountConnected() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountDao.findByUsername(auth.getName());
		return user;
	}

	@Override
	public Set<Account> getHelpers(Account helper) {
		Set<Integer> learnersIds = accountDao.findAllLearnersByHelperId(helper.getAccountId());
		Set<Account> learners = new HashSet<>();
		for (Integer integer : learnersIds) {
			Account learner = accountDao.findById(integer).get();
			learners.add(learner);
		}
		return learners;
	}

}