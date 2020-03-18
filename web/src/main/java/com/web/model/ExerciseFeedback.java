package com.web.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "exercises_feedbacks")
public class ExerciseFeedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exercise_feedback_id")
	private Integer exerciseFeedbackId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	@Column(name = "date_of_feedback_provided")
	@CreationTimestamp
	private Timestamp dateOfFeedbackProvied;

	@Column(name = "message")
	private String message;

	@Column(name = "rating")
	private Integer rating;

	public ExerciseFeedback() {
	}

	public ExerciseFeedback(Integer exerciseFeedbackId, Account user, Exercise exercise,
			Timestamp dateOfFeedbackProvied, String message, Integer rating) {
		super();
		this.exerciseFeedbackId = exerciseFeedbackId;
		this.user = user;
		this.exercise = exercise;
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
		this.message = message;
		this.rating = rating;
	}

	public Integer getExerciseFeedbackId() {
		return exerciseFeedbackId;
	}

	public void setExerciseFeedbackId(Integer exerciseFeedbackId) {
		this.exerciseFeedbackId = exerciseFeedbackId;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public Timestamp getDateOfFeedbackProvied() {
		return dateOfFeedbackProvied;
	}

	public void setDateOfFeedbackProvied(Timestamp dateOfFeedbackProvied) {
		this.dateOfFeedbackProvied = dateOfFeedbackProvied;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "ExerciseFeedback [exerciseFeedbackId=" + exerciseFeedbackId + ", user=" + user + ", exercise="
				+ exercise + ", dateOfFeedbackProvied=" + dateOfFeedbackProvied + ", message=" + message + ", rating="
				+ rating + "]";
	}
}
