package com.web.utils;

import java.sql.Timestamp;

public class Product {

	Integer productId;

	String productName;

	Integer price;

	String companyName;

	Gender forWho;

	String type;

	Timestamp dateOfPurchased;

	String imageName;

	String description;

	public Product() {
	}

	public Product(Integer productId, String productName, Integer price, String companyName, Gender forWho, String type,
			Timestamp dateOfPurchased) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.companyName = companyName;
		this.forWho = forWho;
		this.type = type;
		this.dateOfPurchased = dateOfPurchased;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Gender getForWho() {
		return forWho;
	}

	public void setForWho(Gender forWho) {
		this.forWho = forWho;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getDateOfPurchased() {
		return dateOfPurchased;
	}

	public void setDateOfPurchased(Timestamp dateOfPurchased) {
		this.dateOfPurchased = dateOfPurchased;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", productName=" + productName + ", price=" + price
				+ ", companyName=" + companyName + ", forWho=" + forWho + ", type=" + type + ", dateOfPurchased="
				+ dateOfPurchased + ", imageName=" + imageName + ", description=" + description + "]";
	}
}
