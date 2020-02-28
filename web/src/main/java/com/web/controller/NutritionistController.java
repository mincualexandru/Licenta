package com.web.controller;

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
import com.web.model.Experience;
import com.web.model.Skill;
import com.web.service.AccountService;
import com.web.service.EducationService;
import com.web.service.ExperienceService;
import com.web.service.SkillService;
@Controller
public class NutritionistController {
	
	private Logger logger = Logger.getLogger(AuthenticationController.class);
	
	@Autowired
	private AccountService userService;
	
	@Autowired
	private ExperienceService experienceService;

	@Autowired
	private EducationService educationService;
	
	@Autowired
	private SkillService skillService;
	
	@GetMapping(path = {"/nutritionist"})
	public String nutritionist(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = userService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		return "nutritionist/nutritionist";
	}
	
	
	@GetMapping(path = {"/view_profile"})
	public String viewProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = userService.findByUsername(auth.getName());
		model.addAttribute("account", account);
		return "common/view_profile";
	}
	
	@PostMapping(path = {"/update_experience"})
	public String updateExperience(Model model, 
			@RequestParam Integer experienceId,
			@RequestParam String name,
			@RequestParam String city,
			@RequestParam String start,
			@RequestParam String end) {
		Experience experience = experienceService.findById(experienceId).get();
		experience.setName(name);
		experience.setCity(city);
		experience.setStart(start);
		experience.setEnd(end);
		experienceService.save(experience);
		return "redirect:/view_profile";
	}
	
	@PostMapping(path = {"/update_education"})
	public String updateEducation(Model model, 
			@RequestParam Integer educationId,
			@RequestParam String name,
			@RequestParam String city,
			@RequestParam String start,
			@RequestParam String end) {
		Education education = educationService.findById(educationId).get();
		education.setName(name);
		education.setCity(city);
		education.setStart(start);
		education.setEnd(end);
		educationService.save(education);
		return "redirect:/view_profile";
	}
	
	@PostMapping(path = {"/update_skill"})
	public String updateSkill(Model model, 
			@RequestParam Integer skillId,
			@RequestParam String description) {
		Skill skill = skillService.findById(skillId).get();
		skill.setDescription(description);
		skillService.save(skill);
		return "redirect:/view_profile";
	}
	
	@PostMapping(path = {"/delete_experience"})
	public String deleteExperience(Model model, 
			@RequestParam Integer experienceId) {
		Experience experience = experienceService.findById(experienceId).get();
		experienceService.delete(experience);
		return "redirect:/view_profile";
	}
	
	@PostMapping(path = {"/delete_education"})
	public String deleteEducation(Model model, 
			@RequestParam Integer educationId) {
		Education education = educationService.findById(educationId).get();
		educationService.delete(education);
		return "redirect:/view_profile";
	}
	
	@PostMapping(path = {"/delete_skill"})
	public String deleteSkill(Model model, 
			@RequestParam Integer skillId) {
		Skill skill = skillService.findById(skillId).get();
		skillService.delete(skill);
		return "redirect:/view_profile";
	}
	
}
