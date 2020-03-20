package com.web.utils;

public enum ScaleTypeMeasurement {

	FATPERCENTAGE("HKQuantityTypeIdentifierBodyFatPercentage"), LEANMASS("HKQuantityTypeIdentifierLeanBodyMass"),
	MASSINDEX("HKQuantityTypeIdentifierBodyMassIndex"), MASS("HKQuantityTypeIdentifierBodyMass"),
	HEIGHT("HKQuantityTypeIdentifierHeight");

	private final String scaleTypeMeasurement;

	ScaleTypeMeasurement(String scaleTypeMeasurement) {
		this.scaleTypeMeasurement = scaleTypeMeasurement;
	}

	public String getScaleTypeMeasurement() {
		return scaleTypeMeasurement;
	}

	public static boolean contains(String test) {

		for (ScaleTypeMeasurement type : ScaleTypeMeasurement.values()) {
			if (type.getScaleTypeMeasurement().equals(test)) {
				return true;
			}
		}

		return false;
	}
}
