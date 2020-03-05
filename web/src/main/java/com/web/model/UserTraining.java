package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users_trainings")
public class UserTraining {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_training_id")
	private Integer userTrainingId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "training_plan_id")
	private TrainingPlan trainingPlan;

	@Column(name = "bought")
	private boolean bought;

	public UserTraining() {
	}

	public UserTraining(Integer userTrainingId, Account user, TrainingPlan trainingPlan, boolean bought) {
		super();
		this.userTrainingId = userTrainingId;
		this.user = user;
		this.trainingPlan = trainingPlan;
		this.bought = bought;
	}

	public Integer getUserTrainingId() {
		return userTrainingId;
	}

	public void setUserTrainingId(Integer userTrainingId) {
		this.userTrainingId = userTrainingId;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public TrainingPlan getTrainingPlan() {
		return trainingPlan;
	}

	public void setTrainingPlan(TrainingPlan trainingPlan) {
		this.trainingPlan = trainingPlan;
	}

	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}

	@Override
	public String toString() {
		return "UserTraining [userTrainingId=" + userTrainingId + ", user=" + user + ", trainingPlan=" + trainingPlan
				+ ", bought=" + bought + "]";
	}
}
