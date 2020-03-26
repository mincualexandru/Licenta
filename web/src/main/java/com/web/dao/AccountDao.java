package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Account;

@Repository("accountDao")
public interface AccountDao extends CrudRepository<Account, Integer> {

	@Override
	Set<Account> findAll();

	Set<Account> findAllByActive(boolean b);

	Account findByUsername(String name);

	Set<Account> findAllByRolesNameOrRolesName(String trainer, String nutritionist);

	@Query(value = "select user_id from users_helpers where helper_id = ?1", nativeQuery = true)
	Set<Integer> findAllLearnersByHelperId(Integer accountId);

	Account findByUsernameAndPassword(String userName, String encode);
}
