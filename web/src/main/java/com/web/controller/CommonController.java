package com.web.controller;

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
import com.web.model.Experience;
import com.web.model.Skill;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;
import com.web.service.EducationService;
import com.web.service.ExperienceService;
import com.web.service.SkillService;

@Controller
public class CommonController {

	private Logger logger = Logger.getLogger(AuthenticationController.class);

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
}
