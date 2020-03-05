package com.web.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.web.utils.Gender;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "name")
	@Pattern(regexp = "^[a-zA-Z](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z]){3,18}[a-zA-Z]$", message = "Nume utilizator introdus gresit")
	// poate contine doar litere/_/punct
	// _. nu se poate
	// __ / .. nu se poate
	//
	private String username;

	@Column(name = "first_name")
	@Pattern(regexp = "^[A-Z][a-zA-Z]{3,15}$", message = "Numele de familie introdus gresit")
	private String firstName;

	@Column(name = "last_name")
	@Pattern(regexp = "^[A-Z][a-zA-Z]{3,15}(?: [A-Z][a-zA-Z]*){0,2}$", message = "Prenumele introdus gresit")
	private String lastName;

	@Column(name = "password")
	@Size(min = 3, message = "Valoarea minima a dimensiunii campului este de 3 caractere")
	private String password;

	@Column(name = "email")
	@Pattern(regexp = "\\S+@\\S+\\.\\S+", message = "Adresa de email formata gresit")
	private String email;

	@Column(name = "phone_number")
	@Pattern(regexp = "^[0-9]{10}$", message = "Valoarea dimensiunii campului este de 10 cifre")
	private String phoneNumber;

	@Column(name = "born_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	// @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",
	// message="Data introdusa nu este corecta")
	@Past(message = "Nu poti introduce o data din viitor")
	private LocalDate bornDate;

	@Column(name = "active")
	private boolean active;

	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@OneToOne(mappedBy = "account")
	private AccountInformation accountInformation;

	@OneToOne(mappedBy = "account")
	private Transaction transaction;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	private Set<UserDevice> userDevices = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	private Set<UserTraining> userTrainingPlans = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trainer", orphanRemoval = true)
	private Set<TrainingPlan> trainingPlans = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_helpers", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "helper_id") })
	private Set<Account> helpers = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "accounts_roles", joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<>();

	public Account() {
	}

	public Account(Integer accountId,
			@Pattern(regexp = "^[a-zA-Z](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z]){3,18}[a-zA-Z]$", message = "Nume utilizator introdus gresit") String username,
			@Pattern(regexp = "^[A-Z][a-zA-Z]{3,15}$", message = "Numele de familie introdus gresit") String firstName,
			@Pattern(regexp = "^[A-Z][a-zA-Z]{3,15}(?: [A-Z][a-zA-Z]*){0,2}$", message = "Prenumele introdus gresit") String lastName,
			@Size(min = 3, message = "Valoarea minima a dimensiunii campului este de 3 caractere") String password,
			@Pattern(regexp = "\\S+@\\S+\\.\\S+", message = "Adresa de email formata gresit") String email,
			@Pattern(regexp = "^[0-9]{10}$", message = "Valoarea dimensiunii campului este de 10 cifre") String phoneNumber,
			@Past(message = "Nu poti introduce o data din viitor") LocalDate bornDate, boolean active, Gender gender,
			Set<Role> roles, AccountInformation accountInformation, Set<UserDevice> userDevices) {
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
		this.roles = roles;
		this.accountInformation = accountInformation;
		this.userDevices = userDevices;
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

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public AccountInformation getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
	}

	public Set<UserDevice> getUserDevices() {
		return userDevices;
	}

	public void setUserDevices(Set<UserDevice> userDevices) {
		this.userDevices = userDevices;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Set<TrainingPlan> getTrainingPlans() {
		return trainingPlans;
	}

	public void setTrainingPlans(Set<TrainingPlan> trainingPlans) {
		this.trainingPlans = trainingPlans;
	}

	public Set<Account> getHelpers() {
		return helpers;
	}

	public void setHelpers(Set<Account> helpers) {
		this.helpers = helpers;
	}

	public Set<UserTraining> getUserTrainingPlans() {
		return userTrainingPlans;
	}

	public void setUserTrainingPlans(Set<UserTraining> userTrainingPlans) {
		this.userTrainingPlans = userTrainingPlans;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", username=" + username + ", firstName=" + firstName + ", lastName="
				+ lastName + ", password=" + password + ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", bornDate=" + bornDate + ", active=" + active + ", gender=" + gender + ", roles=" + roles
				+ ", accountInformation=" + accountInformation + ", userDevices=" + userDevices + "]";
	}

}
