package it.ids.samples.addressbook.entity;

public class DocumentListSortOrder {

	private String propertyId;

	private boolean ascendant;

	public DocumentListSortOrder(String propertyId, boolean ascendant) {
		this.propertyId = propertyId;
		this.ascendant = ascendant;
	}

	public void setAscendant(boolean ascendant) {
		this.ascendant = ascendant;
	}

	public boolean isAscendant() {
		return ascendant;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyId() {
		return propertyId;
	}
}
