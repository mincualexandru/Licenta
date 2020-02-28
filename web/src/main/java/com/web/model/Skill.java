package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "skills")
public class Skill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skill_id")
	private Integer skillId;
	
	@Column(name = "description")
	@Size(min = 3, message = "Valoarea minima a dimensiunii campului este de 3 caractere")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "account_information_id")
    private AccountInformation accountInformation;

	public Skill() {
		super();
	}

	public Skill(String description) {
		super();
		this.description = description;
	}

	public Integer getSkillId() {
		return skillId;
	}

	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountInformation getAccountInformation() {
		return accountInformation;
	}

	public void setAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
	}

	@Override
	public String toString() {
		return "Skill [skillId=" + skillId + ", description=" + description + "]";
	}
}
