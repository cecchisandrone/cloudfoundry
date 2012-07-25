package it.ids.samples.addressbook.service;

import it.ids.samples.addressbook.entity.Document;
import it.ids.samples.addressbook.entity.DocumentListSortOrder;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface DocumentService {

	public void save(File file, String filename);

	public List<Document> findDocuments(int startIndex, int size, DocumentListSortOrder order);

	public abstract int getDocumentsCount();

	public InputStream getFileInputStream(Document document);

	public void removeDocuments(List<Document> removedDocuments);
}
