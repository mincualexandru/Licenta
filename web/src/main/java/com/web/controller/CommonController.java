package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import com.web.service.HelperPlanService;
import com.web.service.RoleService;
import com.web.service.UserDeviceService;
import com.web.utils.Product;
import com.web.utils.Qualifying;

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
	private HelperFeedbackService helperFeedbackService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private HelperPlanService helperPlanService;

	@GetMapping(path = { "/success" })
	public String success(Model model, @ModelAttribute("message") String message) {
		model.addAttribute("message", message);
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

	@PostMapping(path = { "/helper_offers_feedback" })
	public String trainerOffersFeedback(Model model, @RequestParam Integer learnerId,
			RedirectAttributes redirectAttributes) {
		Account helper = accountService.getAccountConnected();
		boolean roleNutritionist = false;
		if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			roleNutritionist = true;
		}
		if (helper.isActive()) {
			LocalDate date = LocalDate.now();
			LocalDateTime startDateTime = date.atStartOfDay();
			LocalDateTime endDateTime = date.atStartOfDay().plusDays(1).minusSeconds(1);
			Timestamp timestampStartDate = Timestamp.valueOf(startDateTime);
			Timestamp timestampEndDate = Timestamp.valueOf(endDateTime);
			if (helperFeedbackService.findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(helper.getAccountId(),
					timestampStartDate, timestampEndDate).isPresent()) {
				redirectAttributes.addFlashAttribute("feedbackWasProvided", true);
				if (helper.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
					return "redirect:/view_progress/" + learnerId;
				} else if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
					return "redirect:/view_progress_nutritionist/" + learnerId;
				}

			}
			model.addAttribute("account", helper);
			model.addAttribute("learnerId", learnerId);
			model.addAttribute("qualifyings", Qualifying.values());
			return "common/helper_offers_feedback";
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/helper_offers_feedback_save" })
	public String trainerOffersFeedbackSave(Model model, @RequestParam Integer learnerId, @RequestParam String reason,
			@RequestParam Qualifying qualifying) {
		Account helper = accountService.getAccountConnected();
		boolean roleNutritionist = false;
		if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			roleNutritionist = true;
		}
		if (helper.isActive()) {
			HelperFeedback helperFeedback = new HelperFeedback();
			helperFeedback.setDateOfFeedbackProvied(new Timestamp(System.currentTimeMillis()));
			helperFeedback.setHelper(accountService.getAccountConnected());
			helperFeedback.setLearner(accountService.findById(learnerId).get());
			helperFeedback.setQualifying(qualifying);
			helperFeedback.setReason(reason);
			helperFeedbackService.save(helperFeedback);
			return "redirect:/view_learners";
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
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
			for (Role role : account.getRoles()) {
				if (role.getName().equals("ROLE_USER")) {
					for (UserDevice userDevice : account.getUserDevices()) {
						if (userDevice.isBought() && !(userDevice.getDevice().getName().equals("Fit Buddy"))) {
							Product product = new Product();
							product.setProductId(userDevice.getDevice().getDeviceId());
							product.setCompanyName(userDevice.getDevice().getCompany());
							product.setProductName(userDevice.getDevice().getName());
							product.setPrice(userDevice.getDevice().getPrice());
							product.setType("Dispozitiv");
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
							product.setDescription(userPlan.getHelperPlan().getDescription());
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
								product.setType("Antrenament");
								product.setDateOfPurchased(userTrainingPlan.getDateOfPurchase());
								products.add(product);
							}
						}
					}

				} else if (role.getName().equals("ROLE_NUTRITIONIST")) {
					for (HelperPlan trainingPlan : account.getPlans()) {
						for (UserPlan userDietPlan : trainingPlan.getUserPlans()) {
							if (userDietPlan.getHelperPlan().getTypeOfPlan().equals("Dieta")
									&& userDietPlan.isBought()) {
								Product product = new Product();
								product.setProductId(userDietPlan.getHelperPlan().getHelperPlanId());
								product.setProductName(userDietPlan.getHelperPlan().getName());
								product.setPrice(userDietPlan.getHelperPlan().getPrice());
								product.setForWho(userDietPlan.getHelperPlan().getForWho());
								product.setType("Dieta");
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
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else if (roleTrainer) {
			return "redirect:/trainer";
		} else {
			return "redirect:/home";
		}

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
			HelperPlan selectedTrainingPlan = helperPlanService.findById(trainingPlanId).get();
			if (selectedRole.getName().equals("ROLE_USER")) {
				Set<Exercise> notPerformedExercises = exerciseService.findAllByTrainingPlanHelperPlanId(trainingPlanId);
				String mostRepeatedMuscleGroup = Stream.of(notPerformedExercises)
						.collect(Collectors.groupingBy(w -> w, Collectors.counting())).entrySet().stream()
						.max(Comparator.comparing(Entry::getValue)).get().getKey().stream().findFirst().get()
						.getTrainedMuscleGroup().getMuscleGroup();
				model.addAttribute("notPerformedExercises", notPerformedExercises);
				model.addAttribute("mostRepeatedMuscleGroup", mostRepeatedMuscleGroup);
			} else if (selectedRole.getName().equals("ROLE_TRAINER")) {
				Set<Exercise> exercises = exerciseService
						.findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(trainingPlanId, "Antrenament");
				String mostRepeatedMuscleGroup = Stream.of(exercises)
						.collect(Collectors.groupingBy(w -> w, Collectors.counting())).entrySet().stream()
						.max(Comparator.comparing(Entry::getValue)).get().getKey().stream().findFirst().get()
						.getTrainedMuscleGroup().getMuscleGroup();
				model.addAttribute("exercises", exercises);
				model.addAttribute("mostRepeatedMuscleGroup", mostRepeatedMuscleGroup);
			}
			model.addAttribute("selectedTrainingPlan", selectedTrainingPlan);
		} else if (dietPlanId != null) {
			HelperPlan selectedDietPlan = helperPlanService.findById(dietPlanId).get();
			if (selectedRole.getName().equals("ROLE_USER")) {
				Set<Food> notEatenFoods = foodService.findAllByDietPlanHelperPlanId(dietPlanId);
				model.addAttribute("notEatenFoods", notEatenFoods);
			} else if (selectedRole.getName().equals("ROLE_NUTRITIONIST")) {
				Set<Food> foods = foodService.findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(dietPlanId, "Dieta");
				model.addAttribute("foods", foods);
			}
			model.addAttribute("selectedDietPlan", selectedDietPlan);
		}
		model.addAttribute("account", account);
		model.addAttribute("trainingPlanId", trainingPlanId);
		model.addAttribute("dietPlanId", dietPlanId);
		return "common/plan_content";
	}
}
