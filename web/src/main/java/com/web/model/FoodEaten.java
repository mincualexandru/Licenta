package com.web.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "foods_eaten")
public class FoodEaten {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "food_eaten_id")
	private Integer foodEatenId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "food_id")
	private Food food;

	@Column(name = "date_of_execution")
	@CreationTimestamp
	private Timestamp dateOfExecution;

	public FoodEaten() {
	}

	public FoodEaten(Integer foodEatenId, Account user, Food food, Timestamp dateOfExecution) {
		this.foodEatenId = foodEatenId;
		this.user = user;
		this.food = food;
		this.dateOfExecution = dateOfExecution;
	}

	public Integer getFoodEatenId() {
		return foodEatenId;
	}

	public void setFoodEatenId(Integer foodEatenId) {
		this.foodEatenId = foodEatenId;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public Timestamp getDateOfExecution() {
		return dateOfExecution;
	}

	public void setDateOfExecution(Timestamp dateOfExecution) {
		this.dateOfExecution = dateOfExecution;
	}
}
