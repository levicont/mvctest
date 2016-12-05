package com.lvg.mvctest.controllers;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lvg.mvctest.support.Message;

@Controller
@RequestMapping("/security")
public class SecurityController {
	private static final Logger LOG = Logger.getLogger(SecurityController.class);
	
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping("/loginfail")
	public String loginFail(Model uiModel, Locale locale){
		LOG.info("Login fail detected");
		uiModel.addAttribute("message", new Message("error", messageSource.getMessage("message_login_fail", 
				new Object[]{}, locale)));
		return "contacts/list";
	}
}
