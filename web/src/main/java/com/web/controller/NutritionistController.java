package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Food;
import com.web.model.FoodEaten;
import com.web.model.HelperPlan;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.FoodEatenService;
import com.web.service.FoodService;
import com.web.service.HelperFeedbackService;
import com.web.service.HelperPlanService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;
import com.web.utils.Gender;

@Controller
public class NutritionistController {

	private Logger logger = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private HelperPlanService helperPlanService;

	@Autowired
	private FoodService foodService;

	@Autowired
	private UserPlanService userPlanService;

	@Autowired
	private HelperFeedbackService helperFeedbackService;

	@Autowired
	private FoodEatenService foodEatenService;

	@Autowired
	private UserDeviceService userDeviceService;

	@GetMapping(path = { "/nutritionist" })
	public String nutritionist(Model model) {
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
		Set<Account> learners = accountService.getLearners(account);
		Set<FoodEaten> foodEatenToday = new HashSet<>();
		learners.forEach(
				element -> foodEatenToday.addAll(foodEatenService.findAllByUserAccountIdAndDateOfExecutionBetween(
						element.getAccountId(), timestampStartDate, timestampEndDate)));
		Set<FoodEaten> allFoods = new HashSet<>();
		learners.forEach(element -> allFoods.addAll(element.getFoodEaten()));
		Map<Food, Long> favoriteFoods = allFoods.stream().map(element -> element.getFood())
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		Map<Food, Long> favoriteFoodDesc = foodService.sortFoods(favoriteFoods, false);
		model.addAttribute("account", account);
		model.addAttribute("learners", learners);
		model.addAttribute("plansCreatedToday", plansCreatedToday);
		model.addAttribute("foodEatenToday", foodEatenToday);
		model.addAttribute("userPlansToday", userPlansToday);
		model.addAttribute("favoriteFoodDesc", favoriteFoodDesc);
		return "nutritionist/nutritionist";

	}

	@GetMapping(path = { "/diet_plans" })
	public String trainingPlans(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			model.addAttribute("dietPlans",
					helperPlanService.findAllDietPlansByHelperPlanNotAssociated(account.getAccountId()));
			return "nutritionist/diet_plans";
		} else {
			return "redirect:/nutritionist";
		}
	}

	@GetMapping(path = { "/purchased_diet_plans" })
	public String purchasedDietPlans(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Set<UserPlan> userDiets = userPlanService.findAllByHelperPlanTypeOfPlan("Dieta");
			userDiets.removeIf(
					element -> !element.getHelperPlan().getHelper().getAccountId().equals(account.getAccountId()));
			model.addAttribute("dietPlans", userDiets);
			return "nutritionist/purchased_diet_plans";
		} else {
			return "redirect:/nutritionist";
		}

	}

	@GetMapping(path = { "/create_diet_plan" })
	public String createDietPlan(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (!model.containsAttribute("dietPlan")) {
				model.addAttribute("dietPlan", new HelperPlan());
			}
			model.addAttribute("sex", Gender.values());
			return "nutritionist/create_diet_plan";
		} else {
			return "redirect:/nutritionist";
		}

	}

	@PostMapping(path = { "/create_diet_plan_save" })
	public String createDietPlanSave(Model model, @Valid @ModelAttribute("dietPlan") HelperPlan dietPlan,
			@RequestParam String typeOfPlan, BindingResult bindingResult, RedirectAttributes attr) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.dietPlan", bindingResult);
				attr.addFlashAttribute("dietPlan", dietPlan);
				return "redirect:/create_diet_plan";
			} else {
				dietPlan.setTypeOfPlan(typeOfPlan);
				dietPlan.setHelper(account);
				helperPlanService.save(dietPlan);
				return "redirect:/diet_plans";
			}
		} else {
			return "redirect:/nutritionist";
		}
	}

	@GetMapping(path = { "/edit_diet_plan/{id}" })
	public String editDietPlan(Model model, @PathVariable("id") String dietPlanId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (helperPlanService.checkPlan(account, dietPlanId, "Dieta")) {
				if (!model.containsAttribute("dietPlan")) {
					HelperPlan dietPlan = helperPlanService
							.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(dietPlanId), "Dieta").get();
					model.addAttribute("dietPlan", dietPlan);
				}
			} else {
				model.addAttribute("inexistentValue", true);
			}
			model.addAttribute("sex", Gender.values());
			return "nutritionist/edit_diet_plan";
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/edit_diet_plan_save" })
	public String editDietPlanSave(@Valid @ModelAttribute("dietPlan") HelperPlan dietPlan, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam Integer dietPlanId, @RequestParam String typeOfPlan) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.dietPlan", bindingResult);
				attr.addFlashAttribute("dietPlan", dietPlan);
				return "redirect:/edit_diet_plan/" + dietPlanId;
			} else {
				HelperPlan oldDietPlan = helperPlanService.findById(dietPlanId).get();
				oldDietPlan.setTypeOfPlan(typeOfPlan);
				oldDietPlan.setForWho(dietPlan.getForWho());
				oldDietPlan.setName(dietPlan.getName());
				oldDietPlan.setPrice(dietPlan.getPrice());
				oldDietPlan.setDescription(dietPlan.getDescription());
				oldDietPlan.setDateOfCreation(oldDietPlan.getDateOfCreation());
				oldDietPlan.setHelper(oldDietPlan.getHelper());
				helperPlanService.save(oldDietPlan);
				return "redirect:/diet_plans";
			}
		} else {
			return "redirect:/nutritionist";
		}
	}

	@GetMapping(path = { "/create_food_for_diet_plan/{id}" })
	public String createFoodForDietPlan(Model model, @PathVariable("id") String dietPlanId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (helperPlanService.checkPlan(account, dietPlanId, "Dieta")) {
				if (!model.containsAttribute("food")) {
					Food newFood = new Food();
					model.addAttribute("food", newFood);
				}
			} else {
				model.addAttribute("inexistentValue", true);
			}
			model.addAttribute("dietPlanId", dietPlanId);
			return "nutritionist/create_food_for_diet_plan";
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/create_food_for_diet_plan_save" })
	public String createFoodForDietPlanSave(Model model, @RequestParam Integer dietPlanId,
			@Valid @ModelAttribute("food") Food food, BindingResult bindingResult, RedirectAttributes attr) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.food", bindingResult);
				attr.addFlashAttribute("food", food);
				return "redirect:/create_food_for_diet_plan/" + dietPlanId;
			} else {
				food.setDietPlan(helperPlanService.findById(dietPlanId).get());
				foodService.save(food);
				return "redirect:/diet_plans";
			}
		} else {
			return "redirect:/nutritionist";
		}
	}

	@GetMapping(path = { "/view_progress_nutritionist/{id}" })
	public String viewProgressNutritionist(Model model, @PathVariable("id") String learnerId) {
		Account helper = accountService.getAccountConnected();
		if (helper.isActive()) {
			if (accountService.checkLearner(helper, learnerId)) {
				Account learner = accountService.findById(Integer.parseInt(learnerId)).get();
				Map<String, Integer> chartFoodEaten = new TreeMap<>();
				Map<String, Integer> chartAccumulatedCalories = new TreeMap<>();
				Set<FoodEaten> foodEatenForLearner = foodEatenService.findAllByUserAccountId(learner.getAccountId());
				String previousValue = null;
				Integer numberOfFoodEatenByDay = 0;
				Integer numberOfCaloriesPerFoodDay = 0;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				boolean noFeedbackWasProvided = true;
				Timestamp timestampStart = Timestamp.valueOf(LocalDate.now().atStartOfDay());
				Timestamp timestampEnd = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusDays(1).minusSeconds(1));
				for (FoodEaten foodEaten : foodEatenForLearner) {
					String startDate = foodEaten.getDateOfExecution().toLocalDateTime().toLocalDate().format(formatter);
					if (!startDate.equals(previousValue)) {
						numberOfFoodEatenByDay = 0;
						numberOfCaloriesPerFoodDay = 0;
						previousValue = startDate;
					}
					numberOfFoodEatenByDay++;
					numberOfCaloriesPerFoodDay += Math.round(foodEaten.getFood().getCalories());
					chartFoodEaten.put(startDate, numberOfFoodEatenByDay);
					chartAccumulatedCalories.put(startDate, numberOfCaloriesPerFoodDay);
				}
				if (helperFeedbackService.findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(
						learner.getAccountId(), timestampStart, timestampEnd).isPresent()) {
					noFeedbackWasProvided = false;
				}
				learner.getUserDevices().forEach(userDevice -> userDeviceService.getHeightAndWeight(model, userDevice));
				model.addAttribute("learner", learner);
				model.addAttribute("noFeedbackWasProvided", noFeedbackWasProvided);
				model.addAttribute("chartAccumulatedCalories", chartAccumulatedCalories);
				model.addAttribute("chartFoodEaten", chartFoodEaten);
				model.addAttribute("foodEatenForLearner", foodEatenForLearner);
			} else {
				model.addAttribute("inexistentValue", true);
			}
			return "nutritionist/view_progress_nutritionist";
		} else {
			return "redirect:/nutritionist";
		}

	}
}
