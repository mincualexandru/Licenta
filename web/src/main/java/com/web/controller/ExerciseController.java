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
import com.web.model.Exercise;
import com.web.model.ExerciseAdvice;
import com.web.model.ExerciseImage;
import com.web.model.HelperPlan;
import com.web.service.AccountService;
import com.web.service.ExerciseAdviceService;
import com.web.service.ExerciseImageService;
import com.web.service.ExerciseService;

@Controller
public class ExerciseController {

	private Logger LOGGER = Logger.getLogger(HelperController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseImageService exerciseImageService;

	@Autowired
	private ExerciseAdviceService exerciseAdviceService;

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
					exerciseImage.setPath("/uploadedimages/exercises/man/");
				} else if (helperPlan.getForWho().getGender().equals("Femeie")) {
					exerciseImage.setPath("/uploadedimages/exercises/woman/");
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
	public String addAdviceForExerciseSave(@Valid @ModelAttribute("exerciseAdvice") ExerciseAdvice exerciseAdvice,
			BindingResult bindingResult, RedirectAttributes attr, Model model, @RequestParam Integer exerciseId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (bindingResult.hasErrors()) {
				attr.addFlashAttribute("org.springframework.validation.BindingResult.exerciseAdvice", bindingResult);
				attr.addFlashAttribute("exerciseAdvice", exerciseAdvice);
				return "redirect:/view_exercise/" + exerciseId;
			} else {
				exerciseAdvice.setExercise(exerciseService.findById(exerciseId).get());
				exerciseAdviceService.save(exerciseAdvice);
				return "redirect:/view_exercise/" + exerciseId;
			}
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/delete_photo_from_exercise" })
	public String deletePhotoFromExercise(Model model, @RequestParam Integer exerciseId,
			@RequestParam Integer photoId) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			ExerciseImage exerciseImage = exerciseImageService.findById(photoId).get();
			exerciseImageService.deleteImage(exerciseImage);
			exerciseImageService.deleteByExerciseImagesIdAndExerciseExerciseId(photoId, exerciseId);
			return "redirect:/view_exercise/" + exerciseId;
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/edit_advice_exercise" })
	public String editAdviceExercise(Model model, @RequestParam Integer exerciseId, @RequestParam Integer adviceId,
			@RequestParam String editName, RedirectAttributes attr) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			ExerciseAdvice advice = exerciseAdviceService.findByExerciseAdviceIdAndExerciseExerciseId(adviceId,
					exerciseId);
			if (editName.length() < 3) {
				attr.addFlashAttribute("errorAdvice", true);
				attr.addFlashAttribute("exerciseIdSelected", advice.getExerciseAdviceId());
				return "redirect:/view_exercise/" + exerciseId;
			} else {
				advice.setAdvice(editName);
				exerciseAdviceService.save(advice);
				return "redirect:/view_exercise/" + exerciseId;
			}
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

}
