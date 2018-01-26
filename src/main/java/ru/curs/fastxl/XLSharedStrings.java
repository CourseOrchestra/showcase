package ru.curs.fastxl;

import java.io.*;
import java.util.*;

import javax.xml.stream.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Класс, работающий с файлом sharedStrings.xml.
 * 
 */
class XLSharedStrings {

	private ArrayList<String> array = new ArrayList<>();
	private HashMap<String, Integer> reversed = new HashMap<>();

	XLSharedStrings(final InputStream is) throws EFastXLRuntime {
		SharedStringsParser p = new SharedStringsParser();
		try {
			TransformerFactory.newInstance().newTransformer()
					.transform(new StreamSource(is), new SAXResult(p));
		} catch (Exception e) {
			if (p.getError() != null) {
				throw p.getError();
			} else {
				throw new EFastXLRuntime("Could not parse sharedStrings.xml: " + e.getMessage());
			}
		}
	}

	/**
	 * Добавляет строку к строковому пулу.
	 * 
	 * @param s
	 *            Строка.
	 * @return Позиция строки в строковом пуле.
	 */
	public int appendString(final String s) {
		Integer result = reversed.get(s);
		if (result == null) {
			// Индекс, который получит новая строка.
			result = array.size();
			array.add(s);
			reversed.put(s, result);
		}
		return result;
	}

	/**
	 * Возвращает строку из строкового пула.
	 * 
	 * @param position
	 *            Позиция строки в пуле.
	 * @return Строку из пула.
	 */
	public String getString(final int position) {
		return array.get(position);
	}

	/**
	 * Возвращает длину массива строк в пуле.
	 */
	public int getCount() {
		return array.size();
	}

	/**
	 * Возвращает количество уникальных строк в пуле.
	 */
	public int getUniqueCount() {
		return reversed.size();
	}

	/**
	 * Созраняет XML в поток.
	 * 
	 * @param os
	 *            Поток, в который сохраняется xml.
	 * @throws EFastXLRuntime
	 *             Если что-то не получилось.
	 */
	public void saveXML(final OutputStream os) throws EFastXLRuntime {
		try {
			XMLStreamWriter xmlWriter =
				XMLOutputFactory.newInstance().createXMLStreamWriter(
						new OutputStreamWriter(os, "UTF-8"));
			xmlWriter.writeStartDocument("utf-8", "1.0");

			xmlWriter.writeStartElement("sst");
			xmlWriter.writeAttribute("xmlns",
					"http://schemas.openxmlformats.org/spreadsheetml/2006/main");
			xmlWriter.writeAttribute("count", String.valueOf(getCount()));
			xmlWriter.writeAttribute("uniqueCount", String.valueOf(getUniqueCount()));

			for (String value : array) {
				xmlWriter.writeStartElement("si");
				xmlWriter.writeStartElement("t");
				xmlWriter.writeCharacters(value);
				xmlWriter.writeEndElement();
				xmlWriter.writeEndElement();
			}

			// Финализируем документ
			xmlWriter.writeEndDocument();
			xmlWriter.flush();
		} catch (final Exception e) {
			throw new EFastXLRuntime(String.format("Не удаётся сохранить sharedStrings.\n%s",
					e.getMessage()));
		}
	}

	/**
	 * Состояние парсера.
	 */
	private enum ParserState {
		INITIAL, SST, SI, T,
	}

	/**
	 * SAX обработчик.
	 * 
	 * @author bogatov
	 * 
	 */
	private class SharedStringsParser extends DefaultHandler {

		private ParserState state = ParserState.INITIAL;
		private StringBuilder text = new StringBuilder();
		private EFastXLRuntime e = null;

		public EFastXLRuntime getError() {
			return e;
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {
			// Читаем значение текста
			if (state == ParserState.T) {
				text.append(ch, start, length);
			}
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) throws SAXException {

			switch (state) {
			case INITIAL:
				if ("sst".equals(localName)) {
					String count = atts.getValue("count");
					// Если файл содержит подсказки --- используем их, чтобы не
					// потом не перестраивать массив и хэш
					if (count != null) {
						array = new ArrayList<String>(Integer.parseInt(count));
					}
					count = atts.getValue("uniqueCount");
					if (count != null) {
						reversed = new HashMap<String, Integer>(Integer.parseInt(count));
					}
				}
				if ("si".equals(localName)) {
					state = ParserState.SI;
				}
				break;
			case SST:
				if ("si".equals(localName)) {
					state = ParserState.SI;
				}
				break;
			case SI:
				if ("t".equals(localName)) {
					state = ParserState.T;
					text = new StringBuilder();
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name)
				throws SAXException {
			switch (state) {
			case T:
				if ("t".equals(localName)) {
					// Запихиваем прочитанную строку в память
					String buf = text.toString();
					array.add(buf);
					reversed.put(buf, array.size() - 1);
					try {
						validateAddedString(array.size() - 1, buf);
					} catch (EFastXLRuntime e2) {
						e = e2;
					}
					state = ParserState.SI;
				}
				break;
			case SI:
				if ("si".equals(localName)) {
					state = ParserState.SST;
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Вызывается в момент десериализации объекта. Наследники могут
	 * переопределить данный метод, чтобы выполнить тотальный поиск/индексацию
	 * по всему файлу.
	 * 
	 * @param index
	 *            индекс строки.
	 * @param value
	 *            значение строки.
	 * @throws EFastXLRuntime
	 *             Если строка ошибочна (т. е. явно содержит плейсхолдер, но у
	 *             плейсхолдера неверный синтаксис).
	 */
	@SuppressWarnings("unused")
	protected void validateAddedString(final int index, final String value) throws EFastXLRuntime {

	}
}
