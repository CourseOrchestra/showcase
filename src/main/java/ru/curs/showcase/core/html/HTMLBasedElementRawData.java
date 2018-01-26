package ru.curs.showcase.core.html;

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.ElementRawData;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Класс, содержащий необработанные XML данные и метаданные HTML-подобного
 * элемента инф. панели - WEBTEXT или XFORMS.
 * 
 * @author den
 * 
 */
public final class HTMLBasedElementRawData extends ElementRawData {

	/**
	 * Исходные данные.
	 */
	private Document data;

	/**
	 * Конструктор, создающий RawWebText, не содержащий исходные данные для
	 * трансформации.
	 * 
	 * @param aElementInfo
	 *            - описание элемента.
	 * @param aContext
	 *            - контекст.
	 */
	public HTMLBasedElementRawData(final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		super(aElementInfo, aContext);
		data = XMLUtils.createEmptyDoc("root");
	}

	public HTMLBasedElementRawData(final Document aDataDocument, final InputStream settings,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		super(aElementInfo, aContext, settings);
		data = aDataDocument;
	}

	public Document getData() {
		return data;
	}

	public void setData(final Document aData) {
		data = aData;
	}

	/**
	 * Установка данных, переданных в виде строки, содержащей XML.
	 * 
	 * @param aData
	 *            - новые данные.
	 * @throws IOException
	 * @throws SAXException
	 */
	public void setData(final String aData) throws SAXException, IOException {
		data = XMLUtils.stringToDocument(aData);
	}

}
