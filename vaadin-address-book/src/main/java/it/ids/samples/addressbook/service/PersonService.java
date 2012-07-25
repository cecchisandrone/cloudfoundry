package it.ids.samples.addressbook.service;

import it.ids.samples.addressbook.entity.Person;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface PersonService {

	public Person save(Person person);

	public List<Person> findAll();
}
