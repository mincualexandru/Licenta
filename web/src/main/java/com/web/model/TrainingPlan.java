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
@Table(name = "trainings_plans")
public class TrainingPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_plan_id")
	private int trainingPlanId;

	@ManyToOne
	@JoinColumn(name = "trainer_id")
	private Account trainer;

	@Column(name = "name")
	@NotEmpty(message = "Campul este obligatoriu !")
	private String name;

	@Column(name = "intensity")
	@NotEmpty(message = "Campul este obligatoriu !")
	private String intensity;

	@Column(name = "for_who")
	@Enumerated(EnumType.STRING)
	private Gender forWho;

	@Column(name = "price")
	@Range(min = 0, message = "Valorile negative nu sunt permise")
	@NotNull(message = "Campul este obligatoriu !")
	private Integer price;

	@Column(name = "creation_date")
	@CreationTimestamp
	private Timestamp dateOfCreation;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trainingPlan", orphanRemoval = true)
	private Set<UserTraining> userTrainingPlans = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trainingPlan", orphanRemoval = true)
	private Set<Exercise> exercises = new HashSet<>();

	public TrainingPlan() {

	}

	public TrainingPlan(int trainingPlanId, Account trainer, String name, String intensity, Gender forWho,
			Integer price, Timestamp dateOfCreation, Set<UserTraining> userTrainingPlans) {
		this.trainingPlanId = trainingPlanId;
		this.trainer = trainer;
		this.name = name;
		this.intensity = intensity;
		this.forWho = forWho;
		this.price = price;
		this.dateOfCreation = dateOfCreation;
		this.userTrainingPlans = userTrainingPlans;
	}

	public int getTrainingPlanId() {
		return trainingPlanId;
	}

	public void setTrainingPlanId(int trainingPlanId) {
		this.trainingPlanId = trainingPlanId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntensity() {
		return intensity;
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}

	public Gender getForWho() {
		return forWho;
	}

	public void setForWho(Gender forWho) {
		this.forWho = forWho;
	}

	public Account getTrainer() {
		return trainer;
	}

	public void setTrainer(Account trainer) {
		this.trainer = trainer;
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

	public Set<UserTraining> getUserTrainingPlans() {
		return userTrainingPlans;
	}

	public void setUserTrainingPlans(Set<UserTraining> userTrainingPlans) {
		this.userTrainingPlans = userTrainingPlans;
	}

	public Set<Exercise> getExercises() {
		return exercises;
	}

	public void setExercises(Set<Exercise> exercises) {
		this.exercises = exercises;
	}

	@Override
	public String toString() {
		return "TrainingPlan [trainingPlanId=" + trainingPlanId + ", trainer=" + trainer + ", name=" + name
				+ ", intensity=" + intensity + ", forWho=" + forWho + ", price=" + price + ", dateOfCreation="
				+ dateOfCreation + ", userTrainingPlans=" + userTrainingPlans + ", exercises=" + exercises + "]";
	}
}
