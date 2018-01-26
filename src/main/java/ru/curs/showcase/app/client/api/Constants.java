/**
 * 
 */
package ru.curs.showcase.app.client.api;

/**
 * 
 * Класс в котором определены все константы клиентской части приложения
 * Showcase.
 * 
 * @author anlug
 * 
 */
public final class Constants {

	private Constants() {
		super();
	}

	// public static final String PLEASE_WAIT_DATA_ARE_LOADING =
	// "Пожалуйста, подождите...Идет загрузка данных";
	public static final String CHART_DIV_ID_SUFFIX = "_chart_graph";
	public static final String CHART_LEGEND_DIV_ID_SUFFIX = "_chart_legend";
	public static final String MAP_DIV_ID_SUFFIX = "_map_graph";
	public static final String MAP_LEGEND_DIV_ID_SUFFIX = "_map_legend";
	public static final String PLUGIN_DIV_ID_SUFFIX = "_plugin";

	// public static final String PLEASE_WAIT_NAVIGATION_DATA_ARE_LOADING =
	// "Пожалуйста, подождите...Идет загрузка данных навигации";
	// public static final String PLEASE_WAIT_CHART_DATA_ARE_LOADING =
	// "Пожалуйста, подождите...Идет загрузка данных графика";
	// public static final String PLEASE_WAIT_NAVIGATION_DATA_ARE_REFRESHING =
	// "Пожалуйста, подождите...Идет обновление данных навигатора";
	// public static final String PLEASE_WAIT_MAP_DATA_ARE_LOADING =
	// "Пожалуйста, подождите...Идет загрузка данных карты";
	// public static final String PLEASE_WAIT_WEBTEXT_DATA_ARE_LOADING =
	// "Пожалуйста, подождите...Идет загрузка текстовых данных";
	// public static final String PLEASE_WAIT_GRID_1 =
	// "Пожалуйста, подождите... идет загрузка данных";
	// public static final String PLEASE_WAIT_GRID_2 =
	// "Пожалуйста, подождите... идет загрузка данных в соответствии с выбранным элементом";
	// public static final String PLEASE_WAIT_XFORM_1 =
	// "Пожалуйста, подождите... идет загрузка данных";
	// public static final String PLEASE_WAIT_XFORM_2 =
	// "Пожалуйста, подождите... идет загрузка данных в соответствии с выбранным элементом";
	// public static final String PLEASE_WAIT_XFORM_3 =
	// "Пожалуйста, подождите...";

	// public static final String WELCOME_TAB_CAPTION = "Добро пожаловать";
	// public static final String ERROR = "Ошибка";
	// public static final String EMPTY = "Пусто";

	// public static final String ERROR_OF_CHART_DATA_RETRIEVING_FROM_SERVER =
	// "при получении данных графика с сервера";
	// public static final String ERROR_OF_NAVIGATOR_DATA_RETRIEVING_FROM_SERVER
	// =
	// "при получении данных навигатора с сервера";
	// public static final String ERROR_OF_MAP_DATA_RETRIEVING_FROM_SERVER =
	// "при получении данных карты с сервера";
	// public static final String ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER =
	// "при получении данных внешнего плагина с сервера";
	// public static final String ERROR_OF_WEBTEXT_DATA_RETRIEVING_FROM_SERVER =
	// "при получении текстовых данных с сервера";
	// public static final String
	// ERROR_OF_SERVER_CURRENT_STATE_RETRIEVING_FROM_SERVER =
	// "при получении данных о текущем состоянии приложения";
	// public static final String ERROR_OF_MAIN_PAGE_RETRIEVING_FROM_SERVER =
	// "при получении данных о главной странице приложения";

	// public static final String GRID_ERROR_CAPTION_EXPORT_EXCEL =
	// "Ошибка при экспорте в Excel";
	// public static final String GRID_ERROR_CAPTION_FILE_DOWNLOAD =
	// "Ошибка при скачивании файла";
	// public static final String ERROR_OF_MAP_PAINTING =
	// "Ошибка при построении карты";
	// public static final String ERROR_OF_PLUGIN_PAINTING =
	// "Ошибка при построении внешнего плагина";
	// public static final String ERROR_OF_PLUGIN_GETDATA =
	// "Ошибка при получение данных плагина";
	// public static final String ERROR_OF_CHART_PAINTING =
	// "Ошибка при построении графика";
	// public static final String GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE =
	// "Экспорт в Excel текущей страницы";
	// public static final String GRID_CAPTION_EXPORT_TO_EXCEL_ALL =
	// "Экспорт в Excel всей таблицы";
	// public static final String GRID_CAPTION_COPY_TO_CLIPBOARD =
	// "Копировать в буфер обмена";
	//
	// public static final String GRID_MESSAGE_POPUP_EXPORT_TO_EXCEL =
	// "Начат экспорт в Excel. Это может занять несколько минут. Кликните сюда, чтобы скрыть сообщение";
	public static final int GRID_MESSAGE_POPUP_EXPORT_TO_EXCEL_DELAY = 3000;

	public static final String GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE =
		"resources/internal/ExportToExcelCurrentPage.png";
	public static final String GRID_IMAGE_EXPORT_TO_EXCEL_ALL =
		"resources/internal/ExportToExcelAll.png";
	public static final String GRID_IMAGE_COPY_TO_CLIPBOARD =
		"resources/internal/CopyToClipboard.png";
	public static final String GRID_IMAGE_FILTER = "resources/internal/filter.png";
	public static final String GRID_IMAGE_ADD_RECORD = "resources/internal/AddRecord.png";
	public static final String GRID_IMAGE_SAVE = "resources/internal/Save.png";
	public static final String GRID_IMAGE_REVERT = "resources/internal/Revert.png";

	public static final String IMAGE_FOR_WAITING_WINDOW = "resources/internal/progress.gif";
	public static final int GRID_SELECTION_DELAY = 900;

	/**
	 * Отступы в приложении для главной панели.
	 */
	public static final int SPACINGN = 10;

	/**
	 * Размеры левой панели splitLayoutPanel.
	 */
	public static final int SPLITLAYOUTPANELSIZEN = 300;

	/**
	 * Высота хедера и футера по умолчанию в пикселях.
	 */
	public static final int HEIGHTOFFOOTERANDBOTTOM = 50;

	// public static final String XFORM_UPLOAD_CAPTION = "Загрузка файла";

	// public static final String XFORM_SAVE_DATA_ERROR =
	// "Ошибка при сохранении данных XForms на сервере";

	// public static final String XFORMS_DOWNLOAD_ERROR =
	// "Ошибка при скачивании файла";
	//
	// public static final String EXPORT_TO_PNG_ERROR =
	// "Ошибка при PNG скачивании файла";
	//
	// public static final String XFORMS_UPLOAD_ERROR =
	// "Ошибка при загрузке файла(ов) на сервер";
	//
	// public static final String TRANSFORMATION_NAVIGATOR_WIDTH_ERROR =
	// "Ошибка преобразования значения ширины навигатора";
	// public static final String TRANSFORMATION_HEADER_OR_FOOTER_WIDTH_ERROR =
	// "Ошибка преобразования значения высоты верхнего или нижнего колонтитула";
	//
	// public static final String ERROR_IN_SERVER_ACTIVITY =
	// "при выполнении действия на сервере";

	// private final int widthN = 1200;
	// private final int heightN = 700;
	// private final int splitMinWidgetSizeN = 30;

}
