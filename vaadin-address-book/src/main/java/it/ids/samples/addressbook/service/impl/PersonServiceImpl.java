package it.ids.samples.addressbook.service.impl;

import it.ids.samples.addressbook.entity.Person;
import it.ids.samples.addressbook.repositories.PersonRepository;
import it.ids.samples.addressbook.service.PersonService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

	@Autowired
	private PersonRepository repository;

	@Override
	@Transactional
	public Person save(Person person) {
		return repository.save(person);
	}

	@Override
	public List<Person> findAll() {
		return repository.findAll();
	}
}
