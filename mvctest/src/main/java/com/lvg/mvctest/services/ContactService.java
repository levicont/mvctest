package com.lvg.mvctest.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lvg.mvctest.models.Contact;

public interface ContactService {
	List<Contact> finAll();
	Contact findById(Long id);
	Contact save(Contact contact);
	Page<Contact> findAllByPage(Pageable pagable);
}
