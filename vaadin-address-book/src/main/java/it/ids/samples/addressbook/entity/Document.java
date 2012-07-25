package it.ids.samples.addressbook.entity;

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.GridFSFile;

public class Document {

	private String id;

	private String filename;

	private long length;

	private Date uploadDate;

	public Document(GridFSFile file) {
		this.filename = file.getFilename();
		this.uploadDate = file.getUploadDate();
		this.length = file.getLength();
		this.id = ((ObjectId) file.getId()).toString();
	}

	public Document() {
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
