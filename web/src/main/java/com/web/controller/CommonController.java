package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.AccountInformation;
import com.web.model.Education;
import com.web.model.Exercise;
import com.web.model.ExerciseDone;
import com.web.model.ExerciseFeedback;
import com.web.model.Experience;
import com.web.model.HelperFeedback;
import com.web.model.Measurement;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.model.TrainingPlan;
import com.web.model.UserDevice;
import com.web.model.UserTraining;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;
import com.web.service.EducationService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.ExperienceService;
import com.web.service.HelperFeedbackService;
import com.web.service.MeasurementService;
import com.web.service.SkillService;
import com.web.service.UserTrainingService;
import com.web.utils.Product;
import com.web.utils.Qualifying;
import com.web.utils.ScaleTypeMeasurement;

@Controller
public class CommonController {

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private ExperienceService experienceService;

	@Autowired
	private EducationService educationService;

	@Autowired
	private SkillService skillService;

	@Autowired
	private AccountInformationService accountInformationService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private UserTrainingService userTrainingService;

	@Autowired
	private HelperFeedbackService helperFeedbackService;

	@GetMapping(path = { "/view_profile" })
	public String viewProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		if (!model.containsAttribute("skill")) {
			model.addAttribute("skill", new Skill());
		}
		if (!model.containsAttribute("education")) {
			model.addAttribute("education", new Education());
		}
		if (!model.containsAttribute("experience")) {
			model.addAttribute("experience", new Experience());
		}
		for (UserDevice userDevice : account.getUserDevices()) {
			getHeightAndWeight(model, userDevice);
		}
		model.addAttribute("account", account);
		return "common/view_profile";
	}

	@PostMapping(path = { "/update_about_me" })
	public String updateAboutMe(Model model, @RequestParam Integer accountInformationId,
			@RequestParam String descriptionAccountInformation) {
		AccountInformation accountInformation = accountInformationService.findById(accountInformationId).get();
		accountInformation.setDescription(descriptionAccountInformation);
		accountInformationService.save(accountInformation);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/update_experience" })
	public String updateExperience(Model model, @RequestParam Integer experienceId, @RequestParam String expEditName,
			@RequestParam String expEditCity, @RequestParam String editStart, @RequestParam String editEnd) {
		Experience experience = experienceService.findById(experienceId).get();
		experience.setName(expEditName);
		experience.setCity(expEditCity);
		experience.setStart(editStart);
		experience.setEnd(editEnd);
		experienceService.save(experience);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/update_education" })
	public String updateEducation(Model model, @RequestParam Integer educationId, @RequestParam String editName,
			@RequestParam String editCity, @RequestParam String editStart, @RequestParam String editEnd) {
		Education education = educationService.findById(educationId).get();
		education.setName(editName);
		education.setCity(editCity);
		education.setStart(editStart);
		education.setEnd(editEnd);
		educationService.save(education);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/update_skill" })
	public String updateSkill(Model model, @RequestParam Integer skillId, @RequestParam String description2) {
		Skill skill = skillService.findById(skillId).get();
		skill.setDescription(description2);
		skillService.save(skill);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/delete_experience" })
	public String deleteExperience(Model model, @RequestParam Integer experienceId) {
		Experience experience = experienceService.findById(experienceId).get();
		experienceService.delete(experience);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/delete_education" })
	public String deleteEducation(Model model, @RequestParam Integer educationId) {
		Education education = educationService.findById(educationId).get();
		educationService.delete(education);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/delete_skill" })
	public String deleteSkill(Model model, @RequestParam Integer skillId) {
		Skill skill = skillService.findById(skillId).get();
		skillService.delete(skill);
		return "redirect:/view_profile";
	}

	@PostMapping(path = { "/add_skill" })
	public String addSkill(@Valid @ModelAttribute("skill") Skill skill, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam Integer accountInformationId) {

		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.skill", bindingResult);
			attr.addFlashAttribute("skill", skill);
			return "redirect:/view_profile";
		} else {
			skill.setAccountInformation(accountInformationService.findById(accountInformationId).get());
			skillService.save(skill);
			return "redirect:/view_profile";
		}
	}

	@PostMapping(path = { "/add_education" })
	public String addEducation(@Valid @ModelAttribute("education") Education education, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam Integer accountInformationId) {

		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.education", bindingResult);
			attr.addFlashAttribute("education", education);
			return "redirect:/view_profile";
		} else {
			education.setAccountInformation(accountInformationService.findById(accountInformationId).get());
			educationService.save(education);
			return "redirect:/view_profile";
		}
	}

	@PostMapping(path = { "/add_experience" })
	public String addExperience(@Valid @ModelAttribute("experience") Experience experience, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam Integer accountInformationId) {

		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.experience", bindingResult);
			attr.addFlashAttribute("experience", experience);
			return "redirect:/view_profile";
		} else {
			experience.setAccountInformation(accountInformationService.findById(accountInformationId).get());
			experienceService.save(experience);
			return "redirect:/view_profile";
		}
	}

	@GetMapping(path = { "/view_learners" })
	public String viewLearners(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<Integer> learnersIds = accountService.findAllLearnersByHelperId(account.getAccountId());
		Set<Account> learners = new HashSet<>();
		for (Integer integer : learnersIds) {
			Account learner = accountService.findById(integer).get();
			learners.add(learner);
		}
		model.addAttribute("account", account);
		model.addAttribute("learners", learners);
		return "common/view_learners";
	}

	@GetMapping(path = { "/view_feedbacks" })
	public String viewFeedbacks(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<Integer> learnersIds = accountService.findAllLearnersByHelperId(account.getAccountId());
		Set<ExerciseFeedback> exerciseFeedbacks = new HashSet<>();
		for (Integer integer : learnersIds) {
			exerciseFeedbacks.addAll(exerciseFeedbackService.findAllByUserAccountId(integer));
		}
		model.addAttribute("exerciseFeedbacks", exerciseFeedbacks);
		return "common/view_feedbacks";
	}

	@PostMapping(path = { "/view_progress" })
	public String viewProgress(Model model, @RequestParam Integer learnerId) {
		Account learner = accountService.findById(learnerId).get();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account helper = accountService.findByUsername(auth.getName());
		Map<String, Integer> chartExercisesDone = new TreeMap<>();
		Map<String, Integer> chartBurnedCalories = new TreeMap<>();
		for (Role role : helper.getRoles()) {
			if (role.getName().equals("ROLE_TRAINER")) {
				Integer numberOfExercisesUnDone = 0;
				Set<Exercise> totalExercises = exerciseService.findAllNotPerfomerdExercises();
				numberOfExercisesUnDone = totalExercises.size();
				Integer numberOfExercisesDone = 0;
				Set<ExerciseDone> exercisesDoneForLearner = exerciseDoneService
						.findAllByUserAccountId(learner.getAccountId());
				String previousValue = null;
				Integer numberOfExercisesDoneByDay = 0;
				Integer numberOfCaloriesPerExerciseDay = 0;
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

				model.addAttribute("numberOfExercisesUnDone", numberOfExercisesUnDone);
				model.addAttribute("numberOfExercisesDone", numberOfExercisesDone);
				model.addAttribute("chartBurnedCalories", chartBurnedCalories);
				model.addAttribute("chartExercisesDone", chartExercisesDone);
				model.addAttribute("exercisesDoneForLearner", exercisesDoneForLearner);
				model.addAttribute("totalExercises", totalExercises);
			} else if (role.getName().equals("ROLE_NUTRITIONIST")) {

			}
		}
		boolean noFeedbackWasProvided = true;
		if (helperFeedbackService.findByLearnerAccountId(learner.getAccountId()).isPresent()) {
			noFeedbackWasProvided = false;
		}
		for (UserDevice userDevice : learner.getUserDevices()) {
			getHeightAndWeight(model, userDevice);
		}
		model.addAttribute("learner", learner);
		model.addAttribute("noFeedbackWasProvided", noFeedbackWasProvided);
		return "common/view_progress";
	}

	private void getHeightAndWeight(Model model, UserDevice userDevice) {
		Float height;
		Float weight;
		if (userDevice.getDevice().getName().equals("Cantar Inteligent")) {
			Optional<Measurement> heightMeasurement = measurementService.findByNameAndUserDeviceUserDeviceId(
					ScaleTypeMeasurement.HEIGHT.getScaleTypeMeasurement(), userDevice.getUserDeviceId());
			Optional<Measurement> weightMeasurement = measurementService
					.findAllByNameAndUserDeviceUserDeviceId(ScaleTypeMeasurement.MASS.getScaleTypeMeasurement(),
							userDevice.getUserDeviceId())
					.stream().reduce((prev, next) -> next);
			if (heightMeasurement.isPresent() && weightMeasurement.isPresent()) {
				height = heightMeasurement.get().getValue();
				model.addAttribute("height", Math.round(height));
				weight = weightMeasurement.get().getValue();
				model.addAttribute("weight", Math.round(weight));
			}
		}
	}

	private Account getAccountConnected() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		return user;
	}

	@PostMapping(path = { "/helper_offers_feedback" })
	public String trainerOffersFeedback(Model model, @RequestParam Integer learnerId) {
		model.addAttribute("learnerId", learnerId);
		model.addAttribute("qualifyings", Qualifying.values());
		return "common/helper_offers_feedback";
	}

	@PostMapping(path = { "/helper_offers_feedback_save" })
	public String trainerOffersFeedbackSave(Model model, @RequestParam Integer learnerId, @RequestParam String reason,
			@RequestParam Qualifying qualifying) {
		HelperFeedback helperFeedback = new HelperFeedback();
		helperFeedback.setDateOfFeedbackProvied(new Timestamp(System.currentTimeMillis()));
		helperFeedback.setHelper(getAccountConnected());
		helperFeedback.setLearner(accountService.findById(learnerId).get());
		helperFeedback.setQualifying(qualifying);
		helperFeedback.setReason(reason);
		helperFeedbackService.save(helperFeedback);
		return "redirect:/view_learners";
	}

	@GetMapping(path = { "/transaction_history" })
	public String transcationHistory(Model model) {
		Account account = getAccountConnected();
		System.out.println(account.getUsername());
		Integer payments = account.getTransaction().getPayments();
		Integer availableBalance = account.getTransaction().getAvailableBalance();
		Set<Product> products = new HashSet<>();
		for (Role role : account.getRoles()) {
			if (role.getName().equals("ROLE_USER")) {
				for (UserDevice userDevice : account.getUserDevices()) {
					if (userDevice.isBought()) {
						Product product = new Product();
						product.setProductId(userDevice.getDevice().getDeviceId());
						product.setCompanyName(userDevice.getDevice().getCompany());
						product.setProductName(userDevice.getDevice().getName());
						product.setPrice(userDevice.getDevice().getPrice());
						product.setType("device");
						product.setDateOfPurchased(userDevice.getDateOfPurchase());
						products.add(product);
					}
				}

				for (UserTraining userTrainingPlan : account.getUserTrainingPlans()) {
					if (userTrainingPlan.isBought()) {
						Product product = new Product();
						product.setProductId(userTrainingPlan.getTrainingPlan().getTrainingPlanId());
						product.setProductName(userTrainingPlan.getTrainingPlan().getName());
						product.setPrice(userTrainingPlan.getTrainingPlan().getPrice());
						product.setForWho(userTrainingPlan.getTrainingPlan().getForWho());
						product.setType("trainingPlan");
						product.setDateOfPurchased(userTrainingPlan.getDateOfPurchase());
						products.add(product);
					}
				}

			} else if (role.getName().equals("ROLE_TRAINER")) {
				for (TrainingPlan trainingPlan : account.getTrainingPlans()) {
					for (UserTraining userTrainingPlan : trainingPlan.getUserTrainingPlans()) {
						if (userTrainingPlan.isBought()) {
							Product product = new Product();
							product.setProductId(userTrainingPlan.getTrainingPlan().getTrainingPlanId());
							product.setProductName(userTrainingPlan.getTrainingPlan().getName());
							product.setPrice(userTrainingPlan.getTrainingPlan().getPrice());
							product.setForWho(userTrainingPlan.getTrainingPlan().getForWho());
							product.setType("trainingPlan");
							product.setDateOfPurchased(userTrainingPlan.getDateOfPurchase());
							products.add(product);
						}
					}
				}

			} else if (role.getName().equals("ROLE_NUTRITIONIST")) {

			}
		}
		model.addAttribute("account", account);
		model.addAttribute("products", products);
		model.addAttribute("payments", Math.abs(payments));
		model.addAttribute("availableBalance", availableBalance);
		return "common/transaction_history";
	}

	@PostMapping(path = { "/exercises_training_plan" })
	public String createExerciseForTrainingPlanSave(Model model, @RequestParam Integer trainingPlanId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		for (Role role : account.getRoles()) {
			if (role.getName().equals("ROLE_USER")) {
				Set<Exercise> notPerformedExercises = exerciseService
						.findAllNotPerfomerdExercisesForTrainingPlanId(trainingPlanId);
				model.addAttribute("notPerformedExercises", notPerformedExercises);
			} else if (role.getName().equals("ROLE_TRAINER")) {
				Set<Exercise> exercises = exerciseService.findAllByTrainingPlanTrainingPlanId(trainingPlanId);
				model.addAttribute("exercises", exercises);
			}
		}
		model.addAttribute("account", account);
		return "common/exercises_training_plan";
	}
}
