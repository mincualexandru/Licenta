package com.web.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.web.utils.Qualifying;

@Entity
@Table(name = "helpers_feedbacks")
public class HelperFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "helper_feedback_id")
	private Integer helperFeedbackId;

	@ManyToOne
	@JoinColumn(name = "helper_id")
	private Account helper;

	@ManyToOne
	@JoinColumn(name = "learner_id")
	private Account learner;

	@Column(name = "qualifying")
	@Enumerated(EnumType.STRING)
	private Qualifying qualifying;

	@Column(name = "reason")
	private String reason;

	@Column(name = "date_of_feedback_provided")
	@CreationTimestamp
	private Timestamp dateOfFeedbackProvied;

	public HelperFeedback() {
	}

	public HelperFeedback(Integer helperFeedbackId, Account helper, Account learner, Qualifying qualifying,
			String reason, Timestamp dateOfFeedbackProvied) {
		super();
		this.helperFeedbackId = helperFeedbackId;
		this.helper = helper;
		this.learner = learner;
		this.qualifying = qualifying;
		this.reason = reason;
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
	}

	public Integer getHelperFeedbackId() {
		return helperFeedbackId;
	}

	public void setHelperFeedbackId(Integer helperFeedbackId) {
		this.helperFeedbackId = helperFeedbackId;
	}

	public Account getHelper() {
		return helper;
	}

	public void setHelper(Account helper) {
		this.helper = helper;
	}

	public Account getLearner() {
		return learner;
	}

	public void setLearner(Account learner) {
		this.learner = learner;
	}

	public Qualifying getQualifying() {
		return qualifying;
	}

	public void setQualifying(Qualifying qualifying) {
		this.qualifying = qualifying;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Timestamp getDateOfFeedbackProvied() {
		return dateOfFeedbackProvied;
	}

	public void setDateOfFeedbackProvied(Timestamp dateOfFeedbackProvied) {
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
	}

	@Override
	public String toString() {
		return "HelperFeedback [helperFeedbackId=" + helperFeedbackId + ", helper=" + helper + ", learner=" + learner
				+ ", qualifying=" + qualifying + ", reason=" + reason + ", dateOfFeedbackProvied="
				+ dateOfFeedbackProvied + "]";
	}
}
