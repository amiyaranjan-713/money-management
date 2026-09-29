package com.qsp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/status","/health"})
public class HomeController {
	
	@GetMapping
	public String healthCheck() {
		return "Money Management API is running with CI/CD!";
	}
}
