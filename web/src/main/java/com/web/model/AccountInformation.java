package com.web.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Size;

@Entity
@Table(name = "account_information")
public class AccountInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_information_id")
	private Integer accountInformationId;

	@Column(name = "description")
	@Size(min = 3, message = "**Valoarea minima a dimensiunii campului este de 3 caractere")
	private String description;

	@Column(name = "job_obiective")
	@Size(min = 3, message = "**Valoarea minima a dimensiunii campului este de 3 caractere")
	private String jobObiective;

	@OneToOne(optional = false)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountInformation", orphanRemoval = true)
	@Valid
	private List<Skill> skills = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountInformation", orphanRemoval = true)
	@Valid
	private List<Experience> experiences = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountInformation", orphanRemoval = true)
	@Valid
	private List<Education> education = new ArrayList<>();

	public AccountInformation() {
		super();
	}

	public AccountInformation(Integer accountInformationId, String description, String jobObiective, Account account,
			List<Skill> skills, List<Experience> experiences, List<Education> education) {
		super();
		this.accountInformationId = accountInformationId;
		this.description = description;
		this.jobObiective = jobObiective;
		this.account = account;
		this.skills = skills;
		this.experiences = experiences;
		this.education = education;
	}

	public Integer getAccountInformationId() {
		return accountInformationId;
	}

	public void setAccountInformationId(Integer accountInformationId) {
		this.accountInformationId = accountInformationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJobObiective() {
		return jobObiective;
	}

	public void setJobObiective(String jobObiective) {
		this.jobObiective = jobObiective;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	public List<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(List<Experience> experiences) {
		this.experiences = experiences;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}
}
