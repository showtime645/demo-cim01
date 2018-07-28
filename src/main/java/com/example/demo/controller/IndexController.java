package com.example.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.util.BeanUtils;
import com.farsunset.cim.sdk.server.session.DefaultSessionManager;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/index")
@Slf4j
public class IndexController {

	@GetMapping("/index")
	public void index() {
		log.info("index page.");
	}
	
	@GetMapping("/list")
	public String list(HttpServletRequest request) {

		request.setAttribute("sessionList",
				((DefaultSessionManager) BeanUtils.getBean("CIMSessionManager")).queryAll());
		
		log.info("sessionList is {}" , ((DefaultSessionManager) BeanUtils.getBean("CIMSessionManager")).queryAll());

		return "list";
	}
}
