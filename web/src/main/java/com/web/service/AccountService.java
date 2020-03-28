package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Account;

public interface AccountService {

	void save(Account user);

	Optional<Account> findById(Integer id);

	void delete(Account user);

	void deleteById(Integer id);

	Set<Account> findAll();

	void saveUser(Account user, String chooseRoleName);

	Set<Account> findAllByActive(boolean b);

	Account findByUsername(String name);

	Set<Account> findAllByRolesNameOrRolesName(String trainer, String nutritionist);

	Set<Integer> findAllLearnersByHelperId(Integer accountId);

	Account getAccountConnected();
}
