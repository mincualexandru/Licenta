package com.web.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.TrainingPlan;
import com.web.service.AccountService;
import com.web.service.TrainingPlanService;

@Controller
public class TrainerController {

	private Logger logger = Logger.getLogger(AuthenticationController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private TrainingPlanService trainingPlanService;

	@GetMapping(path = { "/trainer" })
	public String trainer(Model model, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		return "trainer/trainer";
	}

	@GetMapping(path = { "/training_plans" })
	public String trainingPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		model.addAttribute("trainingPlans", account.getTrainingPlans());
		return "trainer/training_plans";
	}

	@GetMapping(path = { "/create_training_plan" })
	public String createTrainingPlan(Model model) {
		model.addAttribute("trainingPlan", new TrainingPlan());
		return "trainer/create_training_plan";
	}

	@PostMapping(path = { "/create_training_plan_save" })
	public String createTrainingPlanSave(Model model, @ModelAttribute("trainingPlan") TrainingPlan trainingPlan) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account trainer = accountService.findByUsername(auth.getName());
		trainingPlan.setTrainer(trainer);
		trainingPlanService.save(trainingPlan);
		return "redirect:/training_plans";
	}

}
