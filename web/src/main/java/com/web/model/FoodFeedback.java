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
@Table(name = "foods_feedbacks")
public class FoodFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "food_feedback_id")
	private Integer foodFeedbackId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "food_id")
	private Food food;

	@Column(name = "date_of_feedback_provided")
	@CreationTimestamp
	private Timestamp dateOfFeedbackProvied;

	@Column(name = "message")
	private String message;

	@Column(name = "rating")
	private Integer rating;

	public FoodFeedback() {
	}

	public FoodFeedback(Integer foodFeedbackId, Account user, Food food, Timestamp dateOfFeedbackProvied,
			String message, Integer rating) {
		this.foodFeedbackId = foodFeedbackId;
		this.user = user;
		this.food = food;
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
		this.message = message;
		this.rating = rating;
	}

	public Integer getFoodFeedbackId() {
		return foodFeedbackId;
	}

	public void setFoodFeedbackId(Integer foodFeedbackId) {
		this.foodFeedbackId = foodFeedbackId;
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

	public Timestamp getDateOfFeedbackProvied() {
		return dateOfFeedbackProvied;
	}

	public void setDateOfFeedbackProvied(Timestamp dateOfFeedbackProvied) {
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
