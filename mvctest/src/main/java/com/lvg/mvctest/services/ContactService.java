package com.lvg.mvctest.services;

import java.util.List;

import com.lvg.mvctest.models.Contact;

public interface ContactService {
	List<Contact> finAll();
	Contact findById(Long id);
	Contact save(Contact contact);
}
