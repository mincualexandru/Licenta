package com.web.controller;

import java.util.ArrayList;

import javax.validation.Valid;

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

import com.web.dao.AccountInformationDao;
import com.web.model.Account;
import com.web.model.AccountInformation;
import com.web.model.Education;
import com.web.model.Experience;
import com.web.model.Role;
import com.web.model.Skill;
import com.web.service.AccountService;
import com.web.service.RoleService;

@Controller
public class CurriculumVitaeController {

	@Autowired
	private AccountService userService;

	@Autowired
	private AccountInformationDao accountInformationDao;

	@GetMapping(path = { "/curriculum-vitae" })
	public String curriculumVitae(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = userService.findByUsername(auth.getName());
		if (!model.containsAttribute("curriculumVitae")) {
			AccountInformation curriculumVitae = new AccountInformation();
			curriculumVitae.setSkills(new ArrayList<Skill>());
			curriculumVitae.setEducation(new ArrayList<Education>());
			curriculumVitae.setExperiences(new ArrayList<Experience>());
			model.addAttribute("curriculumVitae", curriculumVitae);
		}
		model.addAttribute("account", account);
		return "authentication/curriculum-vitae";
	}

	@PostMapping(path = { "/curriculumVitaeSave" })
	public String curriculumVitaeSave(
			@Valid @ModelAttribute("curriculumVitae") AccountInformation curriculumVitae, BindingResult bindingResult,
			RedirectAttributes attr, @RequestParam Integer accountId, Model model) {

		System.out.println("Erori " + bindingResult.getErrorCount());
		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.curriculumVitae", bindingResult);
			attr.addFlashAttribute("curriculumVitae", curriculumVitae);
			return "redirect:/curriculum-vitae";
		} else {
			Account currentAccount = userService.findById(accountId).get();

			curriculumVitae.getSkills().removeIf(element -> element.getDescription() == null);
			curriculumVitae.getEducation().removeIf(element -> element.getCity() == null);
			curriculumVitae.getExperiences().removeIf(element -> element.getCity() == null);

			for (Skill skill : curriculumVitae.getSkills())
				skill.setAccountInformation(curriculumVitae);

			for (Education edu : curriculumVitae.getEducation())
				edu.setAccountInformation(curriculumVitae);

			for (Experience experience : curriculumVitae.getExperiences())
				experience.setAccountInformation(curriculumVitae);

			currentAccount.setAccountInformation(curriculumVitae);
			curriculumVitae.setAccount(currentAccount);
			accountInformationDao.save(curriculumVitae);
			userService.save(currentAccount);

			for (Role role : currentAccount.getRoles()) {
				if (role.getName().equals("ROLE_TRAINER")) {
					attr.addFlashAttribute("message", "Felicitari ! Cv-ul tau a fost trimis cu succes !");
					return "redirect:/trainer";
				} else if (role.getName().equals("ROLE_NUTRITIONIST")) {
					attr.addFlashAttribute("message", "Felicitari ! Cv-ul tau a fost trimis cu succes !");
					return "redirect:/nutritionist";
				}
			}
		}
		return "redirect:/appError";
	}
}
