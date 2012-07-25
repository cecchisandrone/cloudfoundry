package it.ids.samples.addressbook.containers;

import it.ids.samples.addressbook.entity.Document;
import it.ids.samples.addressbook.entity.DocumentListSortOrder;
import it.ids.samples.addressbook.service.DocumentService;
import it.ids.samples.addressbook.util.SpringContextHelper;

import java.util.List;
import java.util.Map;

import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

public class DocumentBeanQuery extends AbstractBeanQuery<Document> {

	private DocumentService documentService;

	private DocumentListSortOrder documentListOrder;

	public DocumentBeanQuery(QueryDefinition definition, Map<String, Object> queryConfiguration, Object[] sortPropertyIds, boolean[] sortStates) {
		super(definition, queryConfiguration, sortPropertyIds, sortStates);
		this.documentService = (DocumentService) SpringContextHelper.getBean("documentService");

		// Update sort order
		if (sortPropertyIds != null && sortPropertyIds.length != 0)
			documentListOrder = new DocumentListSortOrder((String) sortPropertyIds[0], sortStates[0]);
	}

	@Override
	protected Document constructBean() {
		return new Document();
	}

	@Override
	public int size() {
		return documentService.getDocumentsCount();
	}

	@Override
	protected List<Document> loadBeans(int startIndex, int count) {
		return documentService.findDocuments(startIndex, count, documentListOrder);
	}

	@Override
	protected void saveBeans(List<Document> addedDocuments, List<Document> modifiedDocuments, List<Document> removedDocuments) {
		// Handle CUD operations
		if (removedDocuments != null) {
			documentService.removeDocuments(removedDocuments);
		}
	}
}
