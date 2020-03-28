package com.web.controller;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.model.Account;
import com.web.model.Education;
import com.web.model.Exercise;
import com.web.model.Experience;
import com.web.model.Food;
import com.web.model.HelperFeedback;
import com.web.model.HelperPlan;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.ExerciseService;
import com.web.service.FoodService;
import com.web.service.HelperFeedbackService;
import com.web.service.UserDeviceService;
import com.web.utils.Product;
import com.web.utils.Qualifying;

@Controller
public class CommonController {

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private FoodService foodService;

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
			userDeviceService.getHeightAndWeight(model, userDevice);
		}
		model.addAttribute("account", account);
		return "common/view_profile";
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
		helperFeedback.setHelper(accountService.getAccountConnected());
		helperFeedback.setLearner(accountService.findById(learnerId).get());
		helperFeedback.setQualifying(qualifying);
		helperFeedback.setReason(reason);
		helperFeedbackService.save(helperFeedback);
		return "redirect:/view_learners";
	}

	@GetMapping(path = { "/transaction_history" })
	public String transcationHistory(Model model) {
		Account account = accountService.getAccountConnected();
		LOGGER.info(account.getUsername());
		Integer payments = account.getTransaction().getPayments();
		Integer availableBalance = account.getTransaction().getAvailableBalance();
		Set<Product> products = new HashSet<>();
		for (Role role : account.getRoles()) {
			if (role.getName().equals("ROLE_USER")) {
				for (UserDevice userDevice : account.getUserDevices()) {
					if (userDevice.isBought() && !(userDevice.getDevice().getName().equals("Fit Buddy"))) {
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

				for (UserPlan userPlan : account.getUserPlans()) {
					if (userPlan.isBought()) {
						Product product = new Product();
						product.setProductId(userPlan.getHelperPlan().getHelperPlanId());
						product.setProductName(userPlan.getHelperPlan().getName());
						product.setPrice(userPlan.getHelperPlan().getPrice());
						product.setForWho(userPlan.getHelperPlan().getForWho());
						product.setType(userPlan.getHelperPlan().getTypeOfPlan());
						product.setDateOfPurchased(userPlan.getDateOfPurchase());
						products.add(product);
					}
				}

			} else if (role.getName().equals("ROLE_TRAINER")) {
				for (HelperPlan trainingPlan : account.getPlans()) {
					for (UserPlan userTrainingPlan : trainingPlan.getUserPlans()) {
						if (userTrainingPlan.getHelperPlan().getTypeOfPlan().equals("Antrenament")
								&& userTrainingPlan.isBought()) {
							Product product = new Product();
							product.setProductId(userTrainingPlan.getHelperPlan().getHelperPlanId());
							product.setProductName(userTrainingPlan.getHelperPlan().getName());
							product.setPrice(userTrainingPlan.getHelperPlan().getPrice());
							product.setForWho(userTrainingPlan.getHelperPlan().getForWho());
							product.setType("trainingPlan");
							product.setDateOfPurchased(userTrainingPlan.getDateOfPurchase());
							products.add(product);
						}
					}
				}

			} else if (role.getName().equals("ROLE_NUTRITIONIST")) {
				for (HelperPlan trainingPlan : account.getPlans()) {
					for (UserPlan userDietPlan : trainingPlan.getUserPlans()) {
						if (userDietPlan.getHelperPlan().getTypeOfPlan().equals("Dieta") && userDietPlan.isBought()) {
							Product product = new Product();
							product.setProductId(userDietPlan.getHelperPlan().getHelperPlanId());
							product.setProductName(userDietPlan.getHelperPlan().getName());
							product.setPrice(userDietPlan.getHelperPlan().getPrice());
							product.setForWho(userDietPlan.getHelperPlan().getForWho());
							product.setType("dietPlan");
							product.setDateOfPurchased(userDietPlan.getDateOfPurchase());
							products.add(product);
						}
					}
				}
			}
		}
		model.addAttribute("account", account);
		model.addAttribute("products", products);
		model.addAttribute("payments", Math.abs(payments));
		model.addAttribute("availableBalance", availableBalance);
		return "common/transaction_history";
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
		if (trainingPlanId != null) {
			if (selectedRole.getName().equals("ROLE_USER")) {
				Set<Exercise> notPerformedExercises = exerciseService
						.findAllNotPerfomerdExercisesForTrainingPlanId(trainingPlanId);
				model.addAttribute("notPerformedExercises", notPerformedExercises);
			} else if (selectedRole.getName().equals("ROLE_TRAINER")) {
				Set<Exercise> exercises = exerciseService
						.findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(trainingPlanId, "Antrenament");
				model.addAttribute("exercises", exercises);
			}
		} else if (dietPlanId != null) {
			if (selectedRole.getName().equals("ROLE_USER")) {
				Set<Food> notEatenFoods = foodService.findAllNotEatenFoodsForDietPlanId(dietPlanId);
				model.addAttribute("notEatenFoods", notEatenFoods);
			} else if (selectedRole.getName().equals("ROLE_NUTRITIONIST")) {
				Set<Food> foods = foodService.findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(dietPlanId, "Dieta");
				model.addAttribute("foods", foods);
			}
		}
		model.addAttribute("account", account);
		model.addAttribute("trainingPlanId", trainingPlanId);
		model.addAttribute("dietPlanId", dietPlanId);
		return "common/plan_content";
	}
}
