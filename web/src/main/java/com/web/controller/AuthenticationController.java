package com.web.controller;

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

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.Role;
import com.web.model.TypeMeasurement;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.RoleService;
import com.web.service.TransactionService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.utils.Gender;

@Controller
public class AuthenticationController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	private Logger logger = Logger.getLogger(AuthenticationController.class);

	@GetMapping(path = { "/", "/login" })
	public String login() {
		logger.info("This is an info log entry");
		Account account = accountService.getAccountConnected();
		if (account != null) {
			return "redirect:/home";
		}
		return "authentication/login";
	}

	@GetMapping(value = { "/signup" })
	public String signup(Model model) {
		if (!model.containsAttribute("user")) {
			Account user = new Account();
			model.addAttribute("user", user);
		}
		String chooseRoleName = (String) model.asMap().get("chooseRoleName");
		Set<Role> roles = roleService.findAll();
		roles.removeIf(element -> element.getName().equals("ROLE_ADMIN"));
		model.addAttribute("selectedRole", chooseRoleName);
		model.addAttribute("sex", Gender.values());
		model.addAttribute("roles", roles);
		return "authentication/signup";
	}

	@PostMapping(value = { "/signup" })
	public String signupSave(@Valid @ModelAttribute("user") Account user, BindingResult bindingResult,
			RedirectAttributes attr, Model model, @RequestParam String chooseRoleName) {
		if (accountService.findByUsername(user.getUsername()).isPresent()) {
			Account userExist = accountService.findByUsername(user.getUsername()).get();
			if (userExist != null) {
				attr.addFlashAttribute("errorMessage", "**Numele de utilizator exista");
				return "redirect:/signup";
			}
		}

		if (bindingResult.hasErrors()) {
			attr.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
			attr.addFlashAttribute("user", user);
			attr.addFlashAttribute("chooseRoleName", chooseRoleName);
			return "redirect:/signup";
		} else {
			if (chooseRoleName.equals("ROLE_USER")) {
				accountService.saveUser(user, chooseRoleName);
				transactionService.createTransaction(user);
				Device newDevice = new Device();
				Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findTypeMeasurementsForFitBuddy();
				deviceService.createDevice(newDevice, typeMeasurements, "Fit Buddy", 0);
				deviceService.save(newDevice);
				userDeviceService.createUserDevice(user, newDevice);
				attr.addFlashAttribute("deviceCreated", true);
				attr.addFlashAttribute("accountCreated", true);
				attr.addFlashAttribute("virtualWalletCreated", true);
			} else if (chooseRoleName.equals("ROLE_TRAINER") || chooseRoleName.equals("ROLE_NUTRITIONIST")) {
				accountService.saveUser(user, chooseRoleName);
				transactionService.createTransaction(user);
				attr.addFlashAttribute("virtualWalletCreated", true);
				attr.addFlashAttribute("accountCreated", true);
			}
		}

		return "redirect:/login";
	}
}
