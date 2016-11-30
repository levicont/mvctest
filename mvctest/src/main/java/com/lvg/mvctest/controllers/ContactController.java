package com.lvg.mvctest.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lvg.mvctest.models.Contact;
import com.lvg.mvctest.services.ContactService;
import com.lvg.mvctest.support.Message;
import com.lvg.mvctest.support.UrlUtil;

@Controller
@RequestMapping("/contacts")
public class ContactController {
	private static final Logger LOG  = Logger.getLogger(ContactController.class);
		
	@Autowired
	ContactService contactSevice;
	
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping(method=RequestMethod.GET)
	public String list(Model uiModel){
		LOG.info("Listing contacts");
		List<Contact> contacts = contactSevice.finAll();
		uiModel.addAttribute("contacts", contacts);
		LOG.info("No. of contacts: "+contacts.size());
		return "contacts/list";
	}
	
	@RequestMapping(path="/{id}", method=RequestMethod.GET)
	public String show(@PathVariable("id")Long id, Model uiModel){
		Contact contact = contactSevice.findById(id);
		uiModel.addAttribute("contact", contact);
		
		return "contacts/show";
	}
	
	
	@RequestMapping(path="/{id}?", params="form", method=RequestMethod.POST)
	public String update(@Valid Contact contact, BindingResult bindingResult, Model uiModel,
						 HttpServletRequest request, RedirectAttributes redirectAttributes, 
						 Locale locale){
		LOG.info("Updating contact");
		if(bindingResult.hasErrors()){
			uiModel.addAttribute("message", new Message("error",
					messageSource.getMessage("contact_save_fail",new Object[]{} ,locale)));
			uiModel.addAttribute("contact", contact);
			return "contacts/update";
		}
		
		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute("message", 
					new Message("success", 
							messageSource.getMessage("contact_save_success", new Object[]{}, locale)));
		contactSevice.save(contact);
		return "redirect:/contacts/"+UrlUtil.encodePathSegment(contact.getId().toString(),request);
		
	}
	
	@RequestMapping(path="/{id}", params="form", method=RequestMethod.GET)
	public String updateForm(@PathVariable("id")Long id, Model uiModel){
		uiModel.addAttribute("contact", contactSevice.findById(id));
		return "contacts/update";
	}
	
	@RequestMapping(params="form", method=RequestMethod.POST)
	public String create(@Valid Contact contact, BindingResult bindingResult, Model uiModel,
						 HttpServletRequest request, RedirectAttributes redirectAttributes, 
						 Locale locale){
		LOG.info("Creating contact");
		if(bindingResult.hasErrors()){
			uiModel.addAttribute("message", new Message("error",
					messageSource.getMessage("contact_save_fail",new Object[]{} ,locale)));
			uiModel.addAttribute("contact", contact);
			return "contacts/create";
		}
		
		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute("message", 
					new Message("success", 
							messageSource.getMessage("contact_save_success", new Object[]{}, locale)));
		LOG.info("Contact id: " + contact.getId());
		contactSevice.save(contact);
		return "redirect:/contacts/"+UrlUtil.encodePathSegment(contact.getId().toString(),request);
		
	}
	
	@RequestMapping(params="form", method=RequestMethod.GET)
	public String createForm(Model uiModel){
		Contact contact = new Contact();
		uiModel.addAttribute("contact", contact);
		return "contacts/create";
	}
	
}
