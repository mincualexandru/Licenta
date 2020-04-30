package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.web.model.ExerciseDone;
import com.web.model.ExerciseImage;
import com.web.model.HelperPlan;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.ExerciseAdviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseImageService;
import com.web.service.ExerciseService;
import com.web.service.HelperFeedbackService;
import com.web.service.HelperPlanService;
import com.web.service.RoleService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;
import com.web.utils.ExerciseCategory;
import com.web.utils.Gender;
import com.web.utils.TrainedMuscleGroup;

@Controller
public class TrainerController {

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private HelperPlanService helperPlanService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseImageService exerciseImageService;

	@Autowired
	private ExerciseAdviceService exerciseAdviceService;

	@Autowired
	private UserPlanService userPlanService;

	@Autowired
	private HelperFeedbackService helperFeedbackService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private RoleService roleService;

	@GetMapping(path = { "/trainer" })
	public String trainer(Model model, RedirectAttributes redirectAttributes) {
		Account account = accountService.getAccountConnected();
		LocalDate date = LocalDate.now();
		LocalDateTime startDateTime = date.atStartOfDay();
		LocalDateTime endDateTime = date.atStartOfDay().plusDays(1).minusSeconds(1);
		Timestamp timestampStartDate = Timestamp.valueOf(startDateTime);
		Timestamp timestampEndDate = Timestamp.valueOf(endDateTime);
		Set<HelperPlan> plansCreatedToday = helperPlanService.findAllByHelperAccountIdAndDateOfCreationBetween(
				account.getAccountId(), timestampStartDate, timestampEndDate);
		Set<UserPlan> userPlansToday = userPlanService.findAllByHelperPlanHelperAccountIdAndDateOfPurchaseBetween(
				account.getAccountId(), timestampStartDate, timestampEndDate);
		Set<Integer> learnersIds = accountService.findAllLearnersByHelperId(account.getAccountId());
		Set<Account> learners = new HashSet<>();
		for (Integer integer : learnersIds) {
			Account learner = accountService.findById(integer).get();
			learners.add(learner);
		}
		Set<ExerciseDone> exercisesDoneToday = new HashSet<>();
		learners.forEach(element -> exercisesDoneToday
				.addAll(exerciseDoneService.findAllByUserAccountIdAndDateOfExecutionBetween(element.getAccountId(),
						timestampStartDate, timestampEndDate)));
		Set<ExerciseDone> allExercises = new HashSet<>();
		learners.forEach(element -> allExercises.addAll(element.getExerciseDone()));
		Map<Exercise, Long> favoriteExercises = allExercises.stream().map(element -> element.getExercise())
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		Map<Exercise, Long> favoriteExercisesDesc = sortExercises(favoriteExercises, false);
		model.addAttribute("account", account);
		model.addAttribute("learners", learners);
		model.addAttribute("plansCreatedToday", plansCreatedToday);
		model.addAttribute("exercisesDoneToday", exercisesDoneToday);
		model.addAttribute("userPlansToday", userPlansToday);
		model.addAttribute("favoriteExercisesDesc", favoriteExercisesDesc);
		return "trainer/trainer";
	}

	private static Map<Exercise, Long> sortExercises(Map<Exercise, Long> unsortMap, final boolean order) {
		List<Entry<Exercise, Long>> list = new LinkedList<>(unsortMap.entrySet());
		list.sort((o1, o2) -> order
				? o1.getValue().compareTo(o2.getValue()) == 0 ? o1.getKey().compareTo(o2.getKey())
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? o2.getKey().compareTo(o1.getKey())
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

	}

	@GetMapping(path = { "/view_progress/{id}" })
	public String viewProgress(Model model, @PathVariable("id") String learnerId) {
		Account helper = accountService.getAccountConnected();
		Map<String, Integer> chartExercisesDone = new TreeMap<>();
		Map<String, Integer> chartBurnedCalories = new TreeMap<>();
		Integer numberOfExercisesUnDone = 0;
		Integer numberOfExercisesDone = 0;
		Set<ExerciseDone> exercisesDoneForLearner = new HashSet<>();
		String previousValue = null;
		Integer numberOfExercisesDoneByDay = 0;
		Integer numberOfCaloriesPerExerciseDay = 0;
		boolean noFeedbackWasProvided = true;
		Timestamp timestampStart = Timestamp.valueOf(LocalDate.now().atStartOfDay());
		Timestamp timestampEnd = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusSeconds(1));
		Account learner = new Account();
		if (helper.isActive()) {
			if (checkId(learnerId) && accountService.findById(Integer.parseInt(learnerId)).isPresent()) {
				learner = accountService.findById(Integer.parseInt(learnerId)).get();
				exercisesDoneForLearner = exerciseDoneService.findAllByUserAccountId(learner.getAccountId());
				for (ExerciseDone exerciseDone : exercisesDoneForLearner) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime startDateTime = exerciseDone.getDateOfExecution().toLocalDateTime();
					LocalDate startDate = startDateTime.toLocalDate();
					String formatDate = startDate.format(formatter);
					String value = formatDate;
					if (!value.equals(previousValue)) {
						numberOfExercisesDoneByDay = 0;
						numberOfCaloriesPerExerciseDay = 0;
						previousValue = value;
					}
					numberOfExercisesDoneByDay++;
					numberOfCaloriesPerExerciseDay += exerciseDone.getExercise().getCaloriesBurned();
					chartExercisesDone.put(value, numberOfExercisesDoneByDay);
					chartBurnedCalories.put(value, numberOfCaloriesPerExerciseDay);
				}
				numberOfExercisesDone = exercisesDoneForLearner.size();

				if (helperFeedbackService.findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(
						learner.getAccountId(), timestampStart, timestampEnd).isPresent()) {
					noFeedbackWasProvided = false;
				}
				for (UserDevice userDevice : learner.getUserDevices()) {
					userDeviceService.getHeightAndWeight(model, userDevice);
				}
			} else {
				model.addAttribute("inexistentValue", true);
			}
			model.addAttribute("account", helper);
			model.addAttribute("learner", learner);
			model.addAttribute("noFeedbackWasProvided", noFeedbackWasProvided);
			model.addAttribute("numberOfExercisesUnDone", numberOfExercisesUnDone);
			model.addAttribute("numberOfExercisesDone", numberOfExercisesDone);
			model.addAttribute("chartBurnedCalories", chartBurnedCalories);
			model.addAttribute("chartExercisesDone", chartExercisesDone);
			model.addAttribute("exercisesDoneForLearner", exercisesDoneForLearner);
			return "trainer/view_progress";
		} else {
			return "redirect:/trainer";
		}

	}

	@GetMapping(path = { "/training_plans" })
	public String trainingPlans(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			model.addAttribute("trainingPlans",
					helperPlanService.findAllTrainingPlansByHelperPlanNotAssociated(account.getAccountId()));
			model.addAttribute("user", account);
			return "trainer/training_plans";
		} else {
			return "redirect:/trainer";
		}

	}

	@GetMapping(path = { "/purchased_training_plans" })
	public String purchasedTrainingPlans(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Set<UserPlan> userTrainings = userPlanService.findAllByHelperPlanTypeOfPlan("Antrenament");
			userTrainings.removeIf(
					element -> !element.getHelperPlan().getHelper().getAccountId().equals(account.getAccountId()));
			model.addAttribute("trainingPlans", userTrainings);
			model.addAttribute("account", account);
			return "trainer/purchased_training_plans";
		} else {
			return "redirect:/trainer";
		}

	}

	@GetMapping(path = { "/create_training_plan" })
	public String createTrainingPlan(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (!model.containsAttribute("trainingPlan")) {
				model.addAttribute("trainingPlan", new HelperPlan());
			}
			model.addAttribute("sex", Gender.values());
			return "trainer/create_training_plan";
		} else {
			return "redirect:/trainer";
		}

	}

	@PostMapping(path = { "/create_training_plan_save" })
	public String createTrainingPlanSave(Model model, @Valid @ModelAttribute("trainingPlan") HelperPlan trainingPlan,
			@RequestParam String typeOfPlan, BindingResult bindingResult, RedirectAttributes attr) {
		Account trainer = accountService.getAccountConnected();
		if (trainer.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.trainingPlan", bindingResult);
				attr.addFlashAttribute("trainingPlan", trainingPlan);
				return "redirect:/create_training_plan";
			} else {
				trainingPlan.setTypeOfPlan(typeOfPlan);
				trainingPlan.setHelper(trainer);
				helperPlanService.save(trainingPlan);
				return "redirect:/training_plans";
			}
		} else {
			return "redirect:/trainer";
		}

	}

	@GetMapping(path = { "/edit_training_plan/{id}" })
	public String editTrainingPlan(Model model, @PathVariable("id") String trainingPlanId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (checkId(trainingPlanId) && helperPlanService
					.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(trainingPlanId), "Antrenament").isPresent()) {
				if (!model.containsAttribute("trainingPlan")) {
					HelperPlan trainingPlan = helperPlanService
							.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(trainingPlanId), "Antrenament").get();
					model.addAttribute("trainingPlan", trainingPlan);
				}
			} else {
				model.addAttribute("inexistentValue", true);
			}
			model.addAttribute("sex", Gender.values());
			return "trainer/edit_training_plan";
		} else {
			return "redirect:/trainer";
		}

	}

	@PostMapping(path = { "/edit_training_plan_save" })
	public String editTrainingPlanSave(@Valid @ModelAttribute("trainingPlan") HelperPlan trainingPlan,
			BindingResult bindingResult, RedirectAttributes attr, Model model, @RequestParam Integer trainingPlanId,
			@RequestParam String typeOfPlan) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				bindingResult.getAllErrors().forEach(element -> System.out.println(element));
				attr.addFlashAttribute("org.springframework.validation.BindingResult.trainingPlan", bindingResult);
				attr.addFlashAttribute("trainingPlan", trainingPlan);
				return "redirect:/edit_training_plan/" + trainingPlanId;
			} else {
				HelperPlan oldTrainingPlan = helperPlanService.findById(trainingPlanId).get();
				oldTrainingPlan.setTypeOfPlan(typeOfPlan);
				oldTrainingPlan.setForWho(trainingPlan.getForWho());
				oldTrainingPlan.setName(trainingPlan.getName());
				oldTrainingPlan.setPrice(trainingPlan.getPrice());
				oldTrainingPlan.setDescription(trainingPlan.getDescription());
				oldTrainingPlan.setDateOfCreation(oldTrainingPlan.getDateOfCreation());
				oldTrainingPlan.setHelper(oldTrainingPlan.getHelper());
				helperPlanService.save(oldTrainingPlan);
				return "redirect:/training_plans";
			}
		} else {
			return "redirect:/trainer";
		}
	}

	@GetMapping(path = { "/create_exercise_for_training_plan/{id}" })
	public String createExerciseForTrainingPlan(Model model, @PathVariable("id") String trainingPlanId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (checkId(trainingPlanId) && helperPlanService
					.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(trainingPlanId), "Antrenament").isPresent()) {
				if (!model.containsAttribute("exercise")) {
					Exercise newExercise = new Exercise();
					model.addAttribute("exercise", newExercise);
				}
			} else {
				model.addAttribute("inexistentValue", true);
			}
			model.addAttribute("muscleGroups", TrainedMuscleGroup.values());
			model.addAttribute("exerciseCategories", ExerciseCategory.values());
			model.addAttribute("trainingPlanId", trainingPlanId);
			return "trainer/create_exercise_for_training_plan";
		} else {
			return "redirect:/trainer";
		}

	}

	@PostMapping(path = { "/create_exercise_for_training_plan_save" })
	public String createExerciseForTrainingPlanSave(Model model, @RequestParam Integer trainingPlanId,
			@Valid @ModelAttribute("exercise") Exercise exercise, BindingResult bindingResult,
			RedirectAttributes attr) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.exercise", bindingResult);
				attr.addFlashAttribute("exercise", exercise);
				return "redirect:/create_exercise_for_training_plan/" + trainingPlanId;
			} else {
				exercise.setTrainingPlan(helperPlanService.findById(trainingPlanId).get());
				exerciseService.save(exercise);
				return "redirect:/training_plans";
			}
		} else {
			return "redirect:/trainer";
		}
	}

	@GetMapping(path = { "/view_exercise/{id}" })
	public String viewExercise(Model model, @PathVariable("id") String exerciseId) {
		Account account = accountService.getAccountConnected();
		boolean roleTrainer = false;
		if (account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
			roleTrainer = true;
		}
		if (checkId(exerciseId) && exerciseService.findById(Integer.parseInt(exerciseId)).isPresent()) {
			Exercise exercise = exerciseService.findById(Integer.parseInt(exerciseId)).get();
			Set<ExerciseAdvice> exerciseAdvice = exercise.getExerciseAdvices();
			List<ExerciseAdvice> exerciseAdviceSorted = exerciseAdvice.stream()
					.sorted((e1, e2) -> e1.getAdvice().compareTo(e2.getAdvice())).collect(Collectors.toList());
			model.addAttribute("account", account);
			model.addAttribute("exercise", exercise);
			model.addAttribute("exerciseAdviceSorted", exerciseAdviceSorted);
			model.addAttribute("exerciseImage", new ExerciseImage());
			model.addAttribute("exerciseAdvice", new ExerciseAdvice());
		} else {
			model.addAttribute("inexistentValue", true);
		}
		return "common/view_exercise";

	}

	@PostMapping(path = { "/add_photo_for_exercise_save" })
	public String addPhotoForExerciseSave(Model model, @RequestParam Integer exerciseId,
			@RequestParam("imageFile") MultipartFile imageFile,
			@ModelAttribute("exerciseImage") ExerciseImage exerciseImage) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			HelperPlan helperPlan = exerciseService.findById(exerciseId).get().getTrainingPlan();
			exerciseImage.setFileName(imageFile.getOriginalFilename());
			Exercise exercise = exerciseService.findById(exerciseId).get();
			exerciseImage.setExercise(exercise);
			try {
				exerciseImageService.saveImage(imageFile, exerciseImage, helperPlan);
				if (helperPlan.getForWho().getGender().equals("Barbat")) {
					exerciseImage.setPath("/images/plans/trainings/man/exercise/");
				} else if (helperPlan.getForWho().getGender().equals("Femeie")) {
					exerciseImage.setPath("/images/plans/trainings/woman/exercise/");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
			exerciseImageService.save(exerciseImage);
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/add_advice_for_exercise_save" })
	public String addAdviceForExerciseSave(Model model, @ModelAttribute("exerciseAdvice") ExerciseAdvice exerciseAdvice,
			@RequestParam Integer exerciseId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			exerciseAdvice.setExercise(exerciseService.findById(exerciseId).get());
			exerciseAdviceService.save(exerciseAdvice);
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/delete_photo_from_exercise" })
	public String deletePhotoFromExercise(Model model, @RequestParam Integer exerciseId,
			@RequestParam Integer photoId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			exerciseImageService.deleteByExerciseImagesIdAndExerciseExerciseId(photoId, exerciseId);
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/edit_advice_exercise" })
	public String editAdviceExercise(Model model, @RequestParam Integer exerciseId, @RequestParam Integer adviceId,
			@RequestParam String editName) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			ExerciseAdvice advice = exerciseAdviceService.findByExerciseAdviceIdAndExerciseExerciseId(adviceId,
					exerciseId);
			advice.setAdvice(editName);
			exerciseAdviceService.save(advice);
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/delete_advice_exercise" })
	public String deleteAdviceExercise(Model model, @RequestParam Integer exerciseId, @RequestParam Integer adviceId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			exerciseAdviceService
					.delete(exerciseAdviceService.findByExerciseAdviceIdAndExerciseExerciseId(adviceId, exerciseId));
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	private boolean checkId(String userDeviceId) {
		try {
			int num = Integer.parseInt(userDeviceId);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
