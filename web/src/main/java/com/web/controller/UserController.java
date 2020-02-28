package com.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

	@GetMapping(path = {"/home"})
	public String home() {
		return "home/home";
	}
	
}
