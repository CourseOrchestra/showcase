package ru.curs.showcase.core.primelements.navigator;

import java.io.InputStream;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.event.ActionFactory;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorFactory extends StartTagSAXHandler {
	private static final String WRONG_ACTION_IN_NAVIGATOR_ERROR =
		"Некорректное описание действия в навигаторе: ";
	private static final String SELECT_ON_LOAD_TAG = "selectOnLoad";
	private static final String XML_ERROR_MES = "описание навигатора";
	private static final String GRP_ICONS_DIR_PARAM_NAME = "navigator.icons.dir.name";
	private static final String GRP_DEF_ICON_PARAM_NAME = "navigator.def.icon.name";
	private static final String GRP_HEIGHT_PARAM = "navigator.group.height";

	private static final String NAVIGATOR = "NAVIGATOR";

	/**
	 * Стартовые тэги, которые будут обработаны.
	 */
	private static final String[] START_TAGS = { NAVIGATOR_TAG, GROUP_TAG };

	/**
	 * Путь к каталогу для иконок.
	 */
	private final String groupIconsDir;
	/**
	 * Имя иконки по умолчанию.
	 */
	private final String groupDefIcon;

	/**
	 * Высота группы навигатора.
	 */
	private final Double groupHeight;

	/**
	 * Конструируемый навигатор.
	 */
	private Navigator result;
	/**
	 * Текущая группа навигатора.
	 */
	private NavigatorGroup currentGroup;
	/**
	 * Фабрика событий.
	 */
	private final ActionFactory actionFactory;

	/**
	 * Стек текущих элементов навигатора.
	 */
	private final LinkedList<NavigatorElement> currentElStack = new LinkedList<NavigatorElement>();

	public NavigatorFactory(final CompositeContext aCallContext) {

		super();

		Date dt1 = new Date();

		groupIconsDir = UserDataUtils.getRequiredProp(GRP_ICONS_DIR_PARAM_NAME);
		groupDefIcon = UserDataUtils.getRequiredProp(GRP_DEF_ICON_PARAM_NAME);
		groupHeight =
			UserDataUtils.getOptionalProp(GRP_HEIGHT_PARAM) != null ? Double
					.parseDouble(UserDataUtils.getOptionalProp(GRP_HEIGHT_PARAM).trim()) : 4;
		actionFactory = new ActionFactory(aCallContext);

		Date dt2 = new Date();
		LoggerHelper.profileToLog("Navigator. Создание фабрики.", dt1, dt2, NAVIGATOR, "");

	}

	public void levelSTARTTAGHandler(final Attributes attrs) {
		NavigatorElement el = new NavigatorElement();
		setupBaseProps(el, attrs);
		if (attrs.getIndex(SELECT_ON_LOAD_TAG) > -1) {
			if ("true".equalsIgnoreCase(attrs.getValue(SELECT_ON_LOAD_TAG))) {
				result.setAutoSelectElement(el);
			}
		}
		if (currentElStack.isEmpty()) {
			currentGroup.getElements().add(el);
		} else {
			currentElStack.getLast().getElements().add(el);
		}
		currentElStack.add(el);
	}

	public void groupSTARTTAGHandler(final Attributes attrs) {
		currentGroup = new NavigatorGroup();
		setupBaseProps(currentGroup, attrs);
		if (attrs.getIndex(IMAGE_ATTR_NAME) > -1) {
			setupImageId(attrs.getValue(IMAGE_ATTR_NAME));
		} else {
			setupImageId(groupDefIcon);
		}
		result.getGroups().add(currentGroup);
	}

	private void setupImageId(final String imageFile) {
		currentGroup.setImageId(String.format("%s/%s", groupIconsDir, imageFile));
	}

	/**
	 * Класс, возврат которого является признаком того, что нужно продолжать
	 * искать требуемый обработчик тэга.
	 * 
	 * @author den
	 * 
	 */
	private class ContinueObject {

	}

	public Object navigatorSTARTTAGHandler(final Attributes attrs) {
		if (result == null) {
			result = new Navigator();
			if (attrs.getIndex(HIDE_ON_LOAD_TAG) > -1) {
				result.setHideOnLoad(Boolean.parseBoolean(attrs.getValue(HIDE_ON_LOAD_TAG)));
			}
			if (attrs.getIndex(WIDTH_TAG) > -1) {
				result.setWidth(attrs.getValue(WIDTH_TAG));
			}
			result.setGroupHeight(groupHeight);
			return null;
		}
		return new ContinueObject();
	}

	/**
	 * Функция построения навигатора из файла, содержащего XML данные.
	 * 
	 * @param file
	 *            - файл с данными.
	 * @return - навигатор.
	 */
	public Navigator fromStream(final DataFile<InputStream> file) {

		Date dt1 = new Date();
		XMLUtils.xsdValidateAppDataSafe(file, "navigator.xsd");
		Date dt2 = new Date();
		LoggerHelper.profileToLog("Navigator. Валидация потока навигатора.", dt1, dt2, NAVIGATOR,
				"");

		dt1 = new Date();

		DefaultHandler myHandler = new DefaultHandler() {
			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				handleStartTag(namespaceURI, lname, qname, attrs);
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				handleEndTag(namespaceURI, lname, qname);
			}

			@Override
			public void characters(final char[] arg0, final int arg1, final int arg2) {
				handleCharacters(arg0, arg1, arg2);
			}
		};

		SimpleSAX sax = new SimpleSAX(file.getData(), myHandler, XML_ERROR_MES);
		sax.parse();

		dt2 = new Date();
		LoggerHelper.profileToLog("Navigator. Формирование навигатора из потока.", dt1, dt2,
				NAVIGATOR, "");

		postProcess();
		result.setWelcomeTabCaption(UserDataUtils
				.getOptionalProp(UserDataUtils.INDEX_WELCOME_TAB_CAPTION));
		return result;
	}

	private void postProcess() {

		Date dt1 = new Date();
		for (NavigatorGroup group : result.getGroups()) {
			checkNavigatorElements(group.getElements());
		}
		Date dt2 = new Date();
		LoggerHelper.profileToLog("Navigator. Валидация датапанелей навигатора.", dt1, dt2,
				NAVIGATOR, "");

	}

	private void checkNavigatorElements(final List<NavigatorElement> elements) {
		for (NavigatorElement element : elements) {
			determineAndCheckAction(element.getAction());
			checkNavigatorElements(element.getElements());
		}
	}

	private void determineAndCheckAction(final Action action) {
		if (action != null) {
			action.determineState();
			if (!action.isCorrect()) {
				throw new IncorrectElementException(WRONG_ACTION_IN_NAVIGATOR_ERROR, action);
			}
		}
	}

	@Override
	protected String[] getStartTags() {
		return START_TAGS;
	}

	@Override
	public Object handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		if (canHandleStartTag(qname)) {
			if (super.handleStartTag(namespaceURI, lname, qname, attrs) == null) {
				return null;
			}
		}
		if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
			levelSTARTTAGHandler(attrs);
			return null;
		}
		if (actionFactory.canHandleStartTag(qname)) {
			Action action = actionFactory.handleStartTag(namespaceURI, lname, qname, attrs);
			NavigatorElement cur = currentElStack.getLast();
			cur.setAction(action);
		}

		return null;
	}

	@Override
	public Object handleEndTag(final String namespaceURI, final String lname, final String qname) {
		if (qname.startsWith(EL_ON_LEVEL_NODE_NAME)) {
			currentElStack.removeLast();
		}

		if (actionFactory.canHandleEndTag(qname)) {
			Action action = actionFactory.handleEndTag(namespaceURI, lname, qname);
			NavigatorElement cur = currentElStack.getLast();
			cur.setAction(action);
		}

		return null;
	}

	@Override
	public void handleCharacters(final char[] arg0, final int arg1, final int arg2) {
		actionFactory.handleCharacters(arg0, arg1, arg2);
	}

}
