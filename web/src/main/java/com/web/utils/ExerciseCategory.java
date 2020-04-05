package com.web.utils;

public enum ExerciseCategory {

	ANDURANTA("Anduranta"), AEROBIC("Aerobic"), STRECHING("STRECHING"), ECHILIBRU("Echilibru"),
	REZISTENTA("Rezistenta"), MASA("Masa Musculara");

	private final String exerciseCategory;

	ExerciseCategory(String exerciseCategory) {
		this.exerciseCategory = exerciseCategory;
	}

	public String getExerciseCategory() {
		return exerciseCategory;
	}
}
