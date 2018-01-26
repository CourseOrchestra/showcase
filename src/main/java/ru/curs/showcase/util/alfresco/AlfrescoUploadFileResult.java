package ru.curs.showcase.util.alfresco;

/**
 * Класс для результата аплоада файла в Alfresco.
 * 
 */
public class AlfrescoUploadFileResult extends AlfrescoBaseResult {

	private String nodeRef = null;

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(final String aNodeRef) {
		nodeRef = aNodeRef;
	}

}
