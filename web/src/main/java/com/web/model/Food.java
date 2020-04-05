package com.web.model;

import java.sql.Timestamp;
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

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "foods")
public class Food {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "food_id")
	private int foodId;

	@ManyToOne
	@JoinColumn(name = "diet_plan_id")
	private HelperPlan dietPlan;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "calories", nullable = true)
	private float calories;

	@Column(name = "protein", nullable = true)
	private float protein;

	@Column(name = "lipids", nullable = true)
	private float lipids;

	@Column(name = "carbohydrates", nullable = true)
	private float carbohydrates;

	@Column(name = "fiber", nullable = true)
	private float fiber;

	@Column(name = "weight", nullable = true)
	private float weight;

	@Column(name = "recommended_time_range", nullable = true)
	private String recommendedTimeRange;

	@Column(name = "recommended_consumption_time", nullable = false)
	private String recommendedConsumptionTime;

	@Column(name = "date")
	@CreationTimestamp
	private Timestamp createDateTime;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "food", orphanRemoval = true)
	private Set<FoodImage> images = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "food", orphanRemoval = true)
	private Set<FoodRecommendation> recommendations = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "food", orphanRemoval = true)
	private Set<FoodEaten> foodsEaten = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "food", orphanRemoval = true)
	private Set<FoodFeedback> foodsFeedbacks = new HashSet<>();

	public Food() {
	}

	public Food(int foodId, HelperPlan dietPlan, String name, float calories, float protein, float lipids,
			float carbohydrates, float fiber, float weight, String recommendedTimeRange, Timestamp createDateTime) {
		super();
		this.foodId = foodId;
		this.dietPlan = dietPlan;
		this.name = name;
		this.calories = calories;
		this.protein = protein;
		this.lipids = lipids;
		this.carbohydrates = carbohydrates;
		this.fiber = fiber;
		this.weight = weight;
		this.recommendedTimeRange = recommendedTimeRange;
		this.createDateTime = createDateTime;
	}

	public int getFoodId() {
		return foodId;
	}

	public void setFoodId(int foodId) {
		this.foodId = foodId;
	}

	public HelperPlan getDietPlan() {
		return dietPlan;
	}

	public void setDietPlan(HelperPlan dietPlan) {
		this.dietPlan = dietPlan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getCalories() {
		return calories;
	}

	public void setCalories(float calories) {
		this.calories = calories;
	}

	public float getProtein() {
		return protein;
	}

	public void setProtein(float protein) {
		this.protein = protein;
	}

	public float getLipids() {
		return lipids;
	}

	public void setLipids(float lipids) {
		this.lipids = lipids;
	}

	public float getCarbohydrates() {
		return carbohydrates;
	}

	public void setCarbohydrates(float carbohydrates) {
		this.carbohydrates = carbohydrates;
	}

	public float getFiber() {
		return fiber;
	}

	public void setFiber(float fiber) {
		this.fiber = fiber;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getRecommendedTimeRange() {
		return recommendedTimeRange;
	}

	public void setRecommendedTimeRange(String recommendedTimeRange) {
		this.recommendedTimeRange = recommendedTimeRange;
	}

	public Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Set<FoodImage> getImages() {
		return images;
	}

	public void setImages(Set<FoodImage> images) {
		this.images = images;
	}

	public Set<FoodRecommendation> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(Set<FoodRecommendation> recommendations) {
		this.recommendations = recommendations;
	}

	public Set<FoodEaten> getFoodsEaten() {
		return foodsEaten;
	}

	public void setFoodsEaten(Set<FoodEaten> foodsEaten) {
		this.foodsEaten = foodsEaten;
	}

	public Set<FoodFeedback> getFoodsFeedbacks() {
		return foodsFeedbacks;
	}

	public void setFoodsFeedbacks(Set<FoodFeedback> foodsFeedbacks) {
		this.foodsFeedbacks = foodsFeedbacks;
	}

	public String getRecommendedConsumptionTime() {
		return recommendedConsumptionTime;
	}

	public void setRecommendedConsumptionTime(String recommendedConsumptionTime) {
		this.recommendedConsumptionTime = recommendedConsumptionTime;
	}
}
