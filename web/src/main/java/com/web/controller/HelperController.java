package com.web.controller;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.model.Account;
import com.web.service.AccountService;
import com.web.service.RoleService;

@Controller
public class HelperController {

	private Logger LOGGER = Logger.getLogger(HelperController.class);

	@Autowired
	private RoleService roleService;

	@Autowired
	private AccountService accountService;

	@GetMapping(path = { "/view_learners" })
	public String viewLearners(Model model) {
		Account account = accountService.getAccountConnected();
		LOGGER.info(account.getUsername());
		if (account.isActive()) {
			Set<Account> learners = accountService.getLearners(account);
			model.addAttribute("helper", account);
			model.addAttribute("learners", learners);
			return "common/view_learners";
		} else if (account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}

	}
}
