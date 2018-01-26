package ru.curs.showcase.core.event;

import java.io.InputStream;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Внутренняя фабрика для создания событий из XML документа. При описании
 * событий может использоваться характеристика "простой" (simple). Она означает
 * событие, для которого определен только Id1.
 * 
 * @author den
 * 
 * @param <E>
 *            - класс событий.
 */
public class EventFactory<E extends Event> extends GeneralXMLHelper {
	/**
	 * Сообщение об ошибке разбора XML.
	 */
	private static final String PROP_COL_ERROR_MES = "SQL XML с описанием событий";

	/**
	 * Текущее событие.
	 */
	private Event current;
	/**
	 * Результат.
	 */
	private final Collection<E> result = new ArrayList<>();
	/**
	 * Фабрика событий.
	 */
	private final ActionFactory actionFactory;

	/**
	 * Требуемый класс событий.
	 */
	private Class<? extends Event> eventClass;
	/**
	 * Атрибут, содержащий Id2.
	 */
	private String id2Tag;
	/**
	 * Префикс к событию, для которого определен Id2.
	 */
	private String id2Prefix;
	/**
	 * Атрибут, содержащий Id1.
	 */
	private String id1Tag;

	/**
	 * Обработчик для SAX парсера.
	 */
	private DefaultHandler saxHandler;

	/**
	 * Общий идентификатор id1.
	 */
	private ID generalId;

	/**
	 * Действие по умолчанию. Может быть задано вместе с набором событий.
	 */
	private Action defaultAction;

	/**
	 * Имя схемы для проверки XML с событиями.
	 */
	private String schemaName;

	/**
	 * Массив дополнительных обработчиков.
	 */
	private final List<SAXTagHandler> handlers = new ArrayList<>();

	public EventFactory(final Class<? extends Event> aEventClass, final CompositeContext context) {
		super();
		init(aEventClass);
		actionFactory = new ActionFactory(context);
	}

	/**
	 * Инициализировать для получения "простых" событий одной функцией.
	 * 
	 * @param aId1Tag
	 *            - тэг для Id1.
	 * @param aSchemaSource
	 *            - имя схемы.
	 */
	public void intiForGetSimpleEvents(final String aId1Tag) {
		id1Tag = aId1Tag;
		id2Tag = "faketag";
		id2Prefix = "fakeprefix";
		schemaName = null;
	}

	/**
	 * Инициализировать для получения событий для конкретного Id1.
	 * 
	 * @param aId2Tag
	 *            - атрибут с Id2.
	 * @param aId2Prefix
	 *            - префикс для атрибута, обозначающего, что Id2 задан.
	 * @param aSchemaName
	 *            - имя схемы.
	 */
	public void initForGetSubSetOfEvents(final String aId2Tag, final String aId2Prefix,
			final String aSchemaName) {
		id1Tag = null;
		id2Tag = aId2Tag;
		id2Prefix = aId2Prefix;
		schemaName = aSchemaName;
	}

	private void init(final Class<? extends Event> aEventClass) {
		eventClass = aEventClass;
		saxHandler = new DefaultHandler() {

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(EVENT_TAG)) {
					eventSTARTTAGHandler(attrs);
					return;
				}

				if (actionFactory.canHandleStartTag(qname)) {
					Action action =
						actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
					if (current != null) {
						current.setAction(action);
					} else {
						defaultAction = action;
					}
					return;
				}

				for (SAXTagHandler handler : handlers) {
					if (handler.canHandleStartTag(qname)) {
						handler.handleStartTag(namespaceURI, lname, qname, attrs);
						return;
					}
				}
			}

			@SuppressWarnings("unchecked")
			private void eventSTARTTAGHandler(final Attributes attrs) {
				Event event;
				String value;
				try {
					event = eventClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new ServerObjectCreateCloseException(e);
				}
				if (id1Tag == null) {
					event.setId1(generalId);
				} else {
					value = attrs.getValue(id1Tag);
					event.setId1(value);
				}
				value = attrs.getValue(NAME_TAG);

				for (InteractionType type : Arrays.asList(InteractionType.values())) {
					if (value.endsWith(type.toString().toLowerCase())) {
						event.setInteractionType(type);
						break;
					}
				}
				if (value.startsWith(id2Prefix)) {
					String id2 = attrs.getValue(id2Tag);
					event.setId2(id2);
				}
				current = event;
				result.add((E) event);
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(EVENT_TAG)) {
					current = null;
				}

				if (actionFactory.canHandleEndTag(qname)) {
					Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
					if (current != null) {
						current.setAction(action);
					} else {
						defaultAction = action;
					}
				}
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				actionFactory.handleCharacters(arg0, arg1, arg2);
			}
		};
	}

	/**
	 * Основная функция фабрики - получение коллекции событий из потока.
	 * 
	 * @param aGeneralId
	 *            - общий идентификатор 1 для возвращаемых событий.
	 * @param data
	 *            - строка с XML данными.
	 * @return - коллекция событий.
	 */
	public Collection<E> getSubSetOfEvents(final ID aGeneralId, final String data) {
		generalId = aGeneralId;
		InputStream xml = TextUtils.stringToStream(data);
		xml = XMLUtils.xsdValidateAppDataSafe(xml, schemaName);
		stdParseProc(xml);
		return result;
	}

	private void stdParseProc(final InputStream stream) {
		SimpleSAX sax = new SimpleSAX(stream, saxHandler, PROP_COL_ERROR_MES);
		sax.parse();
	}

	/**
	 * Основная функция фабрики - получение коллекции "простых" событий из
	 * потока. Проверка схемы при этом не производится: если settings и
	 * properties объединены - это не нужно.
	 * 
	 * @param stream
	 *            - поток с XML данными.
	 * @return - коллекция событий.
	 */
	public Collection<E> getSimpleEvents(final InputStream stream) {
		stdParseProc(stream);
		return result;
	}

	public Action getDefaultAction() {
		return defaultAction;
	}

	/**
	 * Инициализирует фабрику для считывания событий с конкретным Id1 и без Id2.
	 * 
	 * @param aSchemaName
	 *            - имя схемы.
	 */
	public void initForGetSimpleSubSetOfEvents(final String aSchemaName) {
		id1Tag = null;
		id2Tag = "faketag";
		id2Prefix = "fakeprefix";
		schemaName = aSchemaName;
	}

	/**
	 * Добавляет обработчик в фабрику.
	 * 
	 * @param aColorHandler
	 *            - обработчик.
	 */
	public void addHandler(final SAXTagHandler aColorHandler) {
		handlers.add(aColorHandler);
	}
}