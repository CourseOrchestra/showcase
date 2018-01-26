package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Дополнительная информация, которая может передаваться от сервера клиенту при
 * запросе данных.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LyraGridAddData implements SerializableElement {
	private static final long serialVersionUID = -3159944028389882617L;

	private String header = null;
	private String footer = null;

	public String getHeader() {
		return header;
	}

	public void setHeader(final String aHeader) {
		header = aHeader;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(final String aFooter) {
		footer = aFooter;
	}

}
