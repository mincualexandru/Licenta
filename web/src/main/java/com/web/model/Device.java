package com.web.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "devices")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "device_id")
	private Integer deviceId;

	@Column(name = "name")
	private String name;

	@Column(name = "company")
	private String company;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "price")
	private Integer price;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "device", orphanRemoval = true)
	@JsonIgnore
	private Set<UserDevice> userDevices = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	@JoinTable(name = "device_type_measurement", joinColumns = {
			@JoinColumn(name = "device_id") }, inverseJoinColumns = { @JoinColumn(name = "type_measurement_id") })
	private Set<TypeMeasurement> typeMeasurements = new HashSet<>();

	public Device() {
	}

	public Device(Integer deviceId, String name, String company, String serialNumber, Integer price,
			Set<UserDevice> userDevices) {
		super();
		this.deviceId = deviceId;
		this.name = name;
		this.company = company;
		this.serialNumber = serialNumber;
		this.price = price;
		this.userDevices = userDevices;
	}

	public Device(String name, String company, String serialNumber, Integer price) {
		this.name = name;
		this.company = company;
		this.serialNumber = serialNumber;
		this.price = price;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Set<UserDevice> getUserDevices() {
		return userDevices;
	}

	public void setUserDevices(Set<UserDevice> userDevices) {
		this.userDevices = userDevices;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Set<TypeMeasurement> getTypeMeasurements() {
		return typeMeasurements;
	}

	public void setTypeMeasurements(Set<TypeMeasurement> typeMeasurements) {
		this.typeMeasurements = typeMeasurements;
	}

	@Override
	public String toString() {
		return "Device [deviceId=" + deviceId + ", name=" + name + ", company=" + company + ", serialNumber="
				+ serialNumber + ", price=" + price + ", userDevices=" + userDevices + "]";
	}
}
