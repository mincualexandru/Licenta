package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.AccountDao;
import com.web.dao.AccountInformationDao;
import com.web.model.Account;
import com.web.model.AccountInformation;
import com.web.service.AccountInformationService;

@Service("accountInformationService")
public class AccountInformationServiceImpl implements AccountInformationService {

	@Autowired
	private AccountInformationDao accountInformationDao;

	@Autowired
	private AccountDao accountDao;

	@Override
	public void save(AccountInformation accountInformation) {
		accountInformationDao.save(accountInformation);
	}

	@Override
	public Optional<AccountInformation> findById(Integer id) {
		return accountInformationDao.findById(id);
	}

	@Override
	public void delete(AccountInformation accountInformation) {
		accountInformationDao.delete(accountInformation);

	}

	@Override
	public void deleteById(Integer id) {
		accountInformationDao.deleteById(id);
	}

	@Override
	public Set<AccountInformation> findAll() {
		return accountInformationDao.findAll();
	}

	@Override
	public AccountInformation findByAccountAccountId(Integer accountId) {
		return accountInformationDao.findByAccountAccountId(accountId);
	}

	@Override
	public void createAccountInformation(AccountInformation curriculumVitae, Account currentAccount) {
		currentAccount.setAccountInformation(curriculumVitae);
		curriculumVitae.setAccount(currentAccount);
		accountInformationDao.save(curriculumVitae);
		accountDao.save(currentAccount);
	}
}
