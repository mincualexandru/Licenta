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

@Entity
@Table(name = "exercises_images")
public class ExerciseImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "exercise_images_id")
	private int exerciseImagesId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;

	@Column(name = "path")
	private String path;

	@Column(name = "file_name")
	private String fileName;

	public ExerciseImage() {

	}

	public ExerciseImage(int exerciseImagesId, Exercise exercise, String path, String fileName) {
		this.exerciseImagesId = exerciseImagesId;
		this.exercise = exercise;
		this.path = path;
		this.fileName = fileName;
	}

	public int getExerciseImagesId() {
		return exerciseImagesId;
	}

	public void setExerciseImagesId(int exerciseImagesId) {
		this.exerciseImagesId = exerciseImagesId;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
