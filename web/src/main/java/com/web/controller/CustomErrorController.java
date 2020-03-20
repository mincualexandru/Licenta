package com.web.controller;

import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

	@Override
	public String getErrorPath() {
		return "errors/appError";
	}
	
	@RequestMapping("/error")
	public String renderErrorPage(Model model, HttpServletRequest request, Principal principal, HttpSession session) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Integer statusCode = Integer.valueOf(status.toString());

		if (status != null) {
			if (principal != null) {
				if (statusCode == HttpStatus.NOT_FOUND.value())
					return "errors/404Error";
				if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
					return "errors/500Error";
				if (statusCode == HttpStatus.FORBIDDEN.value())
					return "errors/403Error";
			}
		}
		return "errors/appError";
	}

}