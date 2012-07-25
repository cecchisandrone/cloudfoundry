package it.ids.samples.addressbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class TestMongoDB {

	private int filesToInsert = 10;

	private DB db = null;

	@Before
	public void setUpBefore() throws Exception {

		Mongo m = new Mongo("localhost", 27017);
		db = m.getDB("local");
	}

	@Ignore
	@Test
	public void testSingleInsert() throws Exception {

		GridFS gfsFileType = new GridFS(db);
		GridFSInputFile gfsFile = gfsFileType.createFile(new File("C:\\logs.zip"));
		gfsFile.setFilename("test");
		gfsFile.save();

		// Load
		GridFS gfsFileType1 = new GridFS(db);
		GridFSDBFile fileOut1 = gfsFileType1.findOne((ObjectId) gfsFile.getId());

		InputStream is = fileOut1.getInputStream();
		int read = 0;
		byte[] bytes = new byte[1024];
		FileOutputStream os = new FileOutputStream(new File("C:\\output.tst"));
		while ((read = is.read(bytes)) != -1) {
			os.write(bytes, 0, read);
		}
		os.flush();
		os.close();
	}

	@Test
	public void testMassiveInsert() throws MongoException, IOException {

		GridFS gfsFileType = new GridFS(db);

		for (int i = 0; i < filesToInsert; i++) {
			GridFSInputFile gfsFile = gfsFileType.createFile(new File("C:\\logs.zip"));
			gfsFile.setFilename(new BigInteger(130, new SecureRandom()).toString(32));
			gfsFile.save();
		}
	}
}
