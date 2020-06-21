package com.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "exercises_advices")
public class ExerciseAdvice {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "exercise_advice_id")
	private int exerciseAdviceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	@Column(name = "advice")
	@Size(min = 3, message = "**Sfatul trebuie sa aiba minim 3 caractere")
	private String advice;

	public ExerciseAdvice() {
	}

	public ExerciseAdvice(int exerciseAdviceId, Exercise exercise, String advice) {
		this.exerciseAdviceId = exerciseAdviceId;
		this.exercise = exercise;
		this.advice = advice;
	}

	public int getExerciseAdviceId() {
		return exerciseAdviceId;
	}

	public void setExerciseAdviceId(int exerciseAdviceId) {
		this.exerciseAdviceId = exerciseAdviceId;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	@Override
	public String toString() {
		return "ExerciseAdvice [exerciseAdviceId=" + exerciseAdviceId + ", exercise=" + exercise + ", advice=" + advice
				+ "]";
	}
}
