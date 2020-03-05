package com.web.controller;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.model.Account;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;

@Controller
public class AdminController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountInformationService accountInformationService;

	private Logger logger = Logger.getLogger(AuthenticationController.class);

	@GetMapping(path = { "/admin" })
	public String admin(Model model) {
		model.addAttribute("inactiveAccountsSize", accountService.findAllByActive(false).size());
		return "admin/admin";
	}

	@GetMapping(path = { "/validations-accounts" })
	public String validationsAccounts(Model model) {
		Set<Account> inactiveAccounts = accountService.findAllByActive(false);
		inactiveAccounts.removeIf(element -> element.getAccountInformation() == null);
		model.addAttribute("inactiveAccounts", inactiveAccounts);
		return "admin/validations-accounts";
	}

	@PostMapping(path = { "/validate-account" })
	public String validateAccount(Model model, @RequestParam Integer accountId) {
		model.addAttribute("account", accountService.findById(accountId).get());
		model.addAttribute("accountInformation", accountInformationService.findByAccountAccountId(accountId));
		return "admin/validate-account";
	}

	@PostMapping(path = { "/validate-account-save" })
	public String validateAccountSave(@RequestParam Integer accountId, Model model) {
		Account account = accountService.findById(accountId).get();
		logger.info("Arata id " + account.getAccountId());
		account.setActive(true);
		accountService.save(account);
		return "redirect:/validations-accounts";
	}

	@GetMapping(path = { "/users" })
	public String getAllUsers(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		logger.info("Numele contului curent este " + account.getUsername());
		Set<Account> accounts = accountService.findAll();
		accounts.removeIf(element -> element.getUsername() == account.getUsername());
		model.addAttribute("accounts", accounts);
		return "admin/users";
	}

	@PostMapping(path = { "/view_account_details" })
	public String viewAccountDetails(Model model, @RequestParam Integer accountId) {
		model.addAttribute("account", accountService.findById(accountId).get());
		model.addAttribute("accountInformation", accountInformationService.findByAccountAccountId(accountId));
		return "admin/view_account_details";
	}

	@PostMapping(path = { "/create-user-save" })
	public String createUserSave(Model model, @ModelAttribute("user") Account user) {
		accountService.save(user);
		return "redirect:/users";
	}

	@PostMapping(path = { "/delete-user" })
	public String deleteUser(Model model, @RequestParam Integer userId) {
		Account user = accountService.findById(userId).get();
		accountService.delete(user);
		return "redirect:/users";
	}

	@PostMapping(path = { "/update-user" })
	public String modifyUser(Model model, @RequestParam Integer userId, @RequestParam String name) {
		Account user = accountService.findById(userId).get();
		user.setUsername(name);
		accountService.save(user);
		return "redirect:/users";
	}
}
