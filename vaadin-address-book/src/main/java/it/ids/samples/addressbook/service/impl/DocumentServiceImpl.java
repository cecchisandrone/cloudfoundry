package it.ids.samples.addressbook.service.impl;

import it.ids.samples.addressbook.entity.Document;
import it.ids.samples.addressbook.entity.DocumentListSortOrder;
import it.ids.samples.addressbook.service.DocumentService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;

public class DocumentServiceImpl implements DocumentService {

	protected static Logger logger = Logger.getLogger(DocumentServiceImpl.class);

	private MongoTemplate mongoTemplate;

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void save(File file, String filename) {

		GridFS gfsFileType = new GridFS(mongoTemplate.getDb());
		GridFSInputFile gfsFile;
		try {
			gfsFile = gfsFileType.createFile(file);
			gfsFile.setFilename(filename);
			gfsFile.save();
		} catch (IOException e) {
			logger.error("Unable to upload file into DB");
		}
	}

	@Override
	public List<Document> findDocuments(int startIndex, int size, DocumentListSortOrder order) {
		GridFS gridFS = new GridFS(mongoTemplate.getDb());
		DBCursor cursor = null;
		if (order != null)
			cursor = gridFS.getFileList().sort(new BasicDBObject(order.getPropertyId(), order.isAscendant() ? 1 : -1));
		else
			cursor = gridFS.getFileList();

		cursor.skip(startIndex);

		// Build the result list
		List<Document> documents = new ArrayList<Document>();
		while (cursor.hasNext() && documents.size() != size) {
			documents.add(new Document((GridFSFile) cursor.next()));
		}
		return documents;
	}

	@Override
	public int getDocumentsCount() {
		GridFS gridFS = new GridFS(mongoTemplate.getDb());
		return gridFS.getFileList().count();
	}

	@Override
	public InputStream getFileInputStream(Document document) {
		GridFS gridFS = new GridFS(mongoTemplate.getDb());
		GridFSDBFile file = gridFS.findOne(new ObjectId(document.getId()));
		return file.getInputStream();
	}

	@Override
	public void removeDocuments(List<Document> removedDocuments) {
		GridFS gridFS = new GridFS(mongoTemplate.getDb());
		for (Document document : removedDocuments) {
			gridFS.remove(new ObjectId(document.getId()));
		}
	}
}
