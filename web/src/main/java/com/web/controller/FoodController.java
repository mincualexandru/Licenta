package com.web.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Food;
import com.web.model.FoodImage;
import com.web.model.FoodRecommendation;
import com.web.service.AccountService;
import com.web.service.FoodImageService;
import com.web.service.FoodRecommendationService;
import com.web.service.FoodService;

@Controller
public class FoodController {

	private Logger LOGGER = Logger.getLogger(HelperController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private FoodImageService foodImageService;

	@Autowired
	private FoodRecommendationService foodRecommendationService;

	@Autowired
	private FoodService foodService;

	@PostMapping(path = { "/add_photo_for_food_save" })
	public String addPhotoForExerciseSave(Model model, @RequestParam Integer foodId,
			@RequestParam("imageFile") MultipartFile imageFile, @ModelAttribute("foodImage") FoodImage foodImage) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			foodImage.setFileName(imageFile.getOriginalFilename());
			Food food = foodService.findById(foodId).get();
			foodImage.setFood(food);
			try {
				foodImageService.saveImage(imageFile, foodImage);
				foodImage.setPath("/uploadedimages/foods");
			} catch (Exception e) {
				e.printStackTrace();
			}
			foodImageService.save(foodImage);
			return "redirect:/view_food/" + foodId;
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/add_recommendation_for_food_save" })
	public String addAdviceForExerciseSave(
			@Valid @ModelAttribute("foodRecommendation") FoodRecommendation foodRecommendation,
			BindingResult bindingResult, RedirectAttributes attr, Model model, @RequestParam Integer foodId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				System.out.println("ARE ERORI");
				attr.addFlashAttribute("org.springframework.validation.BindingResult.foodRecommendation",
						bindingResult);
				attr.addFlashAttribute("foodRecommendation", foodRecommendation);
				return "redirect:/view_food/" + foodId;
			} else {
				foodRecommendation.setFood(foodService.findById(foodId).get());
				foodRecommendationService.save(foodRecommendation);
				return "redirect:/view_food/" + foodId;
			}
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/delete_photo_from_food" })
	public String deletePhotoFromFood(Model model, @RequestParam Integer foodId, @RequestParam Integer photoId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			FoodImage foodImage = foodImageService.findById(photoId).get();
			foodImageService.deleteImage(foodImage);
			foodImageService.deleteByFoodImageIdAndFoodFoodId(photoId, foodId);
			return "redirect:/view_food/" + foodId;
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/edit_recommendation_food" })
	public String editRecommendationFood(Model model, @RequestParam Integer foodId,
			@RequestParam Integer recommendationId, @RequestParam String editName, RedirectAttributes attr) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			FoodRecommendation advice = foodRecommendationService
					.findByFoodRecommendationIdAndFoodFoodId(recommendationId, foodId);
			if (editName.length() < 3) {
				attr.addFlashAttribute("errorAdvice", true);
				attr.addFlashAttribute("foodIdSelected", advice.getFoodRecommendationId());
				return "redirect:/view_food/" + foodId;
			} else {
				advice.setRecommendation(editName);
				foodRecommendationService.save(advice);
				return "redirect:/view_food/" + foodId;
			}
		} else {
			return "redirect:/nutritionist";
		}
	}

	@PostMapping(path = { "/delete_recommendation_food" })
	public String deleteRecommendationFood(Model model, @RequestParam Integer foodId,
			@RequestParam Integer recommendationId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			foodRecommendationService.delete(
					foodRecommendationService.findByFoodRecommendationIdAndFoodFoodId(recommendationId, foodId));
			return "redirect:/view_food/" + foodId;
		} else {
			return "redirect:/nutritionist";
		}
	}

}
