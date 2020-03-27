package com.web.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import com.web.utils.Gender;

@Entity
@Table(name = "helpers_plans")
public class HelperPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "helper_plan_id")
	private int helperPlanId;

	@ManyToOne
	@JoinColumn(name = "helper_id")
	private Account helper;

	@Column(name = "name")
	@NotEmpty(message = "Campul este obligatoriu !")
	private String name;

	@Column(name = "for_who")
	@Enumerated(EnumType.STRING)
	private Gender forWho;

	@Column(name = "type_of_plan")
	private String typeOfPlan;

	@Column(name = "price")
	@Range(min = 0, message = "Valorile negative nu sunt permise")
	@NotNull(message = "Campul este obligatoriu !")
	private Integer price;

	@Column(name = "creation_date")
	@CreationTimestamp
	private Timestamp dateOfCreation;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "helperPlan", orphanRemoval = true)
	private Set<UserPlan> userPlans = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trainingPlan", orphanRemoval = true)
	private Set<Exercise> exercises = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "dietPlan", orphanRemoval = true)
	private Set<Food> foods = new HashSet<>();

	public HelperPlan() {
	}

	public HelperPlan(int helperPlanId, Account helper, @NotEmpty(message = "Campul este obligatoriu !") String name,
			Gender forWho, String typeOfPlan,
			@Range(min = 0, message = "Valorile negative nu sunt permise") @NotNull(message = "Campul este obligatoriu !") Integer price,
			Timestamp dateOfCreation, Set<UserPlan> userPlans, Set<Exercise> exercises, Set<Food> foods) {
		super();
		this.helperPlanId = helperPlanId;
		this.helper = helper;
		this.name = name;
		this.forWho = forWho;
		this.typeOfPlan = typeOfPlan;
		this.price = price;
		this.dateOfCreation = dateOfCreation;
		this.userPlans = userPlans;
		this.exercises = exercises;
		this.foods = foods;
	}

	public int getHelperPlanId() {
		return helperPlanId;
	}

	public void setHelperPlanId(int helperPlanId) {
		this.helperPlanId = helperPlanId;
	}

	public Account getHelper() {
		return helper;
	}

	public void setHelper(Account helper) {
		this.helper = helper;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gender getForWho() {
		return forWho;
	}

	public void setForWho(Gender forWho) {
		this.forWho = forWho;
	}

	public String getTypeOfPlan() {
		return typeOfPlan;
	}

	public void setTypeOfPlan(String typeOfPlan) {
		this.typeOfPlan = typeOfPlan;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Timestamp getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Timestamp dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	public Set<UserPlan> getUserPlans() {
		return userPlans;
	}

	public void setUserPlans(Set<UserPlan> userPlans) {
		this.userPlans = userPlans;
	}

	public Set<Exercise> getExercises() {
		return exercises;
	}

	public void setExercises(Set<Exercise> exercises) {
		this.exercises = exercises;
	}

	public Set<Food> getFoods() {
		return foods;
	}

	public void setFoods(Set<Food> foods) {
		this.foods = foods;
	}
}
