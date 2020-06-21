package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Account;
import com.web.model.Transaction;

public interface TransactionService {
	void save(Transaction transaction);

	Optional<Transaction> findById(Integer id);

	void delete(Transaction transaction);

	void deleteById(Integer id);

	Set<Transaction> findAll();

	void createTransaction(Account user);
}
