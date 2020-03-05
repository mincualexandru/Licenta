package com.web.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	private String name;

	@Column(name = "intensity")
	private String intensity;

	@Column(name = "day")
	private String day;

	@Column(name = "for_who")
	private String forWho;

	@Column(name = "price")
	private Integer price;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "trainingPlan", orphanRemoval = true)
	private Set<UserTraining> userTrainingPlans = new HashSet<>();

	public TrainingPlan() {

	}

	public TrainingPlan(String name, String intensity, String day, String forWho, Integer price) {
		this.name = name;
		this.intensity = intensity;
		this.day = day;
		this.forWho = forWho;
		this.price = price;
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

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getForWho() {
		return forWho;
	}

	public void setForWho(String forWho) {
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

	public Set<UserTraining> getUserTrainingPlans() {
		return userTrainingPlans;
	}

	public void setUserTrainingPlans(Set<UserTraining> userTrainingPlans) {
		this.userTrainingPlans = userTrainingPlans;
	}

	@Override
	public String toString() {
		return "TrainingPlan [trainingPlanId=" + trainingPlanId + ", trainer=" + trainer + ", name=" + name
				+ ", intensity=" + intensity + ", day=" + day + ", forWho=" + forWho + ", price=" + price + "]";
	}
}
