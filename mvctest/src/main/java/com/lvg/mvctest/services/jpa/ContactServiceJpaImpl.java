package com.lvg.mvctest.services.jpa;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.lvg.mvctest.models.Contact;
import com.lvg.mvctest.repositories.ContactRepository;
import com.lvg.mvctest.services.ContactService;

@Repository
@Service("contactService")
@Transactional
public class ContactServiceJpaImpl implements ContactService{
	private static final Logger LOG = Logger.getLogger(ContactServiceJpaImpl.class);
	
	@Autowired
	ContactRepository contactRepository;
	
	@Transactional(readOnly = true)
	public List<Contact> finAll() {
		return Lists.newArrayList(contactRepository.findAll());
	}

	@Transactional(readOnly = true)
	public Contact findById(Long id) {
		return contactRepository.findOne(id);
	}
	
	@Transactional(readOnly = true)
	public Page<Contact> findAllByPage(Pageable pageable){
		return contactRepository.findAll(pageable);
	}

	public Contact save(Contact contact) {
		LOG.info("Starting to save contact: "+contact);
		return contactRepository.save(contact);
	}
	
}
