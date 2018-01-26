package ru.curs.showcase.app.client.selector;

import java.util.List;

import ru.curs.showcase.app.api.common.*;
import ru.curs.showcase.app.api.selector.*;
import ru.curs.showcase.app.client.AppCurrContext;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

/**
 * Компонента множественного выбора из больших списков.
 */
public class MultiSelectorComponent extends BaseSelectorComponent {

	private static final String SELECT_BUTTONS_WIDTH = "16px";
	private static final String SELECT_BUTTONS_HEIGHT = "60px";
	private static final String SELECT_BUTTONS_IMAGE_STYLE =
		"extragwt-SelectorComponent-SelectButtonImage";

	private final TextBox searchSelectedString = new TextBox();

	private static final int MAX_INTEGER = 100000;

	/**
	 * interface MultiResources.
	 * 
	 */
	interface MultiResources extends ClientBundle {
		ImageResource selectImage();

		ImageResource selectAllImage();

		ImageResource unselectImage();

		ImageResource unselectAllImage();
	}

	private static MultiResources multiresources;

	private final ProvidesKey<DataRecord> providesKey = new ProvidesKey<DataRecord>() {
		@Override
		public Object getKey(final DataRecord dr) {
			return dr.getId();
		}
	};

	private ScrollPanel selectedScrollPanel = null;

	private MultiSelectionModel<DataRecord> allSelectionModel;

	private CellList<DataRecord> selectedCelllist;
	private MultiSelectionModel<DataRecord> selectedSelectionModel;
	private ListDataProvider<DataRecord> selectedDataProvider;

	private List<DataRecord> selected;

	private final JavaScriptObject initSelection;

	public MultiSelectorComponent(final SelectorDataServiceAsync srv1, final String title,
			final JavaScriptObject initSelection1) {
		super(srv1, title);
		initSelection = initSelection1;

		setupWidgets();

		WidgetBuilder b = new WidgetBuilder();
		fillWidgetBuilder(b);
		setWidget(b.done());

		this.addStyleName("server-multiselector-popup");
	}

	public MultiSelectorComponent(final SelectorDataServiceAsync srv1, final String title,
			final JavaScriptObject initSelection1, final Options options1) {
		super(srv1, title, options1);
		initSelection = initSelection1;

		setupWidgets();

		WidgetBuilder b = new WidgetBuilder();
		fillWidgetBuilder(b);
		setWidget(b.done());

		this.addStyleName("server-multiselector-popup");
	}

	private void createCelllist() {
		setCelllist(new CellList<DataRecord>(new DataCell(), providesKey));
	}

	private void setupCelllistSelectionModel() {
		allSelectionModel = new MultiSelectionModel<DataRecord>(providesKey);
		getCelllist().setSelectionModel(allSelectionModel);
	}

	private void setupSearchString() {
		getSearchString().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(final KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					if (getCelllist().getVisibleItemCount() > 0) {
						DataRecord dr = getCelllist().getVisibleItem(0);
						allSelectionModel.setSelected(dr, true);

						selectAction().execute();
					}
				}
			}
		});
	}

	private void setupWidgets() {
		setupHandlers();
		setupSearchString();
		setupStartsWithCheckbox();
		createCelllist();
		setupCelllist();
		setupCelllistSelectionModel();
		setupCellholder();
		setupScrollController();
		setupSearchController();
		setupActions();

		selectedCelllist = new CellList<DataRecord>(new SelectedDataCell(), providesKey);
		selectedCelllist.setSize(PROC100, PROC100);
		selectedCelllist
				.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);

		selectedSelectionModel = new MultiSelectionModel<DataRecord>(providesKey);
		selectedCelllist.setSelectionModel(selectedSelectionModel);
		selectedCelllist.addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
			@Override
			public void onRowCountChange(final RowCountChangeEvent event) {
				// getOkAction().setEnabled(event.getNewRowCount() > 0);
			}
		});

		selectedDataProvider = new ListDataProvider<DataRecord>(providesKey);
		selectedDataProvider.addDataDisplay(selectedCelllist);

		selectedScrollPanel = new ScrollPanel(selectedCelllist);
		selectedScrollPanel.setAlwaysShowScrollBars(true);
		selectedScrollPanel.setWidth(getOptions().getSelectedDataWidth());
		selectedScrollPanel.setHeight(PROC100);
	}

	private void fillWidgetBuilder(final WidgetBuilder b) {

		String searchStringWidth;
		if (getOptions().getManualSearch()) {
			final int delta = 18;
			searchStringWidth =
				String.valueOf(getIntSizeValue(getOptions().getDataWidth())
						- getIntSizeValue(CLEAR_BUTTON_WIDTH) * 2 - delta)
						+ "px";
		} else {
			searchStringWidth =
				String.valueOf(getIntSizeValue(getOptions().getDataWidth())
						- getIntSizeValue(CLEAR_BUTTON_WIDTH) - 2)
						+ "px";
		}

		final int deltaSearchSelectedString = 38;
		String searchSelectedStringWidth =
			String.valueOf(getIntSizeValue(getOptions().getSelectedDataWidth())
					- deltaSearchSelectedString)
					+ "px";

		b.vertical().width(PROC100).horizontal().width(PROC100).height(PROC100);

		WidgetBuilder wbSearch =
			b.vertical().width(PROC100).spacing(DEF_SPACING).horizontal().width(PROC100)
					.widget(getSearchString())
					.style("server-multiselector-searchstringtextbox-element")
					.width(searchStringWidth).height(PROC100).label(" ");
		if (getOptions().getManualSearch()) {
			wbSearch =
				wbSearch.button(manualSearchAction(), ActionButtonStyle.IMAGE)
						.style("server-multiselector-manualsearchbutton-element")
						.height(CLEAR_BUTTON_HEIGHT).width(CLEAR_BUTTON_WIDTH).label(" ")
						.width("4px");
		}
		wbSearch =
			wbSearch.button(clearAction(), ActionButtonStyle.IMAGE)
					.style("server-multiselector-clearbutton-element").height(CLEAR_BUTTON_HEIGHT)
					.width(CLEAR_BUTTON_WIDTH).end().widget(getStartsWithCheckbox())
					.style("server-multiselector-checkbox-element");

		b.horizontal().width(PROC100).style("extragwt-SelectorComponent-ListWrapper")
				.style("server-multiselector-listwrapper-element").widget(getCellholder())
				.smartWidth(getOptions().getDataWidth()).widget(getScroll()).end().end();

		b.vertical().height(PROC100).width(PROC100).spacing(DEF_SPACING)
				.button(selectAction(), ActionButtonStyle.IMAGE)
				.style("server-multiselector-selectbutton-element").height(SELECT_BUTTONS_HEIGHT)
				.width(SELECT_BUTTONS_WIDTH).button(selectAllAction(), ActionButtonStyle.IMAGE)
				.style("server-multiselector-selectallbutton-element")
				.height(SELECT_BUTTONS_HEIGHT).width(SELECT_BUTTONS_WIDTH)
				.button(unselectAction(), ActionButtonStyle.IMAGE)
				.style("server-multiselector-unselectbutton-element")
				.height(SELECT_BUTTONS_HEIGHT).width(SELECT_BUTTONS_WIDTH)
				.button(unselectAllAction(), ActionButtonStyle.IMAGE)
				.style("server-multiselector-unselectallbutton-element")
				.height(SELECT_BUTTONS_HEIGHT).width(SELECT_BUTTONS_WIDTH).end();

		b.vertical().width(PROC100).spacing(DEF_SPACING).horizontal().width(PROC100)
				.widget(searchSelectedString)
				.style("server-multiselector-searchselectedstringtextbox-element")
				.width(searchSelectedStringWidth).height(PROC100).label(" ")
				.button(findAction(), ActionButtonStyle.IMAGE)
				.style("server-multiselector-findbutton-element").height(CLEAR_BUTTON_HEIGHT)
				.width(CLEAR_BUTTON_WIDTH).end();

		b.widget(selectedScrollPanel).style("extragwt-SelectorComponent-ListWrapper")
				.style("server-multiselector-selectedlistwrapper-element").end().end();

		b.horizontal().height(PROC100).width(PROC100).label(" ").cellWidth(PROC100)
				.button(getOkAction()).style("server-multiselector-okbutton-element")
				.widget(new HTML("&nbsp;&nbsp;")).button(getCancelAction())
				.style("server-multiselector-cancelbutton-element").end()
				// .widget(getDebug())
				.end();

	}

	@Override
	public void initData(final String params, final String procName, final String curValue,
			final SelectorAdditionalData addData) {
		final int deltaSearchSelectedString = 12;
		selectedScrollPanel.setHeight(String.valueOf(getSearchString().getParent().getParent()
				.getOffsetHeight()
				- searchSelectedString.getOffsetHeight()
				- deltaSearchSelectedString
				- 2
				* DEF_SPACING)
				+ "px");

		super.initData(params, procName, curValue, addData);

		setInitSelection();
	}

	private void setInitSelection() {
		if (initSelection == null) {
			return;
		}

		final ArrayJsDataRecord adr = (ArrayJsDataRecord) initSelection;

		int aliasId = -1;
		int aliasName = -1;
		for (int j = 0; j < adr.getColumnCount(); j++) {
			if ("id".equalsIgnoreCase(adr.getName(j))) {
				aliasId = j;
			}
			if ("name".equalsIgnoreCase(adr.getName(j))) {
				aliasName = j;
			}
		}
		if ((aliasId == -1) || (aliasName == -1)) {
			aliasId = 0;
			aliasName = 1;
		}
		for (int i = 0; i < adr.getRecordCount(); i++) {
			DataRecord record = new DataRecord();
			record.setId(adr.getValue(i, aliasId));
			record.setName(adr.getValue(i, aliasName));

			for (int j = 0; j < adr.getColumnCount(); j++) {
				if ((j != aliasId) && (j != aliasName)) {
					record.addParameter(adr.getName(j), adr.getValue(i, j));
				}
			}

			selectedDataProvider.getList().add(record);
			setSelectedCelllistRowCount();
		}
	}

	/**
	 * ArrayJsDataRecord.
	 */
	private static final class ArrayJsDataRecord extends JavaScriptObject {
		protected ArrayJsDataRecord() {
		};

		native int getColumnCount() /*-{
			return this.columnCount;
		}-*/;

		native int getRecordCount() /*-{
			return this.recordCount;
		}-*/;

		native String getName(final int j) /*-{
			return this.names[j];
		}-*/;

		native String getValue(final int i, final int j) /*-{
			return this.values[j][i];
		}-*/;

	}

	private boolean isRecordSelected(final DataRecord record) {
		boolean b = false;
		for (DataRecord selectedrecord : selectedDataProvider.getList()) {
			if (record.getId().equals(selectedrecord.getId())) {
				b = true;
				break;
			}
		}
		return b;
	}

	private void setSelectedCelllistRowCount() {
		int count = selectedDataProvider.getList().size();
		selectedCelllist.setRowCount(count, true);
		selectedCelllist.setPageSize(count);
		selectedCelllist.setVisibleRange(0, count);
	}

	private Action selectAction() {
		AbstractAction a = new AbstractAction("Select") {
			@Override
			protected void perform() {
				for (DataRecord record : allSelectionModel.getSelectedSet()) {
					if (!isRecordSelected(record)) {
						selectedDataProvider.getList().add(record);
						setSelectedCelllistRowCount();
					}
				}
			}
		};

		Image selectImage = new Image(getMultiResources().selectImage());
		selectImage.setStyleName(SELECT_BUTTONS_IMAGE_STYLE);
		a.setImage(selectImage);
		return a;
	}

	private Action selectAllAction() {
		AbstractAction a = new AbstractAction("Select all") {
			@Override
			protected void perform() {
				DataRequest req = new DataRequest();
				req.setParams(getDataRequest().getParams());
				req.setProcName(getDataRequest().getProcName());
				req.setCurValue(getDataRequest().getCurValue());
				req.setAddData(getDataRequest().getAddData());
				req.setStartsWith(getDataRequest().isStartsWith());
				req.setFirstRecord(0);
				req.setRecordCount(MAX_INTEGER);

				getSelectorDataService().getSelectorData(req, new AsyncCallback<DataSet>() {
					@Override
					public void onFailure(final Throwable throwable) {
						if (getErrorHandler() != null) {
							getErrorHandler().onDataServiceFailure(throwable);
						}
					}

					@Override
					public void onSuccess(final DataSet dataSet) {
						List<DataRecord> records = getSafeRecords(dataSet);

						// selectedDataProvider.getList().clear();
						// selectedDataProvider.getList().addAll(records);

						for (DataRecord record : records) {
							if (!isRecordSelected(record)) {
								selectedDataProvider.getList().add(record);
								setSelectedCelllistRowCount();
								setSelectedCelllistRowCount();
							}
						}

					}
				});
			}
		};

		Image selectAllImage = new Image(getMultiResources().selectAllImage());
		selectAllImage.setStyleName(SELECT_BUTTONS_IMAGE_STYLE);
		a.setImage(selectAllImage);
		return a;
	}

	private Action unselectAction() {
		AbstractAction a = new AbstractAction("Deselect the record") {
			@Override
			protected void perform() {
				selectedDataProvider.getList().removeAll(selectedSelectionModel.getSelectedSet());
				setSelectedCelllistRowCount();
			}
		};

		Image unselectImage = new Image(getMultiResources().unselectImage());
		unselectImage.setStyleName(SELECT_BUTTONS_IMAGE_STYLE);
		a.setImage(unselectImage);
		return a;
	}

	private Action unselectAllAction() {
		AbstractAction a = new AbstractAction("Deselect all the records") {
			@Override
			protected void perform() {
				selectedDataProvider.getList().clear();
				setSelectedCelllistRowCount();
			}
		};

		Image unselectAllImage = new Image(getMultiResources().unselectAllImage());
		unselectAllImage.setStyleName(SELECT_BUTTONS_IMAGE_STYLE);
		a.setImage(unselectAllImage);
		return a;
	}

	private Action findAction() {
		AbstractAction a = new AbstractAction("Find") {
			@Override
			protected void perform() {
				boolean found = false;
				for (int i = selectedCelllist.getKeyboardSelectedRow() + 1; i < selectedDataProvider
						.getList().size(); i++) {
					if (selectedDataProvider.getList().get(i).getName().toLowerCase()
							.contains(searchSelectedString.getText().toLowerCase())) {
						selectedCelllist.setKeyboardSelectedRow(i, true);
						found = true;
						break;
					}
				}
				if (!found) {
					// Window.alert(AppCurrContext.getInstance().getInternationalizedMessages()
					// .multySelectorStringNotFound());
					Window.alert(
					// AppCurrContext.getInstance().getBundleMap().get("multySelectorStringNotFound"));
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Not found"));
				}
			}
		};

		Image findImage = new Image(getResources().findImage());
		// selectImage.setStyleName(SELECT_BUTTONS_IMAGE_STYLE);
		a.setImage(findImage);
		return a;
	}

	@Override
	protected void clearSelectedDataRecord() {
		allSelectionModel.clear();
	}

	@Override
	protected void setSelectedObject() {
		selected = selectedDataProvider.getList();
	}

	/**
	 * Возвращает список выбранных записей с данными. Список доступен только
	 * после нажатия кнопки OK.
	 * 
	 * @return выделенная запись с данными
	 */
	public List<DataRecord> getListSelectedDataRecords() {
		return selected;
	}

	/**
	 * Возвращает массив выбранных записей с данными в виде Javascript-объекта.
	 * 
	 * @return JavaScriptObject
	 */
	@Override
	public JavaScriptObject getSelectedAsJsObject() {
		ArraySelectedJsObject arrayselected = ArraySelectedJsObject.create();
		for (DataRecord record : selectedDataProvider.getList()) {
			arrayselected.push(getDataRecordAsJsObject(record));
		}
		return arrayselected;
	}

	private static MultiResources getMultiResources() {
		if (multiresources == null) {
			multiresources = GWT.create(MultiResources.class);
		}
		return multiresources;
	}

	/**
	 * DataCell extends AbstractCell<DataRecord>.
	 */
	private final class DataCell extends AbstractCell<DataRecord> {
		private DataCell() {
			super(DBL_CLICK, KEY_DOWN, MOUSE_WHEEL, DOM_MOUSE_SCROLL);
		}

		@Override
		public void render(final Cell.Context context, final DataRecord dataRecord,
				final SafeHtmlBuilder safeHtmlBuilder) {
			renderDataCell(context, dataRecord, safeHtmlBuilder);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onBrowserEvent(final Context context, final Element parent,
				final DataRecord value, final NativeEvent event,
				final ValueUpdater<DataRecord> dataRecordValueUpdater) {
			if (DBL_CLICK.equals(event.getType())) {
				selectAction().execute();
			}

			if ((MOUSE_WHEEL.equals(event.getType()))
					|| (DOM_MOUSE_SCROLL.equals(event.getType()))) {
				getScroll().setScrollPosition(
						getScroll().getScrollPosition() + MOUSE_SCROLL_UNITS
								* event.getMouseWheelVelocityY());

			}

			super.onBrowserEvent(context, parent, value, event, dataRecordValueUpdater);
		}

		@Override
		protected void onEnterKeyDown(final Context context, final Element parent,
				final DataRecord value, final NativeEvent event,
				final ValueUpdater<DataRecord> dataRecordValueUpdater) {
			selectAction().execute();

			super.onEnterKeyDown(context, parent, value, event, dataRecordValueUpdater);
		}
	}

	/**
	 * SelectedDataCell extends AbstractCell<DataRecord>.
	 */
	private final class SelectedDataCell extends AbstractCell<DataRecord> {
		private SelectedDataCell() {
			super(DBL_CLICK, KEY_DOWN);
		}

		@Override
		public void render(final Cell.Context context, final DataRecord dataRecord,
				final SafeHtmlBuilder safeHtmlBuilder) {
			renderDataCell(context, dataRecord, safeHtmlBuilder);
		}

		@Override
		public void onBrowserEvent(final Context context, final Element parent,
				final DataRecord value, final NativeEvent event,
				final ValueUpdater<DataRecord> dataRecordValueUpdater) {
			if (DBL_CLICK.equals(event.getType())) {
				unselectAction().execute();
			}

			super.onBrowserEvent(context, parent, value, event, dataRecordValueUpdater);
		}

		@Override
		protected void onEnterKeyDown(final Context context, final Element parent,
				final DataRecord value, final NativeEvent event,
				final ValueUpdater<DataRecord> dataRecordValueUpdater) {
			unselectAction().execute();

			super.onEnterKeyDown(context, parent, value, event, dataRecordValueUpdater);
		}
	}

}

/**
 * ArraySelectedJsObject extends JavaScriptObject.
 */
final class ArraySelectedJsObject extends JavaScriptObject {
	protected ArraySelectedJsObject() {

	}

	native void push(final JavaScriptObject value)/*-{
		this.push(value);
	}-*/;

	static native ArraySelectedJsObject create()/*-{
		return new Array();
	}-*/;
}
