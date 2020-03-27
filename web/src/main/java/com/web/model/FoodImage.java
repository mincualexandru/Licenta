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

@Entity
@Table(name = "foods_images")
public class FoodImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "food_image_id")
	private int foodImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "food_id", nullable = false)
	private Food food;

	@Column(name = "path")
	private String path;

	@Column(name = "file_name")
	private String fileName;

	public FoodImage() {
		super();
	}

	public FoodImage(int foodImageId, Food food, String path, String fileName) {
		this.foodImageId = foodImageId;
		this.food = food;
		this.path = path;
		this.fileName = fileName;
	}

	public int getFoodImageId() {
		return foodImageId;
	}

	public void setFoodImageId(int foodImageId) {
		this.foodImageId = foodImageId;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
