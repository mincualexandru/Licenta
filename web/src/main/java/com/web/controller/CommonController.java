package com.web.controller;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Education;
import com.web.model.Exercise;
import com.web.model.ExerciseAdvice;
import com.web.model.ExerciseImage;
import com.web.model.Experience;
import com.web.model.Food;
import com.web.model.FoodImage;
import com.web.model.FoodRecommendation;
import com.web.model.HelperPlan;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.model.UserDevice;
import com.web.service.AccountService;
import com.web.service.ExerciseService;
import com.web.service.FoodService;
import com.web.service.HelperPlanService;
import com.web.service.RoleService;
import com.web.service.UserDeviceService;
import com.web.utils.Product;

@Controller
public class CommonController {

	private Logger LOGGER = Logger.getLogger(CommonController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private FoodService foodService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private HelperPlanService helperPlanService;

	@GetMapping(path = { "/success" })
	public String success(Model model, @ModelAttribute("message") String message, HttpServletRequest request) {
		model.addAttribute("message", message);
		model.addAttribute("serverName", request.getServerName());
		return "common/success";
	}

	@GetMapping(path = { "/view_profile" })
	public String viewProfile(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (!model.containsAttribute("account")) {
				model.addAttribute("account", account);
			}
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
				userDeviceService.getHeightAndWeight(model, userDevice);
			}
			if (account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())
					|| account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
				List<Education> educations = account.getAccountInformation().getEducation().stream()
						.sorted((e1, e2) -> e2.getStart().compareTo(e1.getStart())).collect(Collectors.toList());
				List<Experience> experiences = account.getAccountInformation().getExperiences().stream()
						.sorted((e1, e2) -> e2.getStart().compareTo(e1.getStart())).collect(Collectors.toList());
				model.addAttribute("experiences", experiences);
				model.addAttribute("educations", educations);
			}
			model.addAttribute("account", account);
			return "common/view_profile";
		} else if (account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
			return "redirect:/trainer";
		} else if (account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/home";
		}

	}

	@PostMapping(path = { "/edit_account_information" })
	public String editAccountInformation(Model model, @Valid @ModelAttribute("account") Account account,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.account", bindingResult);
			redirectAttributes.addFlashAttribute("account", account);
			return "redirect:/view_profile";
		} else {
			Account accountConnected = accountService.getAccountConnected();
			accountConnected.setEmail(account.getEmail());
			accountConnected.setFirstName(account.getFirstName());
			accountConnected.setLastName(account.getLastName());
			accountConnected.setPhoneNumber(account.getPhoneNumber());
			accountService.save(accountConnected);
			return "redirect:/view_profile";
		}
	}

	@GetMapping(path = { "/transaction_history" })
	public String transcationHistory(Model model) {
		Account account = accountService.getAccountConnected();
		boolean roleNutritionist = false;
		boolean roleTrainer = false;
		boolean roleUser = false;
		if (account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			roleNutritionist = true;
		} else if (account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
			roleTrainer = true;
		} else if (account.getRoles().contains(roleService.findByName("ROLE_USER").get())) {
			roleUser = true;
		}
		if (account.isActive()) {
			Integer payments = account.getTransaction().getPayments();
			Integer availableBalance = account.getTransaction().getAvailableBalance();
			Set<Product> products = new HashSet<>();
			if (roleUser) {

				account.getUserDevices().forEach(userDevice -> {
					if (userDevice.isBought() && !(userDevice.getDevice().getName().equals("Fit Buddy"))) {
						products.add(new Product(userDevice.getDevice().getDeviceId(), userDevice.getDevice().getName(),
								userDevice.getDevice().getPrice(), userDevice.getDevice().getCompany(), null,
								"Dispozitiv", userDevice.getDateOfPurchase(), null));
					}
				});

				account.getUserPlans().forEach(userPlan -> {
					if (userPlan.isBought()) {
						products.add(new Product(userPlan.getHelperPlan().getHelperPlanId(),
								userPlan.getHelperPlan().getName(), userPlan.getHelperPlan().getPrice(), null,
								userPlan.getHelperPlan().getForWho(), userPlan.getHelperPlan().getTypeOfPlan(),
								userPlan.getDateOfPurchase(), userPlan.getHelperPlan().getDescription()));
					}
				});

			} else if (roleTrainer || roleNutritionist) {
				account.getPlans().forEach(helperPlan -> {
					helperPlan.getUserPlans().forEach(userPlan -> {
						String typeOfPlan = "";
						if (userPlan.isBought()) {
							typeOfPlan = userPlan.getHelperPlan().getTypeOfPlan().equals("Antrenament") ? "Antrenament"
									: "Dieta";
						}
						products.add(new Product(userPlan.getHelperPlan().getHelperPlanId(),
								userPlan.getHelperPlan().getName(), userPlan.getHelperPlan().getPrice(), null,
								userPlan.getHelperPlan().getForWho(), typeOfPlan, userPlan.getDateOfPurchase(), null));
					});
				});

			}
			model.addAttribute("account", account);
			model.addAttribute("products", products);
			model.addAttribute("payments", Math.abs(payments));
			model.addAttribute("availableBalance", availableBalance);
			return "common/transaction_history";
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else if (roleTrainer) {
			return "redirect:/trainer";
		} else {
			return "redirect:/home";
		}

	}

	@GetMapping(path = { "/view_exercise/{id}" })
	public String viewExercise(Model model, @PathVariable("id") String exerciseId) {
		Account account = accountService.getAccountConnected();
		if (exerciseService.checkExercise(account, exerciseId)) {
			Exercise exercise = exerciseService.findById(Integer.parseInt(exerciseId)).get();
			Set<ExerciseAdvice> exerciseAdvice = exercise.getExerciseAdvices();
			List<ExerciseAdvice> exerciseAdviceSorted = exerciseAdvice.stream()
					.sorted((e1, e2) -> e1.getAdvice().compareTo(e2.getAdvice())).collect(Collectors.toList());
			model.addAttribute("exercise", exercise);
			model.addAttribute("exerciseAdviceSorted", exerciseAdviceSorted);
			model.addAttribute("exerciseImage", new ExerciseImage());
			if (!model.containsAttribute("exerciseAdvice")) {
				model.addAttribute("exerciseAdvice", new ExerciseAdvice());
			}
		} else {
			model.addAttribute("inexistentValue", true);
		}
		model.addAttribute("account", account);
		return "common/view_exercise";

	}

	@GetMapping(path = { "/view_food/{id}" })
	public String viewFood(Model model, @PathVariable("id") String foodId) {
		Account user = accountService.getAccountConnected();
		if (foodService.checkFood(user, foodId)) {
			Food food = foodService.findById(Integer.parseInt(foodId)).get();
			Set<FoodRecommendation> foodRecommendation = food.getRecommendations();
			List<FoodRecommendation> foodRecommendationSorted = foodRecommendation.stream()
					.sorted((e1, e2) -> e1.getRecommendation().compareTo(e2.getRecommendation()))
					.collect(Collectors.toList());
			model.addAttribute("food", food);
			model.addAttribute("foodRecommendationSorted", foodRecommendationSorted);
			model.addAttribute("foodImage", new FoodImage());
			if (!model.containsAttribute("foodRecommendation")) {
				model.addAttribute("foodRecommendation", new FoodRecommendation());
			}
		} else {
			model.addAttribute("inexistentValue", true);
		}
		model.addAttribute("account", user);
		return "common/view_food";

	}

	@PostMapping(path = { "/plan_content" })
	public String createExerciseForTrainingPlanSave(Model model, @RequestParam(required = false) Integer trainingPlanId,
			@RequestParam(required = false) Integer dietPlanId) {
		Account account = accountService.getAccountConnected();
		Role selectedRole = new Role();
		for (Role role : account.getRoles()) {
			selectedRole = role;
			break;
		}
		Set<Exercise> exercises = new HashSet<>();
		Set<Food> foods = new HashSet<>();
		if (trainingPlanId != null) {
			HelperPlan selectedTrainingPlan = helperPlanService.findById(trainingPlanId).get();
			if (selectedRole.getName().equals("ROLE_USER")) {
				exercises = exerciseService.findAllByTrainingPlanHelperPlanId(trainingPlanId);
			} else if (selectedRole.getName().equals("ROLE_TRAINER")) {
				exercises = exerciseService.findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(trainingPlanId,
						"Antrenament");
			}
			String mostRepeatedMuscleGroup = Stream.of(exercises)
					.collect(Collectors.groupingBy(w -> w, Collectors.counting())).entrySet().stream()
					.max(Comparator.comparing(Entry::getValue)).get().getKey().stream().findFirst().get()
					.getTrainedMuscleGroup().getMuscleGroup();
			model.addAttribute("mostRepeatedMuscleGroup", mostRepeatedMuscleGroup);
			model.addAttribute("selectedTrainingPlan", selectedTrainingPlan);
			model.addAttribute("exercises", exercises);
		} else if (dietPlanId != null) {
			HelperPlan selectedDietPlan = helperPlanService.findById(dietPlanId).get();
			if (selectedRole.getName().equals("ROLE_USER")) {
				foods = foodService.findAllByDietPlanHelperPlanId(dietPlanId);
			} else if (selectedRole.getName().equals("ROLE_NUTRITIONIST")) {
				foods = foodService.findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(dietPlanId, "Dieta");

			}
			model.addAttribute("foods", foods);
			model.addAttribute("selectedDietPlan", selectedDietPlan);
		}
		model.addAttribute("account", account);
		model.addAttribute("trainingPlanId", trainingPlanId);
		model.addAttribute("dietPlanId", dietPlanId);
		return "common/plan_content";
	}
}
