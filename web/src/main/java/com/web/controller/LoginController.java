package com.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.web.dao.AccountDao;
import com.web.model.Account;

@RestController
public class LoginController {

	@Autowired
	private AccountDao userDAO;

	@PostMapping("/loginResource")
	public Account loginResource(@RequestBody String userName) {
		String message = "";
		try {
			Account userConnected = userDAO.findByUsername(userName);
			message = "You logged in successfully  " + userName + "!";
			ResponseEntity.status(HttpStatus.OK).body(message);
			return userConnected;
		} catch (Exception e) {
			message = "FAIL " + userName + "!";
			ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
			return null;
		}
	}

}
