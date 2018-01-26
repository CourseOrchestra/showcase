package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;

/**
 * Класс метаданных лирагрида.
 * 
 */
@XmlRootElement(name = "lyraGridMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class LyraGridMetadata extends GridMetadata {
	private static final long serialVersionUID = -1919802279561448844L;

	private String lyraGridSorting = null;

	private boolean needCreateWebSocket = false;

	public LyraGridMetadata() {
		super();
	}

	public LyraGridMetadata(final DataPanelElementInfo elInfo) {
		super(elInfo);
	}

	public String getLyraGridSorting() {
		return lyraGridSorting;
	}

	public void setLyraGridSorting(final String aLyraGridSorting) {
		lyraGridSorting = aLyraGridSorting;
	}

	public boolean isNeedCreateWebSocket() {
		return needCreateWebSocket;
	}

	public void setNeedCreateWebSocket(final boolean aNeedCreateWebSocket) {
		needCreateWebSocket = aNeedCreateWebSocket;
	}

}
