package ru.curs.fastxl;

import java.io.*;
import java.util.LinkedHashMap;

import javax.xml.stream.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Обработчик worksheet-xml.
 * 
 */
final class WorksheetProcessor {

	private final InputStream is;
	private final GridRecordSet conn;

	public WorksheetProcessor(final InputStream inputStream, final GridRecordSet gridRecordSet) {
		this.is = inputStream;
		this.conn = gridRecordSet;
	}

	/**
	 * Выполняет трансформацию листа Excel, вставляя вместо плейсхолдеров
	 * результаты выполнения хранимых процедур.
	 * 
	 * @param os
	 *            поток, в который записывается трансформированный лист.
	 * @param sharedStrings
	 *            пул строк
	 * 
	 * @param placeholders
	 *            мэппинг между номерами строк и плейсхолдерами
	 * 
	 * @throws EFastXLRuntime
	 *             Если что-то пошло не так...
	 * 
	 */
	public void transform(final OutputStream os, final XLSharedStrings sharedStrings)
			throws EFastXLRuntime {

		WorksheetParser p = null;
		try {
			XMLStreamWriter xmlWriter =
				XMLOutputFactory.newInstance().createXMLStreamWriter(
						new OutputStreamWriter(os, "UTF-8"));
			p = new WorksheetParser(xmlWriter, sharedStrings);
			TransformerFactory.newInstance().newTransformer()
					.transform(new StreamSource(is), new SAXResult(p));
			xmlWriter.flush();
		} catch (Exception e) {
			if (p != null && p.getError() != null) {
				throw p.getError();
			} else {
				throw new EFastXLRuntime("Error while transforming worksheet: " + e.getClass()
						+ " - " + e.getMessage());
			}
		}
	}

	/**
	 * Состояние парсера.
	 * 
	 */
	private enum ParserState {
		INITIAL, SHEET_DATA, ROW, C, V, AFTER_PLACEHOLDER;
	}

	/**
	 * Worksheet SAX парсер.
	 * 
	 */
	private class WorksheetParser extends DefaultHandler {

		private EFastXLRuntime ex = null;

		private final XMLStreamWriter xmlWriter;
		private final XLSharedStrings sharedStrings;

		private ParserState state = ParserState.INITIAL;

		private String templateCellType = "";

		private int activeRowNum = 0;
		private CellAddress activeCellAddress = new CellAddress(1, 1);

		private int rowOffset = 0;

		private final LinkedHashMap<String, String> prefixes = new LinkedHashMap<>();

		public WorksheetParser(final XMLStreamWriter xmlStreamWriter,
				final XLSharedStrings xlsSharedStrings) {
			this.xmlWriter = xmlStreamWriter;
			this.sharedStrings = xlsSharedStrings;
		}

		public EFastXLRuntime getError() {
			return ex;
		}

		private void error(final Exception e) throws SAXException {
			this.ex = new EFastXLRuntime(e.getMessage());
			throw new SAXException(e);
		}

		@Override
		public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
			prefixes.put(prefix, uri);

			try {
				if ("".equals(prefix)) {
					xmlWriter.setDefaultNamespace(uri);
				} else {
					xmlWriter.setPrefix(prefix, uri);
				}

			} catch (XMLStreamException e) {
				error(e);
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {

			try {
				String chars = new String(ch, start, length);
				if (state == ParserState.V && "s".equalsIgnoreCase(templateCellType)) {
					int index = Integer.parseInt(chars);

					if ("here".equals(sharedStrings.getString(index))) {

						// Подменяем левый верхний угол названием столбца
						index = sharedStrings.appendString(conn.getColumnName(1));

						state = ParserState.AFTER_PLACEHOLDER;

					}
					xmlWriter.writeCharacters(String.valueOf(index));
					// И если при этом мы --- в резалтсете, то заполняем именами
					// столбцов текущую ячейку и те, что идут вправо
					if (state == ParserState.AFTER_PLACEHOLDER) {
						xmlWriter.writeEndElement(); // v
						xmlWriter.writeEndElement(); // c

						CellAddress ca = new CellAddress(activeCellAddress.getAddress());
						for (int i = 2; i <= conn.getColumnCount(); i++) {
							xmlWriter.writeStartElement("c");
							ca.setCol(ca.getCol() + 1);
							xmlWriter.writeAttribute("r", ca.getAddress());
							xmlWriter.writeAttribute("t", "s");
							xmlWriter.writeStartElement("v");

							index = sharedStrings.appendString(conn.getColumnName(i));
							xmlWriter.writeCharacters(String.valueOf(index));

							xmlWriter.writeEndElement(); // v
							xmlWriter.writeEndElement(); // c
						}
						return;
					}
				} else if (state != ParserState.AFTER_PLACEHOLDER) {
					xmlWriter.writeCharacters(chars);
				}
			} catch (XMLStreamException e) {
				this.ex = new EFastXLRuntime(e.getMessage());
				throw new SAXException(e);
			} catch (EFastXLRuntime e) {
				this.ex = e;
				throw new SAXException(e);
			}
		}

		@Override
		public void endDocument() throws SAXException {
			try {
				xmlWriter.writeEndDocument();
			} catch (XMLStreamException e) {
				error(e);
			}
		}

		@Override
		public void processingInstruction(final String target, final String data)
				throws SAXException {
			try {
				xmlWriter.writeProcessingInstruction(target, data);
			} catch (XMLStreamException e) {
				error(e);
			}
		}

		@Override
		public void skippedEntity(final String name) throws SAXException {

			try {
				xmlWriter.writeEntityRef(name);
			} catch (XMLStreamException e) {
				error(e);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			try {
				xmlWriter.writeStartDocument("utf-8", "1.0");
			} catch (XMLStreamException e) {
				error(e);
			}

		}

		// CHECKSTYLE:OFF
		@Override
		public void startElement(String uri, String localName, String name, Attributes atts)
				throws SAXException {
			try {
				switch (state) {
				case INITIAL:
					if ("sheetData".equals(localName))
						state = ParserState.SHEET_DATA;
					break;
				case SHEET_DATA:
					if ("row".equals(localName)) {
						state = ParserState.ROW;
						activeRowNum = Integer.parseInt(atts.getValue("r")) + rowOffset;
						// Информацию о строке копируем, но номер строки
						// увеличиваем на смещение
						xmlWriter.writeStartElement(uri, localName);
						copyAttrs(atts, String.valueOf(activeRowNum));
						return;
					}
					break;
				case ROW:
					if ("c".equals(localName)) {
						state = ParserState.C;

						templateCellType = atts.getValue("t");
						activeCellAddress = new CellAddress(atts.getValue("r"));
						activeCellAddress.setRow(activeCellAddress.getRow() + rowOffset);

						// Информацию о ячейке копируем, но номер строки
						// увеличиваем на смещение.
						xmlWriter.writeStartElement(uri, localName);
						copyAttrs(atts, activeCellAddress.getAddress());
						return;
					}
					break;
				case C:
					if ("v".equals(localName)) {
						state = ParserState.V;
					}
					break;
				case AFTER_PLACEHOLDER:
					return;
				default:
					break;
				}
				xmlWriter.writeStartElement(uri, localName);
				for (String prefix : prefixes.keySet())
					xmlWriter.writeAttribute("".equals(prefix) ? "xmlns" : "xmlns:" + prefix,
							prefixes.get(prefix));
				prefixes.clear();
				copyAttrs(atts, atts.getValue("r"));

			} catch (XMLStreamException e) {
				error(e);
			}
		}

		private void copyAttrs(Attributes atts, String newAddress) throws XMLStreamException {
			for (int i = 0; i < atts.getLength(); i++)
				if ("r".equals(atts.getQName(i)))
					xmlWriter.writeAttribute("r", newAddress);
				else
					xmlWriter.writeAttribute(atts.getQName(i), atts.getValue(i));
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {
			try {
				switch (state) {
				case V:
					state = ParserState.C;
					break;
				case C:
					// Вылезли из ячейки
					state = ParserState.ROW;
					break;
				case ROW:
					// Вылезли из строки
					state = ParserState.SHEET_DATA;

					break;
				case AFTER_PLACEHOLDER:
					if ("row".equals(localName)) {
						// Вылезли из строки с плейсхолдером
						state = ParserState.SHEET_DATA;
						xmlWriter.writeEndElement();
						// Самое главное: пишем строки данных

						CellAddress ca = new CellAddress(activeCellAddress.getAddress());
						while (conn.next()) {
							ca.setRow(ca.getRow() + 1);
							rowOffset++;
							xmlWriter.writeStartElement("row");
							xmlWriter.writeAttribute("r", String.valueOf(ca.getRow()));

							for (int i = 0; i < conn.getColumnCount(); i++) {

								CellAddress ca2 = new CellAddress(ca.getCol() + i, ca.getRow());
								xmlWriter.writeStartElement("c");
								xmlWriter.writeAttribute("r", ca2.getAddress());
								if (!conn.isInteger(i + 1) && !conn.isFloat(i + 1))
									xmlWriter.writeAttribute("t", "s");
								xmlWriter.writeStartElement("v");

								String value;
								if (conn.isInteger(i + 1))
									value = String.valueOf(conn.getInt(i + 1));
								else if (conn.isFloat(i + 1))
									value = String.valueOf(conn.getDouble(i + 1));
								else
									value =
										String.valueOf(sharedStrings.appendString(conn
												.getString(i + 1)));
								xmlWriter.writeCharacters(value);
								xmlWriter.writeEndElement(); // v
								xmlWriter.writeEndElement(); // c
							}

							xmlWriter.writeEndElement(); // row
						}
					}
					return;
				case SHEET_DATA:
					state = ParserState.INITIAL;
					break;
				default:
					break;
				}
				xmlWriter.writeEndElement();
			} catch (Exception e) {
				error(e);
			}
		}

	}

}
