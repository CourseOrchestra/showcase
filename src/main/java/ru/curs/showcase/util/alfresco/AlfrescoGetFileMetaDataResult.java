package ru.curs.showcase.util.alfresco;

/**
 * Класс для результата получения метаданных файла в Alfresco.
 * 
 */
public class AlfrescoGetFileMetaDataResult extends AlfrescoBaseResult {

	private String metaData = null;

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(final String aMetaData) {
		metaData = aMetaData;
	}

}
