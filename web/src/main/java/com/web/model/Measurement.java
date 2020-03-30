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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "measurements")
public class Measurement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "measurement_id")
	private Integer measurementId;

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "user_device_id")
	private UserDevice userDevice;

	@Column(name = "name")
	private String name;

	@Column(name = "value")
	private float value;

	@Column(name = "unit_of_measurement")
	private String unitOfMeasurement;

	@Column(name = "start_date")
	private Timestamp startDate;

	@Column(name = "end_date")
	private Timestamp endDate;

	@Column(name = "from_xml")
	private Boolean fromXml;

	public Measurement() {
	}

	public Measurement(Integer measurementId, UserDevice userDevice, String name, float value, String unitOfMeasurement,
			Timestamp startDate, Timestamp endDate, Boolean fromXml) {
		super();
		this.measurementId = measurementId;
		this.userDevice = userDevice;
		this.name = name;
		this.value = value;
		this.unitOfMeasurement = unitOfMeasurement;
		this.startDate = startDate;
		this.endDate = endDate;
		this.fromXml = fromXml;
	}

	public Integer getMeasurementId() {
		return measurementId;
	}

	public void setMeasurementId(Integer measurementId) {
		this.measurementId = measurementId;
	}

	public UserDevice getUserDevice() {
		return userDevice;
	}

	public void setUserDevice(UserDevice userDevice) {
		this.userDevice = userDevice;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Boolean getFromXml() {
		return fromXml;
	}

	public void setFromXml(Boolean fromXml) {
		this.fromXml = fromXml;
	}

	@Override
	public String toString() {
		return "Measurement [measurementId=" + measurementId + ", userDevice=" + userDevice + ", name=" + name
				+ ", value=" + value + ", unitOfMeasurement=" + unitOfMeasurement + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", fromXml=" + fromXml + "]";
	}
}