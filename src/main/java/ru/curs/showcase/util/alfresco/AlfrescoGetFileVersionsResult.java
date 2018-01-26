package ru.curs.showcase.util.alfresco;

/**
 * Класс для получения версий файла в Alfresco.
 * 
 */
public class AlfrescoGetFileVersionsResult extends AlfrescoBaseResult {

	private String versions = null;

	public String getVersions() {
		return versions;
	}

	public void setVersions(final String aVersions) {
		versions = aVersions;
	}

}
