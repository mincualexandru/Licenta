package com.web.utils;

public enum Gender {

	BARBAT("Barbat"),
	FEMEIE("Femeie");
	
	private final String gender;
	
	Gender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}
	
}
