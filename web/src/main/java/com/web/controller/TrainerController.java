package com.web.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Exercise;
import com.web.model.ExerciseAdvice;
import com.web.model.ExerciseImage;
import com.web.model.Role;
import com.web.model.TrainingPlan;
import com.web.model.UserTraining;
import com.web.service.AccountService;
import com.web.service.ExerciseAdviceService;
import com.web.service.ExerciseImageService;
import com.web.service.ExerciseService;
import com.web.service.TrainingPlanService;
import com.web.service.UserTrainingService;
import com.web.utils.Gender;
import com.web.utils.TrainedMuscleGroup;

@Controller
public class TrainerController {

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private TrainingPlanService trainingPlanService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseImageService exerciseImageService;

	@Autowired
	private ExerciseAdviceService exerciseAdviceService;

	@Autowired
	private UserTrainingService userTrainingService;

	@GetMapping(path = { "/trainer" })
	public String trainer(Model model, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		return "trainer/trainer";
	}

	@GetMapping(path = { "/training_plans" })
	public String trainingPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("trainingPlans",
				trainingPlanService.findAllByTrainingPlanNotAssociated(account.getAccountId()));
		return "trainer/training_plans";
	}

	@GetMapping(path = { "/purchased_training_plans" })
	public String purchasedTrainingPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<UserTraining> userTrainings = userTrainingService.findAll();
		userTrainings.removeIf(
				element -> !element.getTrainingPlan().getTrainer().getAccountId().equals(account.getAccountId()));
		model.addAttribute("trainingPlans", userTrainings);
		return "trainer/purchased_training_plans";
	}

	@GetMapping(path = { "/create_training_plan" })
	public String createTrainingPlan(Model model) {
		if (!model.containsAttribute("trainingPlan")) {
			model.addAttribute("trainingPlan", new TrainingPlan());
		}
		model.addAttribute("sex", Gender.values());
		return "trainer/create_training_plan";
	}

	@PostMapping(path = { "/create_training_plan_save" })
	public String createTrainingPlanSave(Model model, @Valid @ModelAttribute("trainingPlan") TrainingPlan trainingPlan,
			BindingResult bindingResult, RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.trainingPlan", bindingResult);
			attr.addFlashAttribute("trainingPlan", trainingPlan);
			return "redirect:/create_training_plan";
		} else {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Account trainer = accountService.findByUsername(auth.getName());
			trainingPlan.setTrainer(trainer);
			trainingPlanService.save(trainingPlan);
			return "redirect:/training_plans";
		}
	}

	@GetMapping(path = { "/edit_training_plan/{id}" })
	public String editTrainingPlan(Model model, @PathVariable("id") Integer trainingPlanId) {
		if (!model.containsAttribute("trainingPlan")) {
			TrainingPlan trainingPlan = trainingPlanService.findById(trainingPlanId).get();
			model.addAttribute("trainingPlan", trainingPlan);
		}
		model.addAttribute("sex", Gender.values());
		return "trainer/edit_training_plan";
	}

	@PostMapping(path = { "/edit_training_plan_save" })
	public String editTrainingPlanSave(@Valid @ModelAttribute("trainingPlan") TrainingPlan trainingPlan,
			BindingResult bindingResult, RedirectAttributes attr, Model model, @RequestParam Integer trainingPlanId) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.trainingPlan", bindingResult);
			attr.addFlashAttribute("trainingPlan", trainingPlan);
			return "redirect:/edit_training_plan/" + trainingPlanId;
		} else {
			TrainingPlan oldTrainingPlan = trainingPlanService.findById(trainingPlanId).get();
			oldTrainingPlan.setForWho(trainingPlan.getForWho());
			oldTrainingPlan.setIntensity(trainingPlan.getIntensity());
			oldTrainingPlan.setName(trainingPlan.getName());
			oldTrainingPlan.setPrice(trainingPlan.getPrice());
			oldTrainingPlan.setDateOfCreation(oldTrainingPlan.getDateOfCreation());
			oldTrainingPlan.setTrainer(oldTrainingPlan.getTrainer());
			trainingPlanService.save(oldTrainingPlan);
			return "redirect:/training_plans";
		}
	}

	@GetMapping(path = { "/create_exercise_for_training_plan/{id}" })
	public String createExerciseForTrainingPlan(Model model, @PathVariable("id") Integer trainingPlanId) {
		if (!model.containsAttribute("exercise")) {
			Exercise newExercise = new Exercise();
			model.addAttribute("exercise", newExercise);
		}
		model.addAttribute("muscleGroups", TrainedMuscleGroup.values());
		model.addAttribute("trainingPlanId", trainingPlanId);
		return "trainer/create_exercise_for_training_plan";
	}

	@PostMapping(path = { "/create_exercise_for_training_plan_save" })
	public String createExerciseForTrainingPlanSave(Model model, @RequestParam Integer trainingPlanId,
			@Valid @ModelAttribute("exercise") Exercise exercise, BindingResult bindingResult,
			RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.exercise", bindingResult);
			attr.addFlashAttribute("exercise", exercise);
			return "redirect:/create_exercise_for_training_plan/" + trainingPlanId;
		} else {
			exercise.setTrainingPlan(trainingPlanService.findById(trainingPlanId).get());
			exerciseService.save(exercise);
			return "redirect:/training_plans";
		}
	}

	@PostMapping(path = { "/exercises_training_plan" })
	public String createExerciseForTrainingPlanSave(Model model, @RequestParam Integer trainingPlanId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		for (Role role : account.getRoles()) {
			if (role.getName().equals("ROLE_USER")) {
				Set<Exercise> notPerformedExercises = exerciseService
						.findAllNotPerfomerdExercisesForTrainingPlanId(trainingPlanId);
				model.addAttribute("exercises", notPerformedExercises);
			} else if (role.getName().equals("ROLE_TRAINER")) {
				Set<Exercise> exercises = exerciseService.findAllByTrainingPlanTrainingPlanId(trainingPlanId);
				model.addAttribute("exercises", exercises);
			}
		}
		model.addAttribute("account", account);
		return "common/exercises_training_plan";
	}

	@GetMapping(path = { "/view_exercise/{id}" })
	public String viewExercise(Model model, @PathVariable("id") Integer exerciseId) {
		if (!(exerciseService.findById(exerciseId).isPresent())) {
			return "redirect:/error";
		}
		Exercise exercise = exerciseService.findById(exerciseId).get();
		Set<ExerciseAdvice> exerciseAdvice = exercise.getExerciseAdvices();
		List<ExerciseAdvice> exerciseAdviceSorted = exerciseAdvice.stream()
				.sorted((e1, e2) -> e1.getAdvice().compareTo(e2.getAdvice())).collect(Collectors.toList());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		model.addAttribute("exercise", exercise);
		model.addAttribute("exerciseAdviceSorted", exerciseAdviceSorted);
		model.addAttribute("exerciseImage", new ExerciseImage());
		model.addAttribute("exerciseAdvice", new ExerciseAdvice());
		return "common/view_exercise";
	}

	@PostMapping(path = { "/add_photo_for_exercise_save" })
	public String addPhotoForExerciseSave(Model model, @RequestParam Integer exerciseId,
			@RequestParam("imageFile") MultipartFile imageFile,
			@ModelAttribute("exerciseImage") ExerciseImage exerciseImage) {
		exerciseImage.setFileName(imageFile.getOriginalFilename());
		Exercise exercise = exerciseService.findById(exerciseId).get();
		exerciseImage.setExercise(exercise);
		try {
			exerciseImageService.saveImage(imageFile, exerciseImage);
			exerciseImage.setPath("/images/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		exerciseImageService.save(exerciseImage);
		return "redirect:/view_exercise/" + exerciseId;
	}

	@PostMapping(path = { "/add_advice_for_exercise_save" })
	public String addAdviceForExerciseSave(Model model, @ModelAttribute("exerciseAdvice") ExerciseAdvice exerciseAdvice,
			@RequestParam Integer exerciseId) {
		exerciseAdvice.setExercise(exerciseService.findById(exerciseId).get());
		exerciseAdviceService.save(exerciseAdvice);
		return "redirect:/view_exercise/" + exerciseId;
	}

	@PostMapping(path = { "/delete_photo_from_exercise" })
	public String deletePhotoFromExercise(Model model, @RequestParam Integer exerciseId,
			@RequestParam Integer photoId) {
		exerciseImageService.deleteByExerciseImagesIdAndExerciseExerciseId(photoId, exerciseId);
		return "redirect:/view_exercise/" + exerciseId;
	}

	@PostMapping(path = { "/edit_advice_exercise" })
	public String editAdviceExercise(Model model, @RequestParam Integer exerciseId, @RequestParam Integer adviceId,
			@RequestParam String editName) {
		ExerciseAdvice advice = exerciseAdviceService.findByExerciseAdviceIdAndExerciseExerciseId(adviceId, exerciseId);
		advice.setAdvice(editName);
		exerciseAdviceService.save(advice);
		return "redirect:/view_exercise/" + exerciseId;
	}

	@PostMapping(path = { "/delete_advice_exercise" })
	public String deleteAdviceExercise(Model model, @RequestParam Integer exerciseId, @RequestParam Integer adviceId) {
		exerciseAdviceService
				.delete(exerciseAdviceService.findByExerciseAdviceIdAndExerciseExerciseId(adviceId, exerciseId));
		return "redirect:/view_exercise/" + exerciseId;
	}
}
