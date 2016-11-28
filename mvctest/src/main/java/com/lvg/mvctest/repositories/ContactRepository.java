package com.lvg.mvctest.repositories;

import org.springframework.data.repository.CrudRepository;

import com.lvg.mvctest.models.Contact;

public interface ContactRepository extends CrudRepository<Contact, Long>{

}
