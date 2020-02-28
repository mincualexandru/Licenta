package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Account;


@Repository("userDao")
public interface AccountDao extends CrudRepository<Account, Integer>{
	
	@Override
    Set<Account> findAll();

	Set<Account> findAllByActive(boolean b);

	Account findByUsername(String name);
	
}
