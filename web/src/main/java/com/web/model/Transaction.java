package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Integer transactionId;

	@Column(name = "payments")
	private Integer payments;

	@Column(name = "available_balance")
	private Integer availableBalance;

	@OneToOne(optional = false)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	public Transaction() {
	}

	public Transaction(Integer transactionId, Integer payments, Integer availableBalance, Account account) {
		this.transactionId = transactionId;
		this.payments = payments;
		this.availableBalance = availableBalance;
		this.account = account;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getPayments() {
		return payments;
	}

	public void setPayments(Integer payments) {
		this.payments = payments;
	}

	public Integer getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Integer availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", payments=" + payments + ", availableBalance="
				+ availableBalance + ", account=" + account + "]";
	}
}
