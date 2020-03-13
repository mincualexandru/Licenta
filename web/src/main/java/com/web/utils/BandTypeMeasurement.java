package com.web.utils;

public enum BandTypeMeasurement {

	HKQUANTITYTYPEIDENTIFIERHEARTRATE("HKQuantityTypeIdentifierHeartRate"),
	HKQUANTITYTYPEIDENTIFIERSTEPCOUNT("HKQuantityTypeIdentifierStepCount"),
	HKCATEGORYTYPEIDENTIFIERSLEEPANALYSIS("HKCategoryTypeIdentifierSleepAnalysis"),
	HKQUANTITYTYPEIDENTIFIERDISTANCEWALKINGRUNNING("HKQuantityTypeIdentifierDistanceWalkingRunning"),
	HKQUANTITYTYPEIDENTIFIERFLIGHTSCLIMBED("HKQuantityTypeIdentifierFlightsClimbed");

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
