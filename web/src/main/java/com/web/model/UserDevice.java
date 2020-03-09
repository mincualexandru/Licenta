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
@Table(name = "users_devices")
public class UserDevice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_device_id")
	private Integer userDeviceId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "device_id")
	private Device device;

	@Column(name = "bought")
	private boolean bought;

	@Column(name = "date_of_purchase")
	@CreationTimestamp
	private Timestamp dateOfPurchase;

	public UserDevice() {
	}

	public UserDevice(Integer userDeviceId, Account user, Device device, boolean bought, Timestamp dateOfPurchase) {
		super();
		this.userDeviceId = userDeviceId;
		this.user = user;
		this.device = device;
		this.bought = bought;
		this.dateOfPurchase = dateOfPurchase;
	}

	public Integer getUserDeviceId() {
		return userDeviceId;
	}

	public void setUserDeviceId(Integer userDeviceId) {
		this.userDeviceId = userDeviceId;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
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

	@Override
	public String toString() {
		return "UserDevice [userDeviceId=" + userDeviceId + ", user=" + user + ", device=" + device + ", bought="
				+ bought + "]";
	}
}
