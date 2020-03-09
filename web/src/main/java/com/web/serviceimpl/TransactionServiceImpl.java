package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.TransactionDao;
import com.web.model.Transaction;
import com.web.service.TransactionService;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionDao transactionDao;

	@Override
	public void save(Transaction transaction) {
		transactionDao.save(transaction);
	}

	@Override
	public Optional<Transaction> findById(Integer id) {
		return transactionDao.findById(id);
	}

	@Override
	public void delete(Transaction transaction) {
		transactionDao.delete(transaction);
	}

	@Override
	public void deleteById(Integer id) {
		transactionDao.deleteById(id);

	}

	@Override
	public Set<Transaction> findAll() {
		return transactionDao.findAll();
	}

}
