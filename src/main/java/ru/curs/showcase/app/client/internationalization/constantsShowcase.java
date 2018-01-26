/**
 * 
 */
package ru.curs.showcase.app.client.internationalization;

import com.google.gwt.i18n.client.Messages;

/**
 * @author a.lugovtsov
 * 
 */

public interface constantsShowcase extends Messages {

	@DefaultMessage("Отменить")
	String selectorCancelText();

	@DefaultMessage(" Начинается с (Ctrl+B)")
	String selectorStartsWithText();

	@DefaultMessage("Строка не найдена")
	String multySelectorStringNotFound();

	@DefaultMessage("при получении данных таблицы с сервера")
	String gridErrorGetTable();

	@DefaultMessage("Экспорт в Excel")
	String gridExportToExcelCaption();

	@DefaultMessage("Таблица пуста. Экспорт в Excel выполнен не будет.")
	String gridExportToExcelEmptyTable();

	@DefaultMessage("Показывать не более")
	String pageGridShowAtMost();

	@DefaultMessage("Загрузка данных")
	String treeGridLoadingData();

	@DefaultMessage("Загружаемая запись с идентификатором")
	String treeGridLoadingDataDuplicateRecord1();

	@DefaultMessage("уже присутствует в гриде. Записи загружены не будут.")
	String treeGridLoadingDataDuplicateRecord2();

	@DefaultMessage("при получении данных XForm с сервера")
	String xformsErrorGetData();

	@DefaultMessage("при получении данных главной XForm с сервера")
	String xformsErrorGetMainData();

	@DefaultMessage("При загрузке файлов произошла ошибка")
	String uploadError();

	@DefaultMessage("Ошибка при сериализации параметров для Http-запроса плагина.")
	String jsGridSerializationError();

	@DefaultMessage("Ошибка при десериализации объекта")
	String jsGridDeserializationError();

	@DefaultMessage("Загрузка...")
	String jsGridLoadingMessage();

	@DefaultMessage("Нет записей")
	String jsGridNoRecordsMessage();

	@DefaultMessage("Экспорт в Excel потомков текущей записи")
	String jsTreeGridExportToExcelChilds();

	@DefaultMessage("Экспорт в Excel записей нулевого уровня")
	String jsTreeGridExportToExcel0Level();

	@DefaultMessage("Для выполнения частичного обновления, необходимо задать сортировку грида")
	String jsGridPartialUpdateNeedSorting();

	@DefaultMessage("список значений")
	String conditionListOfValues();

	@DefaultMessage("равно")
	String conditionEqual();

	@DefaultMessage("не равно")
	String conditionNotEqual();

	@DefaultMessage("меньше чем")
	String conditionLess();

	@DefaultMessage("меньше или равно")
	String conditionLessEqual();

	@DefaultMessage("больше чем")
	String conditionGreater();

	@DefaultMessage("больше или равно")
	String conditionGreaterEqual();

	@DefaultMessage("содержит")
	String conditionContain();

	@DefaultMessage("начинается ")
	String conditionStartWith();

	@DefaultMessage("заканчивается на")
	String conditionEndWith();

	@DefaultMessage("не содержит")
	String conditionNotContain();

	@DefaultMessage("не начинается с")
	String conditionNotStartWith();

	@DefaultMessage("не оканчивается на")
	String conditionNotEndWith();

	@DefaultMessage("пусто")
	String conditionIsEmpty();

	@DefaultMessage("Соответствие")
	String jsFilterLink();

	@DefaultMessage("Столбец")
	String jsFilterColumn();

	@DefaultMessage("Условие")
	String jsFilterCondition();

	@DefaultMessage("Значение")
	String jsFilterValue();

	@DefaultMessage("Выбрать значения")
	String jsFilterSelectValues();

	@DefaultMessage("Обновить условие фильтра")
	String jsFilterUpdate();

	@DefaultMessage("Добавить")
	String jsFilterAdd();

	@DefaultMessage("Удалить")
	String jsFilterDel();

	@DefaultMessage("Очистить")
	String jsFilterClear();

	@DefaultMessage("Отменить")
	String jsFilterCancel();

	@DefaultMessage("Сообщение")
	String okMessage();

	@DefaultMessage("Пожалуйста, подождите...Идет загрузка данных")
	String please_wait_data_are_loading();

	@DefaultMessage("Добро пожаловать")
	String welcome_tab_caption();

	@DefaultMessage("Ошибка")
	String error();

	@DefaultMessage("Пусто")
	String empty();

	@DefaultMessage("при получении данных графика с сервера")
	String error_of_chart_data_retrieving_from_server();

	@DefaultMessage("при получении данных навигатора с сервера")
	String error_of_navigator_data_retrieving_from_server();

	@DefaultMessage("при получении данных карты с сервера")
	String error_of_map_data_retrieving_from_server();

	@DefaultMessage("при получении данных внешнего плагина с сервера")
	String error_of_plugin_data_retrieving_from_server();

	@DefaultMessage("при получении текстовых данных с сервера")
	String error_of_webtext_data_retrieving_from_server();

	@DefaultMessage("при получении данных о текущем состоянии приложения")
	String error_of_server_current_state_retrieving_from_server();

	@DefaultMessage("при получении данных о главной странице приложения")
	String error_of_main_page_retrieving_from_server();

	@DefaultMessage("Ошибка при экспорте в Excel")
	String grid_error_caption_export_excel();

	@DefaultMessage("Ошибка при скачивании файла")
	String grid_error_caption_file_download();

	@DefaultMessage("Ошибка при построении карты")
	String error_of_map_painting();

	@DefaultMessage("Ошибка при построении внешнего плагина")
	String error_of_plugin_painting();

	@DefaultMessage("Ошибка при получение данных плагина")
	String error_of_plugin_getdata();

	@DefaultMessage("Ошибка при построении графика")
	String error_of_chart_painting();

	@DefaultMessage("Экспорт в Excel текущей страницы")
	String grid_caption_export_to_excel_current_page();

	@DefaultMessage("Экспорт в Excel всей таблицы")
	String grid_caption_export_to_excel_all();

	@DefaultMessage("Копировать в буфер обмена")
	String grid_caption_copy_to_clipboard();

	@DefaultMessage("Фильтр")
	String grid_caption_filter();

	@DefaultMessage("Добавить запись")
	String grid_caption_add_record();

	@DefaultMessage("Сохранить изменения")
	String grid_caption_save();

	@DefaultMessage("Отменить изменения")
	String grid_caption_revert();

	@DefaultMessage("Начат экспорт в Excel. Это может занять несколько минут. Кликните сюда, чтобы скрыть сообщение")
			String grid_message_popup_export_to_excel();

	@DefaultMessage("Загрузка файла")
	String xform_upload_caption();

	@DefaultMessage("Ошибка при сохранении данных XForms на сервере")
	String xform_save_data_error();

	@DefaultMessage("Ошибка при скачивании файл")
	String xforms_download_error();

	@DefaultMessage("Ошибка при PNG скачивании файла")
	String export_to_png_error();

	@DefaultMessage("Ошибка при загрузке файла(ов) на сервер")
	String xforms_upload_error();

	@DefaultMessage("Ошибка преобразования значения ширины навигатора")
	String transformation_navigator_width_error();

	@DefaultMessage("Ошибка преобразования значения высоты верхнего или нижнего колонтитула")
	String transformation_header_or_footer_width_error();

	@DefaultMessage("при выполнении действия на сервере")
	String error_in_server_activity();

}
