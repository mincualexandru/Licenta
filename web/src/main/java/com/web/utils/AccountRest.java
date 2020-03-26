package com.web.utils;

import java.time.LocalDate;

public class AccountRest {
	private Integer accountId;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String phoneNumber;
	private LocalDate bornDate;
	private boolean active;
	private String gender;
	private String role;

	public AccountRest(Integer accountId, String username, String firstName, String lastName, String password,
			String email, String phoneNumber, LocalDate bornDate, boolean active, String gender, String role) {
		super();
		this.accountId = accountId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.bornDate = bornDate;
		this.active = active;
		this.gender = gender;
		this.role = role;
	}

	public AccountRest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalDate getBornDate() {
		return bornDate;
	}

	public void setBornDate(LocalDate bornDate) {
		this.bornDate = bornDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", username=" + username + ", firstName=" + firstName + ", lastName="
				+ lastName + ", password=" + password + ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", bornDate=" + bornDate + ", active=" + active + ", gender=" + gender + ", role=" + role + "]";
	}
}
