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
@Table(name = "users_plans")
public class UserPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_plan_id")
	private Integer userPlanId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "helper_plan_id")
	private HelperPlan helperPlan;

	@Column(name = "bought")
	private boolean bought;

	@Column(name = "date_of_purchase")
	@CreationTimestamp
	private Timestamp dateOfPurchase;

	public UserPlan() {
	}

	public UserPlan(Integer userPlanId, Account user, HelperPlan helperPlan, boolean bought, Timestamp dateOfPurchase) {
		super();
		this.userPlanId = userPlanId;
		this.user = user;
		this.helperPlan = helperPlan;
		this.bought = bought;
		this.dateOfPurchase = dateOfPurchase;
	}

	public Integer getUserPlanId() {
		return userPlanId;
	}

	public void setUserPlanId(Integer userPlanId) {
		this.userPlanId = userPlanId;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public HelperPlan getHelperPlan() {
		return helperPlan;
	}

	public void setHelperPlan(HelperPlan helperPlan) {
		this.helperPlan = helperPlan;
	}

	public boolean isBought() {
		return bought;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}

	public Timestamp getDateOfPurchase() {
		return dateOfPurchase;
	}

	public void setDateOfPurchase(Timestamp dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}
}
