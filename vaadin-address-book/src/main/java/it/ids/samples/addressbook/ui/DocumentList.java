package it.ids.samples.addressbook.ui;

import it.ids.samples.addressbook.containers.DocumentBeanQuery;
import it.ids.samples.addressbook.entity.Document;
import it.ids.samples.addressbook.service.DocumentService;
import it.ids.samples.addressbook.util.SpringContextHelper;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;
import org.vaadin.addons.lazyquerycontainer.QueryItemStatus;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class DocumentList extends Table {

	private static final ThemeResource ICON = new ThemeResource("icons/32/arrow-down.png");

	static final Action DELETE_ACTION = new Action("Delete");

	public DocumentList() {

		BeanQueryFactory<DocumentBeanQuery> queryFactory = new BeanQueryFactory<DocumentBeanQuery>(DocumentBeanQuery.class);
		final LazyQueryContainer container = new LazyQueryContainer(queryFactory, false, 50);
		container.addContainerProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS, QueryItemStatus.class, QueryItemStatus.None, true, false);
		container.addContainerProperty("filename", String.class, "", true, true);
		container.addContainerProperty("length", String.class, "", true, true);
		container.addContainerProperty("uploadDate", Date.class, "", true, true);
		setContainerDataSource(container);

		setSizeFull();
		addGeneratedColumn("length", new ColumnGenerator() {

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Property p = source.getItem(itemId).getItemProperty(columnId);
				Long value = (Long) p.getValue();
				return FileUtils.byteCountToDisplaySize(value);
			}
		});

		addGeneratedColumn("download", new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				Property p = source.getItem(itemId).getItemProperty(columnId);

				// Button w/ icon and tooltip
				Button b = new Button();
				b.setStyleName(BaseTheme.BUTTON_LINK);
				b.setDescription("Download file");
				b.setIcon(ICON);
				b.addListener(new ClickListener() { // react to clicks

					@Override
					public void buttonClick(ClickEvent event) {

						BeanItem<Document> item = (BeanItem<Document>) source.getItem(itemId);

						final Document document = item.getBean();

						StreamResource.StreamSource source = new StreamResource.StreamSource() {
							public InputStream getStream() {
								DocumentService documentService = (DocumentService) SpringContextHelper.getBean("documentService");
								return documentService.getFileInputStream(document);
							}
						};

						StreamResource resource = new StreamResource(source, document.getFilename(), getApplication());
						resource.setMIMEType("application/octet-stream");

						getApplication().getMainWindow().open(resource);
					}
				});
				return b;
			}
		});
		setVisibleColumns(new String[] { LazyQueryView.PROPERTY_ID_ITEM_STATUS, "filename", "length", "uploadDate", "download" });
		setColumnHeaders(new String[] { "Status", "Filename", "Size", "Upload Date", "Download" });

		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);
		/*
		 * Make table selectable, react immediatedly to user events, and pass events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		setMultiSelect(true);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);

		// Actions (a.k.a context menu)
		addActionHandler(new Action.Handler() {
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { DELETE_ACTION };
			}

			public void handleAction(Action action, Object sender, Object target) {
				if (DELETE_ACTION == action) {

					final Object selection = getValue();
					if (selection == null) {
						return;
					}
					if (selection instanceof Integer) {
						final Integer selectedIndex = (Integer) selection;
						if (selectedIndex != null) {
							container.removeItem(selectedIndex);
						}
					}
					if (selection instanceof Collection) {
						final Collection selectionIndexes = (Collection) selection;
						for (final Object selectedIndex : selectionIndexes) {
							container.removeItem((Integer) selectedIndex);
						}
					}
					// Clear selection
					setValue(null);
					container.commit();
					container.refresh();
				}
			}
		});
	}
}