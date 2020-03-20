package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Transaction;

@Repository("transactionDao")
public interface TransactionDao extends CrudRepository<Transaction, Integer> {
	@Override
	Set<Transaction> findAll();
}
