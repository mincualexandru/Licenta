package com.web.utils;

public class Product {

	Integer productId;

	String productName;

	Integer price;

	String companyName;

	String forWho;

	String type;

	public Product() {
	}

	public Product(Integer productId, String productName, Integer price, String companyName, String forWho,
			String type) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.companyName = companyName;
		this.forWho = forWho;
		this.type = type;
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

	public String getForWho() {
		return forWho;
	}

	public void setForWho(String forWho) {
		this.forWho = forWho;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Product [productName=" + productName + ", price=" + price + ", companyName=" + companyName + ", forWho="
				+ forWho + ", type=" + type + "]";
	}
}
