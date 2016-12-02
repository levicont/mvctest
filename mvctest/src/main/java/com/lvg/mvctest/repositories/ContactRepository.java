package com.lvg.mvctest.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.lvg.mvctest.models.Contact;

public interface ContactRepository extends PagingAndSortingRepository<Contact, Long>{

}
