package com.qsp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.service.DashbordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	
	private final DashbordService dashbordService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getDashbordData(){
		Map<String, Object> dashbordData= dashbordService.getDashboardData();
		return ResponseEntity.ok(dashbordData);
	}
}
