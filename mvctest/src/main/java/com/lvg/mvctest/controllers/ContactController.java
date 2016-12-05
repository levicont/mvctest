package com.lvg.mvctest.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.lvg.mvctest.models.Contact;
import com.lvg.mvctest.models.ContactGrid;
import com.lvg.mvctest.services.ContactService;
import com.lvg.mvctest.support.Message;
import com.lvg.mvctest.support.UrlUtil;

@Controller
@RequestMapping("/contacts")
public class ContactController {
	private static final Logger LOG = Logger.getLogger(ContactController.class);

	@Autowired
	ContactService contactSevice;

	@Autowired
	MessageSource messageSource;

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model uiModel) {
		LOG.info("Listing contacts");
		List<Contact> contacts = contactSevice.finAll();
		uiModel.addAttribute("contacts", contacts);
		LOG.info("No. of contacts: " + contacts.size());
		return "contacts/list";
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") Long id, Model uiModel) {
		Contact contact = contactSevice.findById(id);
		uiModel.addAttribute("contact", contact);

		return "contacts/show";
	}

	@RequestMapping(path = "/{id}?", params = "form", method = RequestMethod.POST)
	public String update(@Valid Contact contact, BindingResult bindingResult, Model uiModel,
			HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale,
			@RequestParam(value="file", required=false)Part file) {
		LOG.info("Updating contact");
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("message", new Message("error",
					messageSource.getMessage("contact_save_fail", new Object[] {}, locale)));
			uiModel.addAttribute("contact", contact);
			return "contacts/update";
		}

		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute("message", new Message("success",
				messageSource.getMessage("contact_save_success", new Object[] {}, locale)));
		// processing uploading file
		if (file != null) {
			LOG.info("File name: " + file.getName());
			LOG.info("File size: " + file.getSize());
			LOG.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream in = file.getInputStream();
				if (in == null)
					LOG.info("File inputstream is null");
				fileContent = IOUtils.toByteArray(in);
				contact.setPhoto(fileContent);
			} catch (IOException ex) {
				LOG.info("Error saving uploaded file");
			}
			contact.setPhoto(fileContent);
		}
		contactSevice.save(contact);
		return "redirect:/contacts/" + UrlUtil.encodePathSegment(contact.getId().toString(), request);

	}

	@RequestMapping(path = "/{id}", params = "form", method = RequestMethod.GET)
	public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		uiModel.addAttribute("contact", contactSevice.findById(id));
		return "contacts/update";
	}

	@RequestMapping(params = "form", method = RequestMethod.POST)
	public String create(@Valid Contact contact, BindingResult bindingResult, Model uiModel,
			HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale,
			@RequestParam(value = "file", required = false) Part file) {
		LOG.info("Creating contact");
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("message", new Message("error",
					messageSource.getMessage("contact_save_fail", new Object[] {}, locale)));
			uiModel.addAttribute("contact", contact);
			return "contacts/create";
		}

		uiModel.asMap().clear();
		redirectAttributes.addFlashAttribute("message", new Message("success",
				messageSource.getMessage("contact_save_success", new Object[] {}, locale)));
		LOG.info("Contact id: " + contact.getId());

		// processing uploading file
		if (file != null) {
			LOG.info("File name: " + file.getName());
			LOG.info("File size: " + file.getSize());
			LOG.info("File content type: " + file.getContentType());
			byte[] fileContent = null;
			try {
				InputStream in = file.getInputStream();
				if (in == null)
					LOG.info("File inputstream is null");
				fileContent = IOUtils.toByteArray(in);
				contact.setPhoto(fileContent);
			} catch (IOException ex) {
				LOG.info("Error saving uploaded file");
			}
			contact.setPhoto(fileContent);
		}
		contactSevice.save(contact);
		return "redirect:/contacts/" + UrlUtil.encodePathSegment(contact.getId().toString(), request);

	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		Contact contact = new Contact();
		uiModel.addAttribute("contact", contact);
		return "contacts/create";
	}

	@RequestMapping(value = "/listgrid", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ContactGrid listGrid(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			@RequestParam(value = "sidx", required = false) String sortBy,
			@RequestParam(value = "sord", required = false) String order) {
		LOG.info("Listing contacts for grid with page: " + page + ", rows: " + rows);
		LOG.info("Listing contacts for grid with sort: " + sortBy + ", order: " + order);

		// ***Sorting setup
		Sort sort = null;
		String orderBy = sortBy;
		if (orderBy != null && orderBy.equals("birthDateString"))
			orderBy = "birthDate";
		if (orderBy != null && order != null) {
			if (order.equals("desc")) {
				sort = new Sort(Sort.Direction.DESC, orderBy);
			} else {
				sort = new Sort(Sort.Direction.ASC, orderBy);
			}
		}

		// ** Paging for Spring Data starts from 0
		// ** Paging for jqGrid starts from 1

		PageRequest pageRequest = null;
		if (sort != null) {
			pageRequest = new PageRequest(page - 1, rows, sort);
		} else {
			pageRequest = new PageRequest(page - 1, rows);
		}

		Page<Contact> contactPage = contactSevice.findAllByPage(pageRequest);
		ContactGrid contactGrid = new ContactGrid();
		contactGrid.setCurrentPage(contactPage.getNumber() + 1);
		contactGrid.setTotalPages(contactPage.getTotalPages());
		contactGrid.setTotalRecords(contactPage.getTotalElements());
		contactGrid.setContactData(Lists.newArrayList(contactPage.iterator()));

		return contactGrid;

	}

	@RequestMapping(value = "/photo/{id}", method = RequestMethod.GET)
	@ResponseBody
	public byte[] downloadPhoto(@PathVariable("id") Long id) {
		Contact contact = contactSevice.findById(id);		
		if (contact.getPhoto() != null) {
			LOG.info("Downloading photo for contact id: " + contact.getId() + " with size: "
					+ contact.getPhoto().length);

		}
		return contact.getPhoto();
	}

}
