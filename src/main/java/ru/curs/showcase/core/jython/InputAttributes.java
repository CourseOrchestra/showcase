package ru.curs.showcase.core.jython;

/**
 * Входные атрибуты для получения данных.
 * 
 * @author bogatov
 * 
 */
public class InputAttributes {

	private String mainContext;
	private String addContext;
	private String filterinfo;
	private String sessionContext;

	public String getMainContext() {
		return mainContext;
	}

	public void setMainContext(final String aMainContext) {
		this.mainContext = aMainContext;
	}

	public String getAddContext() {
		return addContext;
	}

	public void setAddContext(final String aAddContext) {
		this.addContext = aAddContext;
	}

	public String getFilterinfo() {
		return filterinfo;
	}

	public void setFilterinfo(final String afilterinfo) {
		this.filterinfo = afilterinfo;
	}

	public String getSessionContext() {
		return sessionContext;
	}

	public void setSessionContext(final String aSessionContext) {
		this.sessionContext = aSessionContext;
	}

}
