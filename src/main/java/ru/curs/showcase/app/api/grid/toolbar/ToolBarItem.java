package ru.curs.showcase.app.api.grid.toolbar;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.event.Action;

/**
 * Элемент панели инструментов.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolBarItem extends BaseToolBarItem {
	private static final long serialVersionUID = 1L;

	private Action action;
	private String downloadLinkId = null;
	private String fileName = null;

	public ToolBarItem() {
		super();
	}

	public Action getAction() {
		return action;
	}

	public void setAction(final Action aAction) {
		action = aAction;
	}

	public String getDownloadLinkId() {
		return downloadLinkId;
	}

	public void setDownloadLinkId(final String aDownloadLinkId) {
		downloadLinkId = aDownloadLinkId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String aFileName) {
		fileName = aFileName;
	}

}
