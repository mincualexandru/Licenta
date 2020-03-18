package com.web.utils;

public class MeasurementObiective {

	String name;
	String date;
	Float value;

	public MeasurementObiective() {
	}

	public MeasurementObiective(String name, String date, Float value) {
		super();
		this.name = name;
		this.date = date;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "MeasurementObiective [name=" + name + ", date=" + date + ", value=" + value + "]";
	}
}
