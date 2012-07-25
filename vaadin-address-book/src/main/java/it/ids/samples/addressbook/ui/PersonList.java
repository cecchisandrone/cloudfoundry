package it.ids.samples.addressbook.ui;

import it.ids.samples.addressbook.AddressBookApplication;
import it.ids.samples.addressbook.containers.PersonContainer;
import it.ids.samples.addressbook.entity.Person;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class PersonList extends Table {
	public PersonList(AddressBookApplication app) {
		setSizeFull();
		setContainerDataSource(app.getPersonContainer());

		setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
		setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);

		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);

		/*
		 * Make table selectable, react immediatedly to user events, and pass events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		addListener((ValueChangeListener) app);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);

		// customize email column to have mailto: links using column generator
		addGeneratedColumn("email", new ColumnGenerator() {
			public Component generateCell(Table source, Object itemId, Object columnId) {
				Person p = (Person) itemId;
				Link l = new Link();
				l.setResource(new ExternalResource("mailto:" + p.getEmail()));
				l.setCaption(p.getEmail());
				return l;
			}
		});
	}

}