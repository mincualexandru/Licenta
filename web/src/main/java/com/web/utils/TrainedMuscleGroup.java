package com.web.utils;

public enum TrainedMuscleGroup {

	PIEPT("Piept"), SPATE("Spate"), UMERI("Umeri"), TRICEPS("Triceps"), BICEPS("Biceps"), ABDOMEN("Abdomen"),
	TRAPEZ("Trapez"), COAPSE("Coapse"), GAMBE("Gambe"), ANTEBRAT("Antebrat");

	private final String muscleGroup;

	TrainedMuscleGroup(String muscleGroup) {
		this.muscleGroup = muscleGroup;
	}

	public String getMuscleGroup() {
		return muscleGroup;
	}

}
