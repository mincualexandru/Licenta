package com.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.validation.Valid;

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
import com.web.model.AccountInformation;
import com.web.model.Education;
import com.web.model.Experience;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;
import com.web.service.EducationService;
import com.web.service.ExperienceService;
import com.web.service.SkillService;

@Controller
public class CurriculumVitaeController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountInformationService accountInformationService;

	@Autowired
	private ExperienceService experienceService;

	@Autowired
	private EducationService educationService;

	@Autowired
	private SkillService skillService;

	@GetMapping(path = { "/curriculum-vitae" })
	public String curriculumVitae(Model model) {
		Account account = accountService.getAccountConnected();
		Role selectedRole = new Role();
		for (Role role : account.getRoles()) {
			selectedRole = role;
			break;
		}
		if (!account.isActive()) {
			if (!model.containsAttribute("curriculumVitae")) {
				AccountInformation curriculumVitae = new AccountInformation();
				curriculumVitae.setSkills(new ArrayList<Skill>());
				curriculumVitae.setEducation(new ArrayList<Education>());
				curriculumVitae.setExperiences(new ArrayList<Experience>());
				model.addAttribute("curriculumVitae", curriculumVitae);
			}
			model.addAttribute("account", account);
			return "common/curriculum-vitae";
		} else if (selectedRole.getName().equals("ROLE_NUTRITIONIST")) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/curriculumVitaeSave" })
	public String curriculumVitaeSave(@Valid @ModelAttribute("curriculumVitae") AccountInformation curriculumVitae,
			BindingResult bindingResult, RedirectAttributes attr, @RequestParam Integer accountId, Model model) {

		if (bindingResult.hasErrors()) {

			curriculumVitae.getSkills().removeIf(element -> element.getDescription() == "");
			curriculumVitae.getEducation().removeIf(element -> element.getCity() == "" && element.getEnd() == ""
					&& element.getStart() == "" && element.getName() == "");
			curriculumVitae.getExperiences().removeIf(element -> element.getCity() == "" && element.getEnd() == ""
					&& element.getStart() == "" && element.getName() == "");

			curriculumVitae.getSkills().removeIf(element -> element.getDescription() == null);
			curriculumVitae.getEducation().removeIf(element -> element.getCity() == null && element.getEnd() == null
					&& element.getStart() == null && element.getName() == null);
			curriculumVitae.getExperiences().removeIf(element -> element.getCity() == null && element.getEnd() == null
					&& element.getStart() == null && element.getName() == null);

			curriculumVitae.getExperiences().removeIf(element -> {
				try {
					return new SimpleDateFormat("yyyy").parse(element.getStart())
							.after(new SimpleDateFormat("yyyy").parse(element.getEnd()));

				} catch (ParseException e) {
					e.printStackTrace();
				}
				return false;
			});

			curriculumVitae.getEducation().removeIf(element -> {
				try {
					return new SimpleDateFormat("yyyy").parse(element.getStart())
							.after(new SimpleDateFormat("yyyy").parse(element.getEnd()));

				} catch (ParseException e) {
					e.printStackTrace();
				}
				return false;
			});

			attr.addFlashAttribute("org.springframework.validation.BindingResult.curriculumVitae", bindingResult);
			attr.addFlashAttribute("curriculumVitae", curriculumVitae);
			return "redirect:/curriculum-vitae";
		} else {
			Account currentAccount = accountService.findById(accountId).get();

			curriculumVitae.getSkills().removeIf(element -> element.getDescription() == null);
			curriculumVitae.getEducation().removeIf(element -> element.getCity() == null);
			curriculumVitae.getExperiences().removeIf(element -> element.getCity() == null);

			curriculumVitae.getSkills().forEach(element -> element.setAccountInformation(curriculumVitae));
			curriculumVitae.getEducation().forEach(element -> element.setAccountInformation(curriculumVitae));
			curriculumVitae.getExperiences().forEach(element -> element.setAccountInformation(curriculumVitae));

			accountInformationService.createAccountInformation(curriculumVitae, currentAccount);

			for (Role role : currentAccount.getRoles()) {
				if (role.getName().equals("ROLE_TRAINER")) {
					attr.addFlashAttribute("message", "Cv-ul tau a fost trimis cu succes !");
					return "redirect:/trainer";
				} else if (role.getName().equals("ROLE_NUTRITIONIST")) {
					attr.addFlashAttribute("message", "Cv-ul tau a fost trimis cu succes !");
					return "redirect:/nutritionist";
				}
			}
		}
		return "redirect:/appError";
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
