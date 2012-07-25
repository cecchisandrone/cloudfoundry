package it.ids.samples.addressbook.ui;

import it.ids.samples.addressbook.service.DocumentService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window.Notification;

public class DocumentView extends VerticalSplitPanel implements Receiver {

	protected static Logger logger = Logger.getLogger(DocumentView.class);

	private static final long serialVersionUID = 1L;

	private ProgressIndicator pi = new ProgressIndicator();
	private Upload upload = new Upload("Upload a file from your computer", this);
	private Label state = new Label();
	private Label textualProgress = new Label();
	private Label fileName = new Label();
	private DocumentService documentService;
	private String filename;
	private File tempFile;
	private static final int MAX_FILE_SIZE = 300000000;

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public DocumentView() {

		addStyleName("view");

		setCaption("Document library");
		setSizeFull();

		// Button to cancel upload
		final Button cancelProcessing = new Button("Cancel");
		cancelProcessing.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				upload.interruptUpload();
			}
		});
		cancelProcessing.setVisible(false);
		cancelProcessing.setStyleName("small");

		Panel p = new Panel("Status");
		p.setSizeUndefined();
		FormLayout l = new FormLayout();
		l.setMargin(true);
		p.setContent(l);
		HorizontalLayout stateLayout = new HorizontalLayout();
		stateLayout.setSpacing(true);
		stateLayout.addComponent(state);
		stateLayout.addComponent(cancelProcessing);
		stateLayout.setCaption("Current state:");
		state.setValue("Idle");
		l.addComponent(stateLayout);
		fileName.setCaption("Filename:");
		l.addComponent(fileName);
		pi.setCaption("Progress");
		pi.setVisible(false);
		l.addComponent(pi);
		textualProgress.setVisible(false);
		l.addComponent(textualProgress);
		upload.setImmediate(true);

		VerticalLayout vl = new VerticalLayout();
		vl.addComponent(upload);
		vl.addComponent(p);
		vl.setMargin(true);
		vl.setSpacing(true);

		// Adding listeners for upload
		upload.addListener(new Upload.StartedListener() {
			public void uploadStarted(StartedEvent event) {

				if (event.getContentLength() > MAX_FILE_SIZE) {
					upload.interruptUpload();
					getWindow().showNotification("Error", "Maximum file size is " + FileUtils.byteCountToDisplaySize(MAX_FILE_SIZE), Notification.TYPE_ERROR_MESSAGE);
				}

				// this method gets called immediatedly after upload is
				// started
				pi.setValue(0f);
				pi.setVisible(true);
				pi.setPollingInterval(500); // hit server frequantly to get
				textualProgress.setVisible(true);
				// updates to client
				state.setValue("Uploading");
				fileName.setValue(event.getFilename());

				cancelProcessing.setVisible(true);
			}
		});

		upload.addListener(new Upload.ProgressListener() {
			public void updateProgress(long readBytes, long contentLength) {
				// this method gets called several times during the update
				pi.setValue(new Float(readBytes / (float) contentLength));
				textualProgress.setValue("Processed " + FileUtils.byteCountToDisplaySize(readBytes) + " bytes of " + FileUtils.byteCountToDisplaySize(contentLength));
			}

		});

		upload.addListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {

				// Save file into db
				if (tempFile != null) {
					documentService.save(tempFile, filename);
					tempFile.delete();
				}
			}
		});

		upload.addListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {

			}
		});

		upload.addListener(new Upload.FinishedListener() {
			public void uploadFinished(FinishedEvent event) {
				state.setValue("Idle");
				pi.setVisible(false);
				textualProgress.setVisible(false);
				cancelProcessing.setVisible(false);
				fileName.setValue("");
				setFirstComponent(new DocumentList());
			}
		});

		// Set split panel components
		this.setFirstComponent(new DocumentList());
		this.setSecondComponent(vl);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {

		try {
			tempFile = File.createTempFile(filename, "tmp");
			logger.info("Saving upload to temp file: " + tempFile);
			this.filename = filename;
			return new FileOutputStream(tempFile);
		} catch (Exception e) {
			logger.error("Unable to create upload temporary file");
			throw new RuntimeException("Unable to create upload temporary file");
		}
	}
}
