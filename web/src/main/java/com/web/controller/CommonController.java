package com.web.controller;

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
import com.web.model.ExerciseDone;
import com.web.model.Experience;
import com.web.model.Measurement;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.model.UserDevice;
import com.web.model.UserTraining;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;
import com.web.service.EducationService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.ExperienceService;
import com.web.service.MeasurementService;
import com.web.service.SkillService;
import com.web.service.TrainingPlanService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.UserTrainingService;
import com.web.service.XmlParserService;
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
	private UserDeviceService userDeviceService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private XmlParserService xmlParsersService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private TrainingPlanService trainingPlanService;

	@Autowired
	private UserTrainingService userTrainingService;

	@GetMapping(path = { "/view_profile" })
	public String viewProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Float height = 0f;
		Float weight = 0f;
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

	@PostMapping(path = { "/view_progress" })
	public String viewProgress(Model model, @RequestParam Integer learnerId) {
		Account learner = accountService.findById(learnerId).get();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account helper = accountService.findByUsername(auth.getName());
		Map<String, Integer> chartExercisesDone = new TreeMap<>();
		Map<String, Integer> chartBurnedCalories = new TreeMap<>();
		for (Role role : helper.getRoles()) {
			if (role.getName().equals("ROLE_TRAINER")) {
				Integer numberOfExercises = 0;
				Set<UserTraining> userTrainingPlans = userTrainingService
						.findAllByUserAccountId(learner.getAccountId());
				for (UserTraining userTraining : userTrainingPlans) {
					numberOfExercises += userTraining.getTrainingPlan().getExercises().size();
				}
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
				model.addAttribute("numberOfExercises", numberOfExercises);
				model.addAttribute("numberOfExercisesDone", numberOfExercisesDone);
				for (UserDevice userDevice : learner.getUserDevices()) {
					getHeightAndWeight(model, userDevice);
				}
				model.addAttribute("chartBurnedCalories", chartBurnedCalories);
				model.addAttribute("chartExercisesDone", chartExercisesDone);
			} else if (role.getName().equals("ROLE_NUTRITIONIST")) {

			}
		}
		model.addAttribute("learner", learner);
		return "common/view_progress";
	}

	private void getHeightAndWeight(Model model, UserDevice userDevice) {
		Float height;
		Float weight;
		if (userDevice.getDevice().getName().equals("Cantar Inteligent")) {
			Optional<Measurement> heightMeasurement = measurementService
					.findByName(ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERHEIGHT.getScaleTypeMeasurement());
			Optional<Measurement> weightMeasurement = measurementService
					.findAllByName(ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYMASS.getScaleTypeMeasurement())
					.stream().reduce((prev, next) -> next);
			if (heightMeasurement.isPresent() && weightMeasurement.isPresent()) {
				height = heightMeasurement.get().getValue();
				model.addAttribute("height", Math.round(height));
				weight = weightMeasurement.get().getValue();
				model.addAttribute("weight", Math.round(weight));
			}
		}
	}

}
