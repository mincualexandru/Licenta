package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.AccountInformation;

@Repository("accountInformationDao")
public interface AccountInformationDao extends CrudRepository<AccountInformation, Integer>{
	@Override
    Set<AccountInformation> findAll();
	
	AccountInformation findByAccountAccountId(Integer accountId);
}
