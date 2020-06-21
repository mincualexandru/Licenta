package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "foods_recommendations")
public class FoodRecommendation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "food_recommendation_id")
	private int foodRecommendationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "food_id", nullable = false)
	private Food food;

	@Column(name = "recommendation")
	@Size(min = 3, message = "**Beneficiul trebuie sa aiba minim 3 caractere")
	private String recommendation;

	public FoodRecommendation() {
		super();
	}

	public FoodRecommendation(int foodRecommendationId, Food food, String recommendation) {
		this.foodRecommendationId = foodRecommendationId;
		this.food = food;
		this.recommendation = recommendation;
	}

	public int getFoodRecommendationId() {
		return foodRecommendationId;
	}

	public void setFoodRecommendationId(int foodRecommendationId) {
		this.foodRecommendationId = foodRecommendationId;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
}
