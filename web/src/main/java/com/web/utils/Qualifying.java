package com.web.utils;

public enum Qualifying {

	FOARTENEMULTUMIT("Foarte nemultumit"), NEMULTUMIT("Nemultumit"), SATISFACATOR("Satisfacator"), MULTUMIT("Multumit"),
	FOARTEMULTUMIT("Foarte multumit");

	private final String qualifying;

	Qualifying(String qualifying) {
		this.qualifying = qualifying;
	}

	public String getQualifying() {
		return qualifying;
	}

}
