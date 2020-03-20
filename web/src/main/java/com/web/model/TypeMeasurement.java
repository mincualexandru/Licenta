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
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "types_measurements")
public class TypeMeasurement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "type_measurement_id")
	private Integer typeMeasurementId;

	@Column(name = "type")
	private String type;

	@Column(name = "goal_min")
	private Float goalMin;

	@Column(name = "goal_max")
	private Float goalMax;

	@Column(name = "purpose")
	private String purpose;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE }, mappedBy = "typeMeasurements")
	private Set<Device> devices = new HashSet<>();

	public TypeMeasurement() {
	}

	public TypeMeasurement(Integer typeMeasurementId, String type, Float goalMin, Float goalMax, String purpose,
			Set<Device> devices) {
		super();
		this.typeMeasurementId = typeMeasurementId;
		this.type = type;
		this.goalMin = goalMin;
		this.goalMax = goalMax;
		this.purpose = purpose;
		this.devices = devices;
	}

	public Integer getTypeMeasurementId() {
		return typeMeasurementId;
	}

	public void setTypeMeasurementId(Integer typeMeasurementId) {
		this.typeMeasurementId = typeMeasurementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public void setDevices(Set<Device> devices) {
		this.devices = devices;
	}

	public Float getGoalMin() {
		return goalMin;
	}

	public void setGoalMin(Float goalMin) {
		this.goalMin = goalMin;
	}

	public Float getGoalMax() {
		return goalMax;
	}

	public void setGoalMax(Float goalMax) {
		this.goalMax = goalMax;
	}
}
