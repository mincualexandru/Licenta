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
@Table(name = "exercises_done")
public class ExerciseDone implements Comparable<ExerciseDone> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exercise_done_id")
	private Integer exerciseDoneId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Account user;

	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	@Column(name = "date_of_execution")
	@CreationTimestamp
	private Timestamp dateOfExecution;

	public ExerciseDone() {
	}

	public ExerciseDone(Integer exerciseDoneId, Account user, Exercise exercise, Timestamp dateOfExecution) {
		super();
		this.exerciseDoneId = exerciseDoneId;
		this.user = user;
		this.exercise = exercise;
		this.dateOfExecution = dateOfExecution;
	}

	public Integer getExerciseDoneId() {
		return exerciseDoneId;
	}

	public void setExerciseDoneId(Integer exerciseDoneId) {
		this.exerciseDoneId = exerciseDoneId;
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

	public Timestamp getDateOfExecution() {
		return dateOfExecution;
	}

	public void setDateOfExecution(Timestamp dateOfExecution) {
		this.dateOfExecution = dateOfExecution;
	}

	@Override
	public String toString() {
		return "ExerciseDone [exerciseDoneId=" + exerciseDoneId + ", user=" + user + ", exercise=" + exercise
				+ ", dateOfExecution=" + dateOfExecution + "]";
	}

	@Override
	public int compareTo(ExerciseDone o) {
		return (int) (this.exerciseDoneId - o.getExerciseDoneId());
	}
}
