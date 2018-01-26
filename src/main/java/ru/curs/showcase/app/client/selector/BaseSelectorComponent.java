package ru.curs.showcase.app.client.selector;

import java.util.*;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.Range;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.common.*;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.panels.DialogBoxWithCaptionButton;

/**
 * Базовый класс компоненты выбора из больших списков (единичного и
 * множественного).
 */
public abstract class BaseSelectorComponent extends DialogBoxWithCaptionButton {

	protected static final int DEF_SPACING = 10;

	protected static final String PROC100 = "100%";

	protected static final String CLEAR_BUTTON_HEIGHT = "16px";
	protected static final String CLEAR_BUTTON_WIDTH = "16px";

	protected static final String DBL_CLICK = "dblclick";
	protected static final String KEY_DOWN = "keydown";

	protected static final String MOUSE_WHEEL = "mousewheel";
	protected static final String DOM_MOUSE_SCROLL = "DOMMouseScroll";

	protected static final int MOUSE_SCROLL_UNITS = 10;

	/**
	 * Интерфейс, посредством которого компонента оповещает о завершении
	 * процесса выбора (то есть своем закрытии).
	 */
	public interface SelectorListener {
		/**
		 * Вызывается компонентой при закрытии как по кнопке ОК, так и по кнопке
		 * Отменить.
		 * 
		 * @param selector
		 *            Компонента-селектор.
		 */
		void onSelectionComplete(BaseSelectorComponent selector);
	}

	/**
	 * Интерфейс, позволяющий обрабатывать ошибки вызова сервиса данных.
	 */
	public interface ErrorHandler {
		/**
		 * Вызывается при взникновении ошибки во время вызова сервиса данных.
		 * 
		 * @param throwable
		 *            исключение
		 */
		void onDataServiceFailure(Throwable throwable);
	}

	/**
	 * Класс настроек компоненты.
	 */
	public static class Options {

		/**
		 * Значение по умолчанию.
		 */
		private static final int DEF_VISIBLERECORDCOUNT = 15;
		/**
		 * Значение по умолчанию.
		 */
		private static final int DEF_VISIBLEBOTTOMBLANKCOUNT = 5;
		/**
		 * Значение по умолчанию.
		 */
		private static final int DEF_DATAREQUESTDELAY = 430;
		/**
		 * Значение по умолчанию.
		 */
		private static final int DEF_SCROLLREQUESTDELAY = 100;

		/**
		 * visibleRecordCount.
		 * 
		 */
		private int visibleRecordCount = DEF_VISIBLERECORDCOUNT;
		/**
		 * visibleBottomBlankCount.
		 * 
		 */
		private int visibleBottomBlankCount = DEF_VISIBLEBOTTOMBLANKCOUNT;
		/**
		 * startsWithChecked.
		 * 
		 */
		private boolean startsWithChecked = true;
		/**
		 * dataRequestDelay.
		 * 
		 */
		private int dataRequestDelay = DEF_DATAREQUESTDELAY;
		/**
		 * scrollRequestDelay.
		 * 
		 */
		private int scrollRequestDelay = DEF_SCROLLREQUESTDELAY;

		/**
		 * clearImage.
		 * 
		 */
		private Image clearImage;
		/**
		 * okText.
		 * 
		 */
		private String okText = "OK";
		/**
		 * cancelText.
		 * 
		 */
		// private String cancelText =
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .selectorCancelText();
		private String cancelText =
			// AppCurrContext.getInstance().getBundleMap().get("selectorCancelText");
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "Cancel");
		/**
		 * startsWithText.
		 * 
		 */
		private String startsWithText =
			// AppCurrContext.getInstance().getBundleMap().get("selectorStartsWithText");
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"Starts with (Ctrl+B)");
		// private String startsWithText = AppCurrContext.getInstance()
		// .getInternationalizedMessages().selectorStartsWithText();

		/**
		 * hideStartsWith.
		 * 
		 */
		private boolean hideStartsWith = false;

		/**
		 * dataWidth.
		 * 
		 */
		private String dataWidth = "400px";
		/**
		 * dataHeight.
		 * 
		 */
		private String dataHeight = "250px";
		/**
		 * selectedDataWidth.
		 * 
		 */
		private String selectedDataWidth = "312px";

		private boolean selectedFirst = false;

		/**
		 * errorHandler.
		 * 
		 */
		private ErrorHandler errorHandler;
		/**
		 * selectorListener.
		 * 
		 */
		private SelectorListener selectorListener;

		/**
		 * manualSearch.
		 * 
		 */
		private boolean manualSearch = false;

		int getVisibleRecordCount() {
			return visibleRecordCount;
		}

		/**
		 * getvisibleBottomBlankCount.
		 * 
		 * @return visibleBottomBlankCount
		 * 
		 */
		int getvisibleBottomBlankCount() {
			return visibleBottomBlankCount;
		}

		String getDataWidth() {
			return dataWidth;
		}

		String getDataHeight() {
			return dataHeight;
		}

		public String getSelectedDataWidth() {
			return selectedDataWidth;
		}

		public boolean getSelectedFirst() {
			return selectedFirst;
		}

		int getDataRequestDelay() {
			return dataRequestDelay;
		}

		int getScrollRequestDelay() {
			return scrollRequestDelay;
		}

		boolean getManualSearch() {
			return manualSearch;
		}

		/**
		 * Количество отображаемых записей. На самом деле означает количество
		 * записей, которые будут запрашиваться за один запрос с сервера.
		 * Количество же отображаемых записей определяется высотой области
		 * списка.
		 * 
		 * @param value
		 *            новое количество отображаемых записей
		 * 
		 * @return Options
		 */
		public Options visibleRecordCount(final int value) {
			visibleRecordCount = value;
			return this;
		}

		/**
		 * Количество пустых строчек при прокрутке в самый конец набора данных.
		 * 
		 * @param value
		 *            новое количество пустых строчек
		 * 
		 * @return Options
		 */
		public Options visibleBottomBlankCount(final int value) {
			visibleBottomBlankCount = value;
			return this;
		}

		/**
		 * Задает значение флага "Начинается с" при запуске окна.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options startsWithChecked(final boolean value) {
			startsWithChecked = value;
			return this;
		}

		/**
		 * Задает "Поиск по кнопке" при запуске окна.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options manualSearch(final boolean value) {
			manualSearch = value;
			return this;
		}

		/**
		 * Задержка (мс) в выполнении запроса к серверу с момента последнего
		 * изменения строки поиска.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options dataRequestDelay(final int value) {
			dataRequestDelay = value;
			return this;
		}

		/**
		 * Задержка (мс) в выполнении запроса к серверу во время прокрутки
		 * (работает аналогично dataRequestDelay).
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options scrollRequestDelay(final int value) {
			scrollRequestDelay = value;
			return this;
		}

		/**
		 * Изображение на кнопке очистки сроки поиска.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options clearImage(final Image value) {
			clearImage = value;
			return this;
		}

		/**
		 * Текст на кнопке ОК.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options okText(final String value) {
			okText = value;
			return this;
		}

		/**
		 * Текст на кнопке Отменить.
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options cancelText(final String value) {
			cancelText = value;
			return this;
		}

		/**
		 * Текст "Начинается с".
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options startsWithText(final String value) {
			startsWithText = value;
			return this;
		}

		/**
		 * Признак того, что надо скрыть "Начинается с".
		 * 
		 * @param value
		 *            новое значение
		 * 
		 * @return Options
		 */
		public Options hideStartsWith(final boolean value) {
			hideStartsWith = value;
			return this;

		}

		/**
		 * Минимальная ширина области данных (списка).
		 * 
		 * @param width
		 *            ширина
		 * 
		 * @return Options
		 */
		public Options dataWidth(final String width) {
			dataWidth = width;
			return this;
		}

		/**
		 * Высота области данных (списка).
		 * 
		 * @param height
		 *            высота
		 * 
		 * @return Options
		 */
		public Options dataHeight(final String height) {
			dataHeight = height;
			return this;
		}

		/**
		 * Мультиселектор. Минимальная ширина области выбранных данных (списка).
		 * 
		 * @param selectedWidth
		 *            ширина
		 * 
		 * @return Options
		 */
		public Options selectedDataWidth(final String selectedWidth) {
			selectedDataWidth = selectedWidth;
			return this;
		}

		public Options selectedFirst(final boolean value) {
			selectedFirst = value;
			return this;
		}

		/**
		 * Устанавливает обработчик ошибок.
		 * 
		 * @param handler
		 *            обработчик ошибок
		 * 
		 * @return Options
		 */
		public Options errorHandler(final ErrorHandler handler) {
			errorHandler = handler;
			return this;
		}

		/**
		 * Устанавливает обработчик закрытия окна.
		 * 
		 * @param listener
		 *            обработчик
		 * 
		 * @return Options
		 */
		public Options selectorListener(final SelectorListener listener) {
			selectorListener = listener;
			return this;
		}

	}

	/**
	 * interface Resources.
	 * 
	 */
	interface Resources extends ClientBundle {
		ImageResource clearImage();

		ImageResource transparentImage();

		ImageResource findImage();
	}

	private static Resources resources;

	/**
	 * srv.
	 */
	private final SelectorDataServiceAsync srv;

	protected SelectorDataServiceAsync getSelectorDataService() {
		return srv;
	}

	/**
	 * options.
	 */
	private final Options options;

	private final TextBox searchString = new TextBox();
	private final CheckBox startsWithCheckbox = new CheckBox();
	private final SimplePanel cellholder = new SimplePanel();
	private CellList<DataRecord> celllist;
	private final ScrollPanel scroll = new ScrollPanel();
	private final Label debug = new Label();

	private ScrollController scrollctl;
	private SearchController searchctl;

	/**
	 * req.
	 */
	private DataRequest req;

	protected DataRequest getDataRequest() {
		return req;
	}

	/**
	 * skipStartsWithChange.
	 */
	private boolean skipStartsWithChange = false;

	/**
	 * errorHandler.
	 */
	private ErrorHandler errorHandler;

	protected ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * selectorListener.
	 */
	private SelectorListener selectorListener;

	/**
	 * isOK.
	 */
	private boolean isOK = false;

	/**
	 * okAction.
	 */
	private AbstractAction okAction;
	/**
	 * cancelAction.
	 */
	private Action cancelAction;

	/**
	 * Конструктор с опциями по умолчанию.
	 * 
	 * @param srv1
	 *            сервис данных
	 * @param title
	 *            заголовок окна
	 */
	public BaseSelectorComponent(final SelectorDataServiceAsync srv1, final String title) {
		this(srv1, title, new Options());
	}

	/**
	 * Конструктор с опциями в параметре.
	 * 
	 * @param srv1
	 *            сервис данных
	 * @param title
	 *            заголовок окна
	 * @param options1
	 *            опции
	 */
	public BaseSelectorComponent(final SelectorDataServiceAsync srv1, final String title,
			final Options options1) {
		super(false, true, title);
		this.srv = srv1;
		this.options = options1;

		final int zIndex = 102;
		getElement().getStyle().setZIndex(zIndex);
	}

	/**
	 * Возвращает числовое значение размера, извлеченное из переданной строки.
	 * 
	 * @param value
	 *            - строка с размером.
	 * @return - числовое значение.
	 */
	protected static Integer getIntSizeValue(final String value) {
		Integer intValue = null;
		String strValue;
		if (value.indexOf("px") > -1) {
			strValue = value.substring(0, value.indexOf("px"));
			intValue = Integer.valueOf(strValue);
		}
		return intValue;
	}

	protected void setupHandlers() {
		setSelectorListener(options.selectorListener);
		setErrorHandler(options.errorHandler);
	}

	protected void setupStartsWithCheckbox() {

		startsWithCheckbox.setValue(options.startsWithChecked);
		if (options.hideStartsWith) {
			startsWithCheckbox.setText("");
			startsWithCheckbox.setVisible(false);
		} else {
			startsWithCheckbox.setText(options.startsWithText);
		}

		startsWithCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Boolean> event) {
				if (skipStartsWithChange) {
					return;
				}
				requestData(getFirstRecord(), true);
			}
		});
	}

	protected void setupCelllist() {
		celllist.setPageSize(options.visibleRecordCount);
		celllist.setVisibleRange(0, options.visibleRecordCount);
		celllist.setWidth(PROC100);
		celllist.setKeyboardPagingPolicy(
				HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);
	}

	protected void setupCellholder() {
		cellholder.setWidget(celllist);
		cellholder.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		cellholder.setHeight(options.dataHeight);
	}

	protected void setupScrollController() {
		scrollctl = new ScrollController(scroll, options, getResources());
		scrollctl.setDebug(debug);
		scrollctl.setScrollListener(new ScrollController.ScrollListener() {
			@Override
			public void onPositionChange(final int startRecord) {
				requestData(startRecord, false);
			}
		});
	}

	protected void setupSearchController() {
		searchctl = new SearchController(searchString, options);
		searchctl.setDebug(debug);
		searchctl.setSearchListener(new SearchController.SearchListener() {
			@Override
			public void onSearchTextChange(final String searchText) {
				if ((req == null) || options.manualSearch) {
					return;
				}
				req.setCurValue(searchText);
				requestData(0, true);
			}
		});
	}

	protected void setupActions() {
		okAction = okAction();
		cancelAction = cancelAction();
	}

	/**
	 * Устанавливает обработчик ошибок.
	 * 
	 * @param errorHandler1
	 *            обработчик
	 */
	public final void setErrorHandler(final ErrorHandler errorHandler1) {
		this.errorHandler = errorHandler1;
	}

	/**
	 * Устанавливает обработчик завершения процесса выбора.
	 * 
	 * @param selectorListener1
	 *            обработчик
	 */
	public final void setSelectorListener(final SelectorListener selectorListener1) {
		this.selectorListener = selectorListener1;
	}

	/**
	 * Инициализация первоначальных параметров и выполнение первого запроса к
	 * серверу. Для того, чтобы запрос не выполнялcя, следует не задавать (то
	 * есть отставить null) значение curValue.
	 * 
	 * @param params
	 *            параметры
	 * @param procName
	 *            имя процедуры
	 * @param curValue
	 *            значение для строки поиска
	 */
	public void initData(final String params, final String procName, final String curValue,
			final SelectorAdditionalData addData) {
		searchString.setText(curValue);
		searchctl.setText(curValue);

		req = new DataRequest();
		req.setParams(params);
		req.setProcName(procName);
		req.setCurValue(curValue);

		req.setAddData(addData);

		requestData(0, true);

		searchString.setFocus(true);
		searchString.selectAll();

	}

	public abstract JavaScriptObject getSelectedAsJsObject();

	/**
	 * Была ли нажата кнопка OK.
	 * 
	 * @return true если была нажата кнопка OK.
	 */
	public boolean isOK() {
		return isOK;
	}

	private void eventExtPreventDefault(final Event.NativePreviewEvent event) {
		event.consume();
		event.getNativeEvent().preventDefault();
		event.getNativeEvent().stopPropagation();
	}

	private boolean isEventStartsWithCheckbox(final Event.NativePreviewEvent event) {
		return event.getNativeEvent().getCtrlKey() && ((event.getNativeEvent().getKeyCode() == 'B')
				|| (event.getNativeEvent().getKeyCode() == 'b'));
	}

	@Override
	protected void onPreviewNativeEvent(final Event.NativePreviewEvent event) {
		if (Event.ONKEYUP == event.getTypeInt()) {
			if (isEventStartsWithCheckbox(event)) {
				try {
					skipStartsWithChange = true;
					startsWithCheckbox.setValue(!startsWithCheckbox.getValue());
				} finally {
					skipStartsWithChange = false;
				}
				eventExtPreventDefault(event);

				requestData(getFirstRecord(), true);
			} else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
				cancelAction.execute();
			}
		} else if ((Event.ONKEYPRESS == event.getTypeInt()) && isEventStartsWithCheckbox(event)) {
			eventExtPreventDefault(event);
		} else if ((Event.ONKEYDOWN == event.getTypeInt()) && isEventStartsWithCheckbox(event)) {
			eventExtPreventDefault(event);
		}
		super.onPreviewNativeEvent(event);
	}

	private void debugm(final String msg) {
		debug.setText(debug.getText() + " " + msg);
	}

	private void requestData(final int aFirstRecord, final boolean reset) {
		debugm("1");
		if (req == null /*
						 * || req.getCurValue() == null Если очистили поле ввода
						 * -- должно выводиться всё!
						 */) {
			return;
		}
		debugm("2");

		int firstRecord = aFirstRecord;
		if (firstRecord < 0) {
			firstRecord = 0;
		}

		req.setStartsWith(isStartsWith());
		req.setFirstRecord(firstRecord);
		req.setRecordCount(getRecordCount());

		srv.getSelectorData(req, new AsyncCallback<DataSet>() {
			@Override
			public void onFailure(final Throwable throwable) {
				if (errorHandler != null) {
					errorHandler.onDataServiceFailure(throwable);
				}
			}

			@Override
			public void onSuccess(final DataSet dataSet) {
				List<DataRecord> records = getSafeRecords(dataSet);

				debugm("3:" + dataSet.getFirstRecord() + "," + dataSet.getTotalCount() + ","
						+ records.size());

				celllist.setVisibleRangeAndClearData(new Range(0, records.size()), false);
				celllist.setRowData(0, records);
				scrollctl.update(dataSet.getTotalCount(), reset);

				if (reset && options.selectedFirst) {
					clearSelectedDataRecord();
					if (celllist.getVisibleItemCount() > 0) {
						celllist.getSelectionModel().setSelected(celllist.getVisibleItem(0), true);
					}
				}

				if (dataSet.getOkMessage() != null) {
					showMessage(dataSet.getOkMessage());
				}

			}

		});
	}

	private void showMessage(final UserMessage um) {

		String textMessage = um.getText();
		if ((textMessage == null) || textMessage.isEmpty()) {
			return;
		}

		MessageType typeMessage = um.getType();
		if (typeMessage == null) {
			typeMessage = MessageType.INFO;
		}

		String captionMessage = um.getCaption();
		if (captionMessage == null) {
			captionMessage =
				// AppCurrContext.getInstance().getBundleMap().get("okMessage");
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Message");
		}

		String subtypeMessage = um.getSubtype();

		MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage, false,
				subtypeMessage);

	}

	protected List<DataRecord> getSafeRecords(final DataSet ds) {
		if (ds == null || ds.getRecords() == null) {
			return Collections.emptyList();
		}
		return ds.getRecords();
	}

	protected abstract void clearSelectedDataRecord();

	private boolean isStartsWith() {
		return startsWithCheckbox.getValue();
	}

	private int getRecordCount() {
		return options.visibleRecordCount;
	}

	private int getFirstRecord() {
		return req == null ? 0 : req.getFirstRecord();
	}

	protected Action clearAction() {
		AbstractAction a = new AbstractAction("Clear") {
			@Override
			protected void perform() {
				searchctl.clear();
				req.setCurValue("");
				requestData(0, true);
			}
		};
		Image clearImage = options.clearImage != null ? options.clearImage
				: new Image(getResources().clearImage());
		a.setImage(clearImage);
		return a;
	}

	protected Action manualSearchAction() {
		AbstractAction a = new AbstractAction("manualSearch") {
			@Override
			protected void perform() {
				req.setCurValue(searchString.getText());
				requestData(0, true);
			}
		};

		Image findImage = new Image(getResources().findImage());
		a.setImage(findImage);
		return a;
	}

	private AbstractAction okAction() {
		return new AbstractAction(options.okText) {
			@Override
			protected void perform() {
				isOK = true;
				setSelectedObject();
				closeSelector();
			}
		};
	}

	protected abstract void setSelectedObject();

	private Action cancelAction() {
		return new AbstractAction(options.cancelText) {
			@Override
			protected void perform() {
				closeSelector();
			}
		};
	}

	private void closeSelector() {
		BaseSelectorComponent.this.hide();
		if (selectorListener != null) {
			selectorListener.onSelectionComplete(BaseSelectorComponent.this);
		}
	}

	protected void renderDataCell(final Cell.Context context, final DataRecord dataRecord,
			final SafeHtmlBuilder safeHtmlBuilder) {
		String name = dataRecord.getName();
		safeHtmlBuilder
				.appendHtmlConstant("<div style=\"white-space: nowrap;width: 500px\" title=\""
						+ name.trim().replace("\"", "'") + "\">");
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == ' ') {
				safeHtmlBuilder.appendHtmlConstant("&nbsp;");
			} else {
				break;
			}
		}
		safeHtmlBuilder.appendEscaped(name.trim());
		safeHtmlBuilder.appendHtmlConstant("</div>");
	}

	protected JavaScriptObject getDataRecordAsJsObject(final DataRecord record) {
		SelectedJsObject result = SelectedJsObject.create();
		if (record != null) {
			result.setId(record.getId());
			result.setName(record.getName());
			if (record.getParameters() != null) {
				for (Map.Entry<String, String> e : record.getParameters().entrySet()) {
					result.setParam(e.getKey(), e.getValue() == null ? "" : e.getValue());
				}
			}
		}
		return result;
	}

	protected static Resources getResources() {
		if (resources == null) {
			resources = GWT.create(Resources.class);
		}
		return resources;
	}

	protected TextBox getSearchString() {
		return searchString;
	}

	protected CheckBox getStartsWithCheckbox() {
		return startsWithCheckbox;
	}

	protected SimplePanel getCellholder() {
		return cellholder;
	}

	protected CellList<DataRecord> getCelllist() {
		return celllist;
	}

	protected void setCelllist(final CellList<DataRecord> celllist1) {
		this.celllist = celllist1;
	}

	protected ScrollPanel getScroll() {
		return scroll;
	}

	protected AbstractAction getOkAction() {
		return okAction;
	}

	protected Action getCancelAction() {
		return cancelAction;
	}

	protected Options getOptions() {
		return options;
	}

	protected Label getDebug() {
		return debug;
	}

}

/**
 * SelectedJsObject extends JavaScriptObject.
 */
final class SelectedJsObject extends JavaScriptObject {
	protected SelectedJsObject() {

	}

	/**
	 * setName.
	 * 
	 * @param value
	 *            value
	 */
	native void setName(final String value)/*-{
		this.name = value;
	}-*/;

	/**
	 * setId.
	 * 
	 * @param value
	 *            value
	 */
	native void setId(final String value)/*-{
		this.id = value;
	}-*/;

	/**
	 * setParam.
	 * 
	 * @param name
	 *            name
	 * @param value
	 *            value
	 */
	native void setParam(final String name, final String value)/*-{
		this[name] = value;
	}-*/;

	/**
	 * SelectedJsObject.
	 * 
	 * @return Object
	 */
	static native SelectedJsObject create()/*-{
		return new Object();
	}-*/;
}
