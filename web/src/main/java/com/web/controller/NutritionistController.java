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
import com.web.model.Food;
import com.web.model.FoodImage;
import com.web.model.FoodRecommendation;
import com.web.model.HelperPlan;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.FoodImageService;
import com.web.service.FoodRecommendationService;
import com.web.service.FoodService;
import com.web.service.HelperPlanService;
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
	private FoodImageService foodImageService;

	@Autowired
	private FoodRecommendationService foodRecommendationService;

	@Autowired
	private UserPlanService userPlanService;

	@GetMapping(path = { "/nutritionist" })
	public String nutritionist(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		return "nutritionist/nutritionist";
	}

	@GetMapping(path = { "/diet_plans" })
	public String trainingPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("dietPlans",
				helperPlanService.findAllDietPlansByHelperPlanNotAssociated(account.getAccountId()));
		return "nutritionist/diet_plans";
	}

	@GetMapping(path = { "/purchased_diet_plans" })
	public String purchasedDietPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<UserPlan> userDiets = userPlanService.findAllByHelperPlanTypeOfPlan("Dieta");
		userDiets.removeIf(
				element -> !element.getHelperPlan().getHelper().getAccountId().equals(account.getAccountId()));
		model.addAttribute("dietPlans", userDiets);
		return "nutritionist/purchased_diet_plans";
	}

	@GetMapping(path = { "/create_diet_plan" })
	public String createDietPlan(Model model) {
		if (!model.containsAttribute("dietPlan")) {
			model.addAttribute("dietPlan", new HelperPlan());
		}
		model.addAttribute("sex", Gender.values());
		return "nutritionist/create_diet_plan";
	}

	@PostMapping(path = { "/create_diet_plan_save" })
	public String createDietPlanSave(Model model, @Valid @ModelAttribute("dietPlan") HelperPlan dietPlan,
			BindingResult bindingResult, RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.dietPlan", bindingResult);
			attr.addFlashAttribute("dietPlan", dietPlan);
			return "redirect:/create_diet_plan";
		} else {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Account trainer = accountService.findByUsername(auth.getName());
			dietPlan.setHelper(trainer);
			dietPlan.setTypeOfPlan("Dieta");
			helperPlanService.save(dietPlan);
			return "redirect:/diet_plans";
		}
	}

	@GetMapping(path = { "/edit_diet_plan/{id}" })
	public String editDietPlan(Model model, @PathVariable("id") String dietPlanId) {

		if (checkId(dietPlanId) && helperPlanService
				.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(dietPlanId), "Dieta").isPresent()) {
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
	}

	@PostMapping(path = { "/edit_diet_plan_save" })
	public String editDietPlanSave(@Valid @ModelAttribute("dietPlan") HelperPlan dietPlan, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam Integer dietPlanId) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.dietPlan", bindingResult);
			attr.addFlashAttribute("dietPlan", dietPlan);
			return "redirect:/edit_diet_plan/" + dietPlanId;
		} else {
			HelperPlan oldDietPlan = helperPlanService.findById(dietPlanId).get();
			oldDietPlan.setForWho(dietPlan.getForWho());
			oldDietPlan.setName(dietPlan.getName());
			oldDietPlan.setPrice(dietPlan.getPrice());
			oldDietPlan.setDateOfCreation(oldDietPlan.getDateOfCreation());
			oldDietPlan.setHelper(oldDietPlan.getHelper());
			helperPlanService.save(oldDietPlan);
			return "redirect:/diet_plans";
		}
	}

	@GetMapping(path = { "/create_food_for_diet_plan/{id}" })
	public String createFoodForDietPlan(Model model, @PathVariable("id") String dietPlanId) {
		if (checkId(dietPlanId) && helperPlanService
				.findByHelperPlanIdAndTypeOfPlan(Integer.parseInt(dietPlanId), "Dieta").isPresent()) {
			if (!model.containsAttribute("food")) {
				Food newFood = new Food();
				model.addAttribute("food", newFood);
			}
		} else {
			model.addAttribute("inexistentValue", true);
		}
		model.addAttribute("dietPlanId", dietPlanId);
		return "nutritionist/create_food_for_diet_plan";
	}

	@PostMapping(path = { "/create_food_for_diet_plan_save" })
	public String createFoodForDietPlanSave(Model model, @RequestParam Integer dietPlanId,
			@Valid @ModelAttribute("food") Food food, BindingResult bindingResult, RedirectAttributes attr) {
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.food", bindingResult);
			attr.addFlashAttribute("food", food);
			return "redirect:/create_food_for_diet_plan/" + dietPlanId;
		} else {
			food.setDietPlan(helperPlanService.findById(dietPlanId).get());
			foodService.save(food);
			return "redirect:/diet_plans";
		}
	}

	@GetMapping(path = { "/view_food/{id}" })
	public String viewExercise(Model model, @PathVariable("id") String foodId) {
		if (checkId(foodId) && foodService.findById(Integer.parseInt(foodId)).isPresent()) {
			Food food = foodService.findById(Integer.parseInt(foodId)).get();
			Set<FoodRecommendation> foodRecommendation = food.getRecommendations();
			List<FoodRecommendation> foodRecommendationSorted = foodRecommendation.stream()
					.sorted((e1, e2) -> e1.getRecommendation().compareTo(e2.getRecommendation()))
					.collect(Collectors.toList());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Account account = accountService.findByUsername(auth.getName());
			model.addAttribute("account", account);
			model.addAttribute("food", food);
			model.addAttribute("foodRecommendationSorted", foodRecommendationSorted);
			model.addAttribute("foodImage", new FoodImage());
			model.addAttribute("foodRecommendation", new FoodRecommendation());
		} else {
			model.addAttribute("inexistentValue", true);
		}
		return "common/view_food";
	}

	@PostMapping(path = { "/add_photo_for_food_save" })
	public String addPhotoForExerciseSave(Model model, @RequestParam Integer foodId,
			@RequestParam("imageFile") MultipartFile imageFile, @ModelAttribute("foodImage") FoodImage foodImage) {
		foodImage.setFileName(imageFile.getOriginalFilename());
		Food food = foodService.findById(foodId).get();
		foodImage.setFood(food);
		try {
			foodImageService.saveImage(imageFile, foodImage);
			foodImage.setPath("/images/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		foodImageService.save(foodImage);
		return "redirect:/view_food/" + foodId;
	}

	@PostMapping(path = { "/add_recommendation_for_food_save" })
	public String addAdviceForExerciseSave(Model model,
			@ModelAttribute("foodRecommendation") FoodRecommendation foodRecommendation, @RequestParam Integer foodId) {
		foodRecommendation.setFood(foodService.findById(foodId).get());
		foodRecommendationService.save(foodRecommendation);
		return "redirect:/view_food/" + foodId;
	}

	@PostMapping(path = { "/delete_photo_from_food" })
	public String deletePhotoFromExercise(Model model, @RequestParam Integer foodId, @RequestParam Integer photoId) {
		foodImageService.deleteByFoodImageIdAndFoodFoodId(photoId, foodId);
		return "redirect:/view_food/" + foodId;
	}

	@PostMapping(path = { "/edit_recommendation_food" })
	public String editAdviceExercise(Model model, @RequestParam Integer foodId, @RequestParam Integer recommendationId,
			@RequestParam String editName) {
		FoodRecommendation advice = foodRecommendationService.findByFoodRecommendationIdAndFoodFoodId(recommendationId,
				foodId);
		advice.setRecommendation(editName);
		foodRecommendationService.save(advice);
		return "redirect:/view_food/" + foodId;
	}

	@PostMapping(path = { "/delete_recommendation_food" })
	public String deleteAdviceExercise(Model model, @RequestParam Integer foodId,
			@RequestParam Integer recommendationId) {
		foodRecommendationService
				.delete(foodRecommendationService.findByFoodRecommendationIdAndFoodFoodId(recommendationId, foodId));
		return "redirect:/view_food/" + foodId;
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
