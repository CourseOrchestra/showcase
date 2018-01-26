package ru.curs.showcase.core.html.xform;

import java.io.*;
import java.util.*;

import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.GeneralConstants;
import ru.curs.showcase.runtime.XSLTransformerPoolFactory;

/**
 * Класс, разрезающий XForm на содержимое тэгов script и body.
 * 
 */
public final class XFormCutter {

	private XFormCutter() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Разрезает XForm на содержимое тэгов script и body.
	 * 
	 * @param xFormHTML
	 *            Строка с содержимым страницы XForm.
	 * @return Список строк, первой идёт содержимое тэга body, далее ---
	 *         содержимое тэгов script.
	 * @throws TransformerException
	 *             в случае ошибки ввода-вывода, ошибки XSLT-преобразования и
	 *             проч.
	 * @throws XMLStreamException
	 *             в случае ошибки ввода-вывода, ошибки XSLT-преобразования и
	 *             проч.
	 * @throws IOException
	 */
	public static List<String> xFormParts(final String xFormHTML)
			throws TransformerException, XMLStreamException, IOException {

		// переход на новую версию XForms
		String xFormHTML2 = xFormHTML.replace("xmlns=\"\"", "");

		BodyFilter saxParser = new BodyFilter();
		Transformer tr = XSLTransformerPoolFactory.getInstance().acquire();
		try {
			tr.transform(new StreamSource(new StringReader(xFormHTML2)), new SAXResult(saxParser));
		} finally {
			XSLTransformerPoolFactory.getInstance().release(tr);
		}
		/*
		 * Этот список имеет вполне конкретную структуру: сначала идёт тело,
		 * затем стиль (один), затем идут скрипты (которых может быть
		 * несколько).
		 */
		List<String> result = new ArrayList<String>(2 + saxParser.scriptBuilders.size());
		result.add(saxParser.bodyWriter.toString());
		result.add(saxParser.styleBuilder.toString());
		for (StringBuilder sb : saxParser.scriptBuilders) {
			result.add(sb.toString());
		}
		return result;
	}

	/**
	 * Обработчик, выделяющий тэги body и script. Для формирования фрагмента
	 * HTML-кода.
	 */
	private static class BodyFilter extends DefaultHandler {
		/**
		 * String SCRIPT.
		 */
		private static final String SCRIPT = "script";
		/**
		 * String BODY.
		 */
		private static final String BODY = "body";

		/**
		 * enum State.
		 */
		private enum State {
			/**
			 * SKIP, BODY, SCRIPT, STYLE.
			 */
			SKIP, BODY, SCRIPT, STYLE
		};

		/**
		 * State state.
		 */
		private State state = State.SKIP;
		/**
		 * StringWriter bodyWriter.
		 */
		private final StringWriter bodyWriter = new StringWriter();
		/**
		 * StringBuilder styleBuilder.
		 */
		private final StringBuilder styleBuilder = new StringBuilder();
		/**
		 * XMLStreamWriter xmlWriter.
		 */
		private final XMLStreamWriter xmlWriter;
		/**
		 * LinkedList<StringBuilder> scriptBuilders.
		 */
		private final LinkedList<StringBuilder> scriptBuilders = new LinkedList<StringBuilder>();

		public BodyFilter() throws XMLStreamException {
			super();
			xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(bodyWriter);
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {
			switch (state) {
			case BODY:
				try {
					xmlWriter.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXException(e);
				}
				break;
			case SCRIPT:
				scriptBuilders.getLast().append(ch, start, length);
				break;
			case STYLE:
				styleBuilder.append(ch, start, length);
				break;
			default:
				break;
			}

		}

		@Override
		public void endDocument() throws SAXException {
			try {
				xmlWriter.writeEndDocument();
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name)
				throws SAXException {
			switch (state) {
			case BODY:
				if (BODY.equalsIgnoreCase(localName)) {
					state = State.SKIP;
				} else {
					try {
						xmlWriter.writeEndElement();
					} catch (XMLStreamException e) {
						throw new SAXException(e);
					}
				}
				break;
			case SCRIPT:
				if (SCRIPT.equalsIgnoreCase(localName)) {
					state = State.SKIP;
				}
				break;
			case STYLE:
				if (GeneralConstants.STYLE_TAG.equalsIgnoreCase(localName)) {
					state = State.SKIP;
				}
				break;
			default:
				break;
			}
		}

		@Override
		public void processingInstruction(final String target, final String data)
				throws SAXException {
			if (state == State.BODY) {
				try {
					xmlWriter.writeProcessingInstruction(target, data);
				} catch (XMLStreamException e) {
					throw new SAXException(e);
				}
			}
		}

		@Override
		public void skippedEntity(final String name) throws SAXException {
			if (state == State.BODY) {
				try {
					xmlWriter.writeEntityRef(name);
				} catch (XMLStreamException e) {
					throw new SAXException(e);
				}
			}
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) throws SAXException {

			try {
				switch (state) {
				case BODY:
					xmlWriter.writeStartElement(uri, localName);
					for (int i = 0; i < atts.getLength(); i++) {
						xmlWriter.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
					break;
				case SKIP:
					if (BODY.equalsIgnoreCase(localName)) {
						state = State.BODY;
					} else if (SCRIPT.equalsIgnoreCase(localName)) {
						state = State.SCRIPT;
						scriptBuilders.add(new StringBuilder());
					} else if (GeneralConstants.STYLE_TAG.equalsIgnoreCase(localName)) {
						state = State.STYLE;
					}
					break;
				default:
					break;
				}
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

		@Override
		public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
			try {
				if ("".equals(prefix)) {
					xmlWriter.setDefaultNamespace(uri);
				} else {
					xmlWriter.setPrefix(prefix, uri);
				}
			} catch (XMLStreamException e) {
				throw new SAXException(e);
			}
		}

	}

}
