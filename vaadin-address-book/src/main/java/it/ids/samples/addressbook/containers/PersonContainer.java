package it.ids.samples.addressbook.containers;

import it.ids.samples.addressbook.entity.Person;
import it.ids.samples.addressbook.service.PersonService;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItemContainer;

@Component
@SuppressWarnings("serial")
public class PersonContainer extends BeanItemContainer<Person> implements Serializable {

	private PersonService personService;

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	/**
	 * Natural property order for Person bean. Used in tables and forms.
	 */
	public static final Object[] NATURAL_COL_ORDER = new Object[] { "firstName", "lastName", "email", "phoneNumber", "streetAddress", "postalCode", "city" };

	/**
	 * "Human readable" captions for properties in same order as in NATURAL_COL_ORDER.
	 */
	public static final String[] COL_HEADERS_ENGLISH = new String[] { "First name", "Last name", "Email", "Phone number", "Street Address", "Postal Code", "City" };

	public PersonContainer() throws InstantiationException, IllegalAccessException {
		super(Person.class);
	}

	public void populateContainer() {

		removeAllItems();

		List<Person> persons = personService.findAll();

		if (persons != null) {
			for (Person p : persons) {
				addItem(p);
			}
		}
	}
}
