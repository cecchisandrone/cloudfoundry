package it.ids.samples.addressbook;

import it.ids.samples.addressbook.containers.PersonContainer;
import it.ids.samples.addressbook.entity.SearchFilter;
import it.ids.samples.addressbook.ui.DocumentView;
import it.ids.samples.addressbook.ui.HelpWindow;
import it.ids.samples.addressbook.ui.ListView;
import it.ids.samples.addressbook.ui.NavigationTree;
import it.ids.samples.addressbook.ui.PersonForm;
import it.ids.samples.addressbook.ui.PersonList;
import it.ids.samples.addressbook.ui.SearchView;
import it.ids.samples.addressbook.ui.SharingOptions;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class AddressBookApplication extends Application implements ClickListener, ValueChangeListener, ItemClickListener {

	private final NavigationTree tree = new NavigationTree(this);

	private final Button newContact = new Button();
	private final Button search = new Button();
	private final Button share = new Button();
	private final Button files = new Button();
	private final Button help = new Button();
	private final SplitPanel horizontalSplit = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

	// Lazyly created ui references
	private ListView listView = null;
	private SearchView searchView = null;
	private DocumentView documentView = null;
	private PersonList personList = null;
	private PersonForm personForm = null;
	private HelpWindow helpWindow = null;
	private SharingOptions sharingOptions = null;

	private PersonContainer personContainer;

	public void setPersonContainer(PersonContainer personContainer) {
		this.personContainer = personContainer;
	}

	public PersonContainer getPersonContainer() {
		return personContainer;
	}

	public void setPersonForm(PersonForm personForm) {
		this.personForm = personForm;
	}

	public void setDocumentView(DocumentView documentView) {
		this.documentView = documentView;
	}

	@Override
	public void init() {
		buildMainLayout();
		setMainComponent(getListView());
	}

	private void buildMainLayout() {
		setMainWindow(new Window("Address Book Demo application"));

		setTheme("contacts");

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		layout.addComponent(createToolbar());
		layout.addComponent(horizontalSplit);
		layout.setExpandRatio(horizontalSplit, 1);

		horizontalSplit.setSplitPosition(200, SplitPanel.UNITS_PIXELS);
		horizontalSplit.setFirstComponent(tree);

		getMainWindow().setContent(layout);
	}

	private HorizontalLayout createToolbar() {
		HorizontalLayout lo = new HorizontalLayout();
		lo.addComponent(newContact);
		lo.addComponent(files);
		lo.addComponent(search);
		lo.addComponent(share);
		lo.addComponent(help);

		search.addListener((ClickListener) this);
		share.addListener((ClickListener) this);
		help.addListener((ClickListener) this);
		newContact.addListener((ClickListener) this);
		files.addListener((ClickListener) this);

		search.setIcon(new ThemeResource("icons/32/globe.png"));
		search.setDescription("Search");
		share.setIcon(new ThemeResource("icons/32/users.png"));
		share.setDescription("Share");
		help.setIcon(new ThemeResource("icons/32/help.png"));
		help.setDescription("Help");
		newContact.setIcon(new ThemeResource("icons/32/document-add.png"));
		newContact.setDescription("New Contact");
		files.setIcon(new ThemeResource("icons/32/folder-add.png"));
		files.setDescription("Documents");

		lo.setMargin(true);
		lo.setSpacing(true);

		lo.setStyleName("toolbar");

		lo.setWidth("100%");

		Embedded em = new Embedded("", new ThemeResource("images/logo.png"));
		lo.addComponent(em);
		lo.setComponentAlignment(em, Alignment.MIDDLE_RIGHT);
		lo.setExpandRatio(em, 1);

		return lo;
	}

	private void setMainComponent(Component c) {
		horizontalSplit.setSecondComponent(c);
	}

	/*
	 * View getters exist so we can lazily generate the views, resulting in faster application startup time.
	 */
	private ListView getListView() {
		if (listView == null) {
			personList = new PersonList(this);
			listView = new ListView(personList, personForm);
		}
		return listView;
	}

	private SearchView getSearchView() {
		if (searchView == null) {
			searchView = new SearchView(this);
		}
		return searchView;
	}

	private HelpWindow getHelpWindow() {
		if (helpWindow == null) {
			helpWindow = new HelpWindow();
		}
		return helpWindow;
	}

	private SharingOptions getSharingOptions() {
		if (sharingOptions == null) {
			sharingOptions = new SharingOptions();
		}
		return sharingOptions;
	}

	private DocumentView getDocumentView() {
		return documentView;
	}

	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == search) {
			showSearchView();
		} else if (source == help) {
			showHelpWindow();
		} else if (source == share) {
			showShareWindow();
		} else if (source == newContact) {
			addNewContanct();
		} else if (source == files) {
			showDocumentView();
		}
	}

	private void showHelpWindow() {
		getMainWindow().addWindow(getHelpWindow());
	}

	private void showShareWindow() {
		getMainWindow().addWindow(getSharingOptions());
	}

	private void showListView() {
		setMainComponent(getListView());
	}

	private void showSearchView() {
		setMainComponent(getSearchView());
	}

	private void showDocumentView() {
		setMainComponent(getDocumentView());
	}

	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		if (property == personList) {
			Item item = personList.getItem(personList.getValue());
			if (item != personForm.getItemDataSource()) {
				personForm.setItemDataSource(item);
			}
		}
	}

	public void itemClick(ItemClickEvent event) {
		if (event.getSource() == tree) {
			Object itemId = event.getItemId();
			if (itemId != null) {
				if (itemId == NavigationTree.SHOW_ALL) {
					// clear previous filters
					getPersonContainer().removeAllContainerFilters();
					showListView();
				} else if (itemId == NavigationTree.SEARCH) {
					showSearchView();
				} else if (itemId instanceof SearchFilter) {
					search((SearchFilter) itemId);
				}
			}
		}
	}

	private void addNewContanct() {
		showListView();
		personForm.addContact();
	}

	public void search(SearchFilter searchFilter) {
		// clear previous filters
		getPersonContainer().removeAllContainerFilters();
		// filter contacts with given filter
		getPersonContainer().addContainerFilter(searchFilter.getPropertyId(), searchFilter.getTerm(), true, false);
		showListView();

		getMainWindow().showNotification("Searched for " + searchFilter.getPropertyId() + "=*" + searchFilter.getTerm() + "*, found " + getPersonContainer().size() + " item(s).",
				Notification.TYPE_TRAY_NOTIFICATION);
	}

	public void saveSearch(SearchFilter searchFilter) {
		tree.addItem(searchFilter);
		tree.setParent(searchFilter, NavigationTree.SEARCH);
		// mark the saved search as a leaf (cannot have children)
		tree.setChildrenAllowed(searchFilter, false);
		// make sure "Search" is expanded
		tree.expandItem(NavigationTree.SEARCH);
		// select the saved search
		tree.setValue(searchFilter);
	}

}
