package ru.curs.showcase.app.client.selector;

import ru.curs.showcase.app.api.common.*;
import ru.curs.showcase.app.api.selector.*;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.*;

/**
 * Компонента выбора из больших списков.
 */
public class SelectorComponent extends BaseSelectorComponent {

	private final ProvidesKey<DataRecord> providesKey = new ProvidesKey<DataRecord>() {
		@Override
		public Object getKey(final DataRecord dr) {
			return dr.getId();
		}
	};

	private SingleSelectionModel<DataRecord> ssm;

	private DataRecord selected;

	public SelectorComponent(final SelectorDataServiceAsync srv1, final String title) {
		super(srv1, title);

		setupWidgets();

		WidgetBuilder b = new WidgetBuilder();
		fillWidgetBuilder(b);
		setWidget(b.done());

		this.addStyleName("server-selector-popup");
	}

	public SelectorComponent(final SelectorDataServiceAsync srv1, final String title,
			final Options options1) {
		super(srv1, title, options1);

		setupWidgets();

		WidgetBuilder b = new WidgetBuilder();
		fillWidgetBuilder(b);
		setWidget(b.done());

		this.addStyleName("server-selector-popup");
	}

	private void createCelllist() {
		setCelllist(new CellList<DataRecord>(new DataCell(), providesKey));
	}

	private void setupCelllistSelectionModel() {
		ssm = new SingleSelectionModel<DataRecord>(providesKey);
		getCelllist().setSelectionModel(ssm);
		ssm.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(final SelectionChangeEvent event) {
				getOkAction().setEnabled(ssm.getSelectedObject() != null);
			}
		});
	}

	private void setupSearchString() {
		getSearchString().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(final KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					if (getCelllist().getVisibleItemCount() > 0) {
						DataRecord dr = getCelllist().getVisibleItem(0);
						ssm.setSelected(dr, true);

						getOkAction().setEnabled(true);
						getOkAction().execute();
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

		WidgetBuilder wbSearch =
			b.vertical().width(PROC100).spacing(DEF_SPACING).horizontal().width(PROC100)
					.widget(getSearchString())
					.style("server-selector-searchstringtextbox-element").width(searchStringWidth)
					.height(PROC100).label(" ");
		if (getOptions().getManualSearch()) {
			wbSearch =
				wbSearch.button(manualSearchAction(), ActionButtonStyle.IMAGE)
						.style("server-selector-manualsearchbutton-element")
						.height(CLEAR_BUTTON_HEIGHT).width(CLEAR_BUTTON_WIDTH).label(" ")
						.width("4px");
		}
		wbSearch =
			wbSearch.button(clearAction(), ActionButtonStyle.IMAGE)
					.style("server-selector-clearbutton-element").height(CLEAR_BUTTON_HEIGHT)
					.width(CLEAR_BUTTON_WIDTH).end().widget(getStartsWithCheckbox())
					.style("server-selector-checkbox-element");

		b.horizontal().width(PROC100).style("extragwt-SelectorComponent-ListWrapper")
				.style("server-selector-listwrapper-element").widget(getCellholder())
				.smartWidth(getOptions().getDataWidth()).widget(getScroll()).end();

		b.horizontal().width(PROC100).label(" ").cellWidth(PROC100).button(getOkAction())
				.style("server-selector-okbutton-element").widget(new HTML("&nbsp;&nbsp;"))
				.button(getCancelAction()).style("server-selector-cancelbutton-element").end()
				// .widget(debug)
				.end();

	}

	@Override
	protected void clearSelectedDataRecord() {
		ssm.clear();
	}

	@Override
	protected void setSelectedObject() {
		selected = ssm.getSelectedObject();
	}

	/**
	 * Возвращает выделенную запись с данными. Выделенный объект доступен только
	 * после нажатия кнопки OK.
	 * 
	 * @return выделенная запись с данными
	 */
	public DataRecord getSelectedDataRecord() {
		return selected;
	}

	/**
	 * Возвращает выделенную запись с данными в виде Javascript-объекта.
	 * 
	 * @return JavaScriptObject
	 */
	@Override
	public JavaScriptObject getSelectedAsJsObject() {
		return getDataRecordAsJsObject(selected);
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
				getOkAction().execute();
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
			getOkAction().execute();

			super.onEnterKeyDown(context, parent, value, event, dataRecordValueUpdater);
		}
	}

}
