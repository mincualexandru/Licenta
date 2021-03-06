package com.web.model;

import java.sql.Timestamp;
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

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.web.utils.Gender;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "name")
	@Pattern(regexp = "(?=.*[a-z])(?=.*(\\d)).{5,}", message = "**Campul trebuie sa contina minim 5 caractere, o litera si o cifra")
	private String username;

	@Column(name = "first_name")
	@Pattern(regexp = "[A-Z][a-zA-Z]+", message = "**Campul trebuie sa aibe prima litera mare, cifrele fiind interzise")
	@Size(min = 3, message = "**Numele de familie trebuie sa aiba minim 3 caractere")
	private String firstName;

	@Column(name = "last_name")
	@Pattern(regexp = "[A-Z][a-zA-Z]+", message = "**Campul trebuie sa aibe prima litera mare, cifrele fiind interzise")
	@Size(min = 3, message = "**Prenumele trebuie sa aiba minim 3 caractere")
	private String lastName;

	@Column(name = "password")
	@Size(min = 3, message = "**Parola trebuie sa aiba minim 3 caractere")
	private String password;

	@Column(name = "email")
	@Pattern(regexp = "\\S+@\\S+\\.\\S+", message = "**Adresa de email formata gresit")
	private String email;

	@Column(name = "phone_number")
	@Pattern(regexp = "^[0-9]{10}$", message = "**Numarul trebuie sa aiba minim 10 cifre")
	private String phoneNumber;

	@Column(name = "born_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Past(message = "**Nu poti introduce o data din viitor")
	private LocalDate bornDate;

	@Column(name = "active")
	private boolean active;

	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "date_of_creation")
	@CreationTimestamp
	private Timestamp dateOfCreation;

	@OneToOne(mappedBy = "account")
	@JsonIgnore
	private AccountInformation accountInformation;

	@OneToOne(mappedBy = "account")
	@JsonIgnore
	private Transaction transaction;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<UserDevice> userDevices = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<UserPlan> userPlans = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "helper", orphanRemoval = true)
	@JsonIgnore
	private Set<HelperPlan> plans = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<ExerciseDone> exerciseDone = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<ExerciseFeedback> exercisesFeedback = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<FoodEaten> foodEaten = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
	@JsonIgnore
	private Set<FoodFeedback> foodsFeedback = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "helper", orphanRemoval = true)
	@JsonIgnore
	private Set<HelperFeedback> helperFeedback = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "learner", orphanRemoval = true)
	@JsonIgnore
	private Set<HelperFeedback> userFeedback = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_helpers", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "helper_id") })
	@JsonIgnore
	private Set<Account> helpers = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "accounts_roles", joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	@JsonIgnore
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

	public Timestamp getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Timestamp dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
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

	public Set<HelperPlan> getPlans() {
		return plans;
	}

	public void setPlans(Set<HelperPlan> plans) {
		this.plans = plans;
	}

	public Set<Account> getHelpers() {
		return helpers;
	}

	public void setHelpers(Set<Account> helpers) {
		this.helpers = helpers;
	}

	public Set<UserPlan> getUserPlans() {
		return userPlans;
	}

	public void setUserPlans(Set<UserPlan> userPlans) {
		this.userPlans = userPlans;
	}

	public Set<ExerciseDone> getExerciseDone() {
		return exerciseDone;
	}

	public void setExerciseDone(Set<ExerciseDone> exerciseDone) {
		this.exerciseDone = exerciseDone;
	}

	public Set<ExerciseFeedback> getExercisesFeedback() {
		return exercisesFeedback;
	}

	public void setExercisesFeedback(Set<ExerciseFeedback> exercisesFeedback) {
		this.exercisesFeedback = exercisesFeedback;
	}

	public Set<HelperFeedback> getHelperFeedback() {
		return helperFeedback;
	}

	public void setHelperFeedback(Set<HelperFeedback> helperFeedback) {
		this.helperFeedback = helperFeedback;
	}

	public Set<HelperFeedback> getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(Set<HelperFeedback> userFeedback) {
		this.userFeedback = userFeedback;
	}

	public Set<FoodEaten> getFoodEaten() {
		return foodEaten;
	}

	public void setFoodEaten(Set<FoodEaten> foodEaten) {
		this.foodEaten = foodEaten;
	}

	public Set<FoodFeedback> getFoodsFeedback() {
		return foodsFeedback;
	}

	public void setFoodsFeedback(Set<FoodFeedback> foodsFeedback) {
		this.foodsFeedback = foodsFeedback;
	}

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", username=" + username + ", firstName=" + firstName + ", lastName="
				+ lastName + ", password=" + password + ", email=" + email + ", phoneNumber=" + phoneNumber
				+ ", bornDate=" + bornDate + ", active=" + active + ", gender=" + gender + ", roles=" + roles
				+ ", accountInformation=" + accountInformation + ", userDevices=" + userDevices + "]";
	}

}
