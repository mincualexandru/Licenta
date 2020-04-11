package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "experiences")
public class Experience {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "experience_id")
	private Integer experienceId;

	@Column(name = "start")
	@Pattern(regexp = "^(194[5-9]|19[5-9]\\d|200\\d|201[0-9])$", message = "Anul trebuie sa fie cuprins intre 1945 - 2019")
	private String start;

	@Column(name = "end")
	@Pattern(regexp = "^(194[5-9]|19[5-9]\\d|200\\d|201[0-9]|2020)$", message = "Anul trebuie sa fie cuprins intre 1945 - 2020")
	private String end;

	@Column(name = "name")
	@Size(min = 4, message = "Valoarea minima a dimensiunii campului este de 4 caractere")
	private String name;

	@Column(name = "city")
	private String city;

	@ManyToOne
	@JoinColumn(name = "account_information_id")
	private AccountInformation accountInformation;

	public Experience() {
		super();
	}

	public Experience(Integer experienceId, @Past(message = "Nu poti introduce o data din viitor") String start,
			@Past(message = "Nu poti introduce o data din viitor") String end,
			@Size(min = 4, message = "Valoarea minima a dimensiunii campului este de 4 caractere") String name,
			@Pattern(regexp = "^[A-Z][a-zA-Z]{3,15}(?: [A-Z][a-zA-Z]*){0,2}$", message = "Orasul introdus gresit") String city,
			AccountInformation accountInformation) {
		super();
		this.experienceId = experienceId;
		this.start = start;
		this.end = end;
		this.name = name;
		this.city = city;
		this.accountInformation = accountInformation;
	}

	public Integer getExperienceId() {
		return experienceId;
	}

	public void setExperienceId(Integer experienceId) {
		this.experienceId = experienceId;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public AccountInformation getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
	}

	@Override
	public String toString() {
		return "Experience [experienceId=" + experienceId + ", start=" + start + ", end=" + end + ", name=" + name
				+ ", city=" + city + "]";
	}
}
