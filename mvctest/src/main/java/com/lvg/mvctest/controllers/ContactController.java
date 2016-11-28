package com.lvg.mvctest.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lvg.mvctest.models.Contact;
import com.lvg.mvctest.services.ContactService;

@Controller
@RequestMapping("/contacts")
public class ContactController {
	private static final Logger LOG  = Logger.getLogger(ContactController.class);
		
	@Autowired
	ContactService contactSevice;
	
	@RequestMapping(method=RequestMethod.GET)
	public String list(Model uiModel){
		LOG.info("Listing contacts");
		List<Contact> contacts = contactSevice.finAll();
		uiModel.addAttribute("contacts", contacts);
		LOG.info("No. of contacts: "+contacts.size());
		return "contacts/list";
	}
}
