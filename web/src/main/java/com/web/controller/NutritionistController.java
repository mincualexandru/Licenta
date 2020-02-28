package com.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.model.Account;
import com.web.service.AccountService;
@Controller
public class NutritionistController {
	
	@Autowired
	private AccountService userService;

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
	
}
