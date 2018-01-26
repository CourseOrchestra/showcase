package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты скачивание файла с сервера.
 * 
 * @author bogatov
 * 
 */
public class XFormDownloadAttributes extends InputAttributes {
	private String elementId;
	private String formData;

	public String getElementId() {
		return elementId;
	}

	public void setElementId(final String aElementId) {
		this.elementId = aElementId;
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(final String aFormData) {
		this.formData = aFormData;
	}

}
