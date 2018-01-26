package ru.curs.showcase.util.alfresco;

/**
 * Класс для результата создания директории в Alfresco.
 * 
 */
public class AlfrescoCreateFolderResult extends AlfrescoBaseResult {

	private String nodeRef = null;

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(final String aNodeRef) {
		nodeRef = aNodeRef;
	}

}
