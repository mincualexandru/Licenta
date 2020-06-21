package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.AccountInformation;

public interface AccountInformationService {

	void save(AccountInformation accountInformation);

	Optional<AccountInformation> findById(Integer id);

	void delete(AccountInformation accountInformation);

	void deleteById(Integer id);

	Set<AccountInformation> findAll();

	AccountInformation findByAccountAccountId(Integer accountId);

	void createAccountInformation(AccountInformation curriculumVitae, Account currentAccount);
}
