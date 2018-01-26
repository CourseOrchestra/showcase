package ru.curs.showcase.util.xml;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.NamedElement;
import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Базовый класс парсера, содержащий константы для разбора поступающих в систему
 * XML документов.
 * 
 * @author den
 * 
 */
public abstract class GeneralXMLHelper {
	protected static final String MAIN_CONTEXT_TAG = "main_context";
	protected static final String ADD_CONTEXT_TAG = "add_context";

	protected static final String NAME_TAG = "name";
	public static final String ID_TAG = "id";

	public static final String NAVIGATOR_TAG = "navigator";
	protected static final String EL_ON_LEVEL_NODE_NAME = "level";
	protected static final String GROUP_TAG = "group";
	protected static final String IMAGE_ATTR_NAME = "icon";

	protected static final String ACTION_TAG = "action";
	protected static final String DP_ID_ATTR_NAME = "type";

	public static final String DP_TAG = "datapanel";
	protected static final String TAB_TAG = "tab";
	protected static final String ELEMENT_TAG = "element";
	protected static final String DP_CACHING_TAG = "dataPanelCaching";
	/**
	 * Стандартный тэг для типа.
	 */
	public static final String TYPE_TAG = "type";
	public static final String SUBTYPE_TAG = "subtype";
	public static final String CAPTION_TAG = "caption";
	public static final String CLASS_STYLE_TAG = "class_style";

	protected static final String PROC_ATTR_NAME = "proc";
	protected static final String TRANSFORM_ATTR_NAME = "transform";
	protected static final String HIDE_ON_LOAD_TAG = "hideOnLoad";

	public static final String HEADER_TAG = "header";
	public static final String FOOTER_TAG = "footer";

	/**
	 * Тэг события.
	 */
	protected static final String EVENT_TAG = "event";
	/**
	 * Базовый тэг для ширины.
	 */
	public static final String WIDTH_TAG = "width";
	protected static final String HEIGHT_TAG = "height";

	protected static final String VISIBLE_TAG = "visible";

	protected static final String PROPS_TAG = "properties";
	protected static final String FLIP_TAG = "flip";

	protected static final String TEMPLATE_TAG = "template";

	protected static final String LEGEND_TAG = "legend";

	protected static final String COLOR_TAG = "color";

	protected static final String VALUE_TAG = "value";

	protected static final String PAGESIZE_TAG = "pagesize";

	protected static final String HINT_FORMAT_TAG = "hintFormat";

	protected static final String REFRESH_TAG = "refresh";

	/**
	 * Имя атрибута с идентификатором ссылки, уникальным в пределах элемента.
	 */
	protected static final String LINK_ID_TAG = "linkId";

	protected static final String SCHEMA_TAG = "schema";

	protected static final String MODAL_WINDOW_TAG = "modalwindow";

	protected static final String FILENAME_TAG = "filename";

	protected static final String FILE_TAG = "file";

	protected static final String CACHE_DATA_TAG = "cacheData";

	protected static final String SERVER_TAG = "server";

	protected static final String ACTIVITY_TAG = "activity";

	protected static final String ERROR_MES_COL = "error_mes";

	protected static final String FIRE_GENERAL_AND_CONCRETE_EVENTS_TAG =
		"fireGeneralAndConcreteEvents";

	public static final String SOURCE_TAG = "source";

	public static final String TOTAL_COUNT_TAG = "totalCount";

	protected static final String RELATED_TAG = "related";

	protected static final String ELEMENT_ID = "${elementId}";

	protected static final String FILTER_TAG = "filterinfo";

	public static final String USERNAME_TAG = "username";

	public static final String REQUEST_ID_TAG = "requestId";

	public static final String COMMAND_NAME_TAG = "commandName";

	/**
	 * Имя колонки или строки с XML документом, содержащим обработчики.
	 */
	public static final String PROPERTIES_SQL_TAG = "~~" + PROPS_TAG;
	public static final String ID_SQL_TAG = "~~" + ID_TAG;

	public static final String XML_DATASET_TAG = "records";
	public static final String RECORD_TAG = "rec";
	public static final String SQLTYPE_ATTR = "sqltype";

	/**
	 * Установка базовых свойств - id и name - у элемента.
	 * 
	 * @param el
	 *            - элемент для установки
	 * @param attrs
	 *            - XML Attributes.
	 */
	protected void setupBaseProps(final NamedElement el, final Attributes attrs) {
		el.setId(attrs.getValue(ID_TAG));
		/**
		 * Метод UserDataUtils.modifyVariables() переводит строку-аргумент с
		 * помощью Gettext.
		 */
		el.setName(UserDataUtils.modifyVariables(attrs.getValue(NAME_TAG)));
		// el.setName(attrs.getValue(NAME_TAG));
	}
}
