package com.web.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Range;

import com.web.utils.ExerciseCategory;
import com.web.utils.TrainedMuscleGroup;

@Entity
@Table(name = "exercises")
public class Exercise implements Comparable<Exercise> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exercise_id")
	private int exerciseId;

	@ManyToOne
	@JoinColumn(name = "training_plan_id")
	private HelperPlan trainingPlan;

	@Column(name = "category")
	@Enumerated(EnumType.STRING)
	private ExerciseCategory exerciseCategory;

	@Column(name = "name")
	@NotEmpty(message = "**Campul este obligatoriu")
	private String name;

	@Column(name = "calories_burned")
	@Range(min = 0, message = "**Valorile negative nu sunt permise")
	@NotNull(message = "**Campul este obligatoriu")
	private Integer caloriesBurned;

	@Column(name = "number_of_series")
	@Range(min = 0, message = "**Valorile negative nu sunt permise")
	@NotNull(message = "**Campul este obligatoriu")
	private Integer numberOfSeries;

	@Column(name = "number_of_reps")
	@Range(min = 0, message = "**Valorile negative nu sunt permise")
	@NotNull(message = "**Campul este obligatoriu")
	private Integer numberOfReps;

	@Column(name = "execution")
	@NotEmpty(message = "**Campul este obligatoriu")
	private String execution;

	@Column(name = "trainedMuscleGroup")
	@Enumerated(EnumType.STRING)
	private TrainedMuscleGroup trainedMuscleGroup;

	@Column(name = "date")
	@CreationTimestamp
	private Timestamp createDateTime;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", orphanRemoval = true)
	private Set<ExerciseImage> exerciseImages = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", orphanRemoval = true)
	private Set<ExerciseAdvice> exerciseAdvices = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", orphanRemoval = true)
	private Set<ExerciseDone> exerciseDone = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", orphanRemoval = true)
	private Set<ExerciseFeedback> exercisesFeedback = new HashSet<>();

	public Exercise() {
	}

	public Exercise(String name, Integer caloriesBurned, Integer numberOfSeries, Integer numberOfReps,
			TrainedMuscleGroup trainedMuscleGroup, String execution) {
		this.name = name;
		this.caloriesBurned = caloriesBurned;
		this.numberOfSeries = numberOfSeries;
		this.numberOfReps = numberOfReps;
		this.trainedMuscleGroup = trainedMuscleGroup;
		this.execution = execution;
	}

	public Integer getNumberOfSeries() {
		return numberOfSeries;
	}

	public void setNumberOfSeries(Integer numberOfSeries) {
		this.numberOfSeries = numberOfSeries;
	}

	public Integer getNumberOfReps() {
		return numberOfReps;
	}

	public void setNumberOfReps(Integer numberOfReps) {
		this.numberOfReps = numberOfReps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCaloriesBurned() {
		return caloriesBurned;
	}

	public void setCaloriesBurned(Integer caloriesBurned) {
		this.caloriesBurned = caloriesBurned;
	}

	public Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}

	public int getExerciseId() {
		return exerciseId;
	}

	public void setExerciseId(int exerciseId) {
		this.exerciseId = exerciseId;
	}

	public TrainedMuscleGroup getTrainedMuscleGroup() {
		return trainedMuscleGroup;
	}

	public void setTrainedMuscleGroup(TrainedMuscleGroup trainedMuscleGroup) {
		this.trainedMuscleGroup = trainedMuscleGroup;
	}

	public Set<ExerciseImage> getExerciseImages() {
		return exerciseImages;
	}

	public void setExerciseImages(Set<ExerciseImage> exerciseImages) {
		this.exerciseImages = exerciseImages;
	}

	public Set<ExerciseAdvice> getExerciseAdvices() {
		return exerciseAdvices;
	}

	public void setExerciseAdvices(Set<ExerciseAdvice> exerciseAdvices) {
		this.exerciseAdvices = exerciseAdvices;
	}

	public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	public HelperPlan getTrainingPlan() {
		return trainingPlan;
	}

	public void setTrainingPlan(HelperPlan trainingPlan) {
		this.trainingPlan = trainingPlan;
	}

	public Set<ExerciseDone> getExerciseDone() {
		return exerciseDone;
	}

	public void setExerciseDone(Set<ExerciseDone> exerciseDone) {
		this.exerciseDone = exerciseDone;
	}

	public Set<ExerciseFeedback> getExercisesFeedback() {
		return exercisesFeedback;
	}

	public void setExercisesFeedback(Set<ExerciseFeedback> exercisesFeedback) {
		this.exercisesFeedback = exercisesFeedback;
	}

	public ExerciseCategory getExerciseCategory() {
		return exerciseCategory;
	}

	public void setExerciseCategory(ExerciseCategory category) {
		this.exerciseCategory = category;
	}

	@Override
	public String toString() {
		return "Exercise [exerciseId=" + exerciseId + ", name=" + name + ", caloriesBurned=" + caloriesBurned
				+ ", numberOfSeries=" + numberOfSeries + ", numberOfReps=" + numberOfReps + ", trainedMuscleGroup="
				+ trainedMuscleGroup + ", createDateTime=" + createDateTime + "]";
	}

	@Override
	public int compareTo(Exercise o) {
		return (int) (this.exerciseId - o.getExerciseId());
	}
}
