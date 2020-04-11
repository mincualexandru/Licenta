package com.web.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.dao.AccountDao;
import com.web.model.Account;
import com.web.model.Role;
import com.web.utils.AccountRest;

@RestController
public class LoginController {

	@Autowired
	private AccountDao userDAO;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@PostMapping("/loginResource")
	public AccountRest loginResource(@RequestParam String userName, @RequestParam String password) {
		Account userConnected = userDAO.findByUsername(userName);
		if (userConnected.isActive()) {
			if (bCryptPasswordEncoder.matches(password, userConnected.getPassword())) {
				Role selectedRole = null;
				for (Role role : userConnected.getRoles()) {
					selectedRole = role;
				}
				AccountRest accountRest = new AccountRest();
				accountRest.setAccountId(userConnected.getAccountId());
				accountRest.setActive(userConnected.isActive());
				accountRest.setBornDate(userConnected.getBornDate());
				accountRest.setEmail(userConnected.getEmail());
				accountRest.setFirstName(userConnected.getFirstName());
				accountRest.setGender(userConnected.getGender().getGender());
				accountRest.setLastName(userConnected.getLastName());
				accountRest.setPassword(userConnected.getPassword());
				accountRest.setPhoneNumber(userConnected.getPhoneNumber());
				accountRest.setRole(selectedRole.getName());
				accountRest.setUsername(userConnected.getUsername());
				return accountRest;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
