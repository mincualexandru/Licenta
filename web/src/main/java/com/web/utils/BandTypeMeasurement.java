package com.web.utils;

public enum BandTypeMeasurement {

	ENERGYBURNED("HKQuantityTypeIdentifierActiveEnergyBurned"), HEARTRATE("HKQuantityTypeIdentifierHeartRate"),
	STEPCOUNT("HKQuantityTypeIdentifierStepCount"), SLEEPANALYSISASSLEEP("HKCategoryTypeIdentifierSleepAnalysis");

	private final String bandTypeMeasurement;

	BandTypeMeasurement(String bandTypeMeasurement) {
		this.bandTypeMeasurement = bandTypeMeasurement;
	}

	public String getBandTypeMeasurement() {
		return bandTypeMeasurement;
	}

	public static boolean contains(String test) {

		for (BandTypeMeasurement type : BandTypeMeasurement.values()) {
			if (type.getBandTypeMeasurement().equals(test)) {
				return true;
			}
		}

		return false;
	}
}
