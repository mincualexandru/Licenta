package com.web.controller;

import java.util.Iterator;
import java.util.Set;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Role;
import com.web.model.Account;
import com.web.service.RoleService;
import com.web.utils.Gender;
import com.web.service.AccountService;

@Controller
public class AuthenticationController {

	@Autowired
	private AccountService userService;

	@Autowired
	private RoleService roleService;
	
	private Logger logger = Logger.getLogger(AuthenticationController.class);
	
	@GetMapping(path = { "/", "/login" })
	public String login() {
		logger.info("This is an info log entry");
		return "authentication/login";
	}

	@GetMapping(value = { "/signup" })
	public String signup(Model model) {
		if (!model.containsAttribute("user")) {
			Account user = new Account();
			model.addAttribute("user", user);
		}

		String chooseRoleName = (String) model.asMap().get("chooseRoleName");
		model.addAttribute("selectedRole", chooseRoleName);

		Set<Role> roles = roleService.findAll();
		Iterator<Role> iter = roles.iterator();
		while (iter.hasNext()) {
			Role role = iter.next();
			if (role.getName().equals("ROLE_ADMIN")) {
				iter.remove();
				break;

			}
		}
		
		model.addAttribute("sex", Gender.values());
		
		model.addAttribute("roles", roles);
		return "authentication/signup";
	}

	@PostMapping(value = { "/signup" })
	public String signupSave(@Valid @ModelAttribute("user") Account user, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam String chooseRoleName) {

		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
			attr.addFlashAttribute("user", user);
			attr.addFlashAttribute("chooseRoleName", chooseRoleName);
			return "redirect:/signup";
		} else {
			if(chooseRoleName.equals("ROLE_USER")) {
				user.setActive(true);
				userService.saveUser(user, chooseRoleName);
			} else if(chooseRoleName.equals("ROLE_TRAINER") || chooseRoleName.equals("ROLE_NUTRITIONIST")) {
				user.setActive(false);
				userService.saveUser(user, chooseRoleName);
			}
		}

		return "redirect:/login";
	}
}