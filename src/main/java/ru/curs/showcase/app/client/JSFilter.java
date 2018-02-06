package ru.curs.showcase.app.client;

import java.util.Date;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;
import com.google.gwt.view.client.*;

import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.panels.DialogBoxWithCaptionButton;

/**
 * Класс задания фильтра.
 */
public class JSFilter extends DialogBoxWithCaptionButton {

	private final VerticalPanel vpMain = new VerticalPanel();
	private final SplitLayoutPanel slpMain = new SplitLayoutPanel();
	private ScrollPanel sp;
	private final VerticalPanel vpEdit = new VerticalPanel();
	private final FlowPanel hpFooter = new FlowPanel();

	private final ProvidesKey<Filter> providesKey = new ProvidesKey<Filter>() {
		@Override
		public Object getKey(final Filter filter) {
			return filter.getId();
		}
	};
	private CellList<Filter> celllist;
	private SingleSelectionModel<Filter> selectionModel;
	private ListDataProvider<Filter> listDataProvider;

	private final ListBox linkBox = new ListBox(false);
	private final ListBox columnBox = new ListBox(false);
	private final ListBox conditionBox = new ListBox(false);
	private final TextBox valueBox = new TextBox();
	private DateBox dateBox = null;
	private final DatePicker datePicker = new DatePicker();
	private Button btnSelector = null;
	private Button btnUpdate = null;

	private int maxId = 0;
	private final JSLiveGridPluginPanel jsLiveGridPluginPanel;

	// conditionListOfValues: список значений
	// conditionEqual: равно
	// conditionNotEqual: не равно
	// conditionLess: меньше чем
	// conditionLessEqual: меньше или равно
	// conditionGreater: больше чем
	// conditionGreaterEqual: больше или равно
	// conditionContain: содержит
	// conditionStartWith: начинается с
	// conditionEndWith: заканчивается на
	// conditionNotContain: не содержит
	// conditionNotStartWith: не начинается с
	// conditionNotEndWith: не оканчивается на
	// conditionIsEmpty: пусто
	// private static final String[] STRING_CONDITIONS = {
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionContain(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionListOfValues(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionStartWith(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionEndWith(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionNotContain(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionNotStartWith(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEndWith(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionIsEmpty()
	// };
	private static final String[] STRING_CONDITIONS = {
			// AppCurrContext.getInstance().getBundleMap().get("conditionContain"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "contains"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionListOfValues"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"list of values"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionStartWith"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"starts with"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionEndWith"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"ends with"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionNotContain"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"does not contain"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionNotEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"does not equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionNotStartWith"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"does not start with"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionNotEndWith"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"does not end with"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionIsEmpty")
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"is empty") };
	// private static final String[] DATE_NUMBER_CONDITIONS = {
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionListOfValues(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionGreater(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionLess(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionGreaterEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionLessEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEqual(),
	// AppCurrContext.getInstance().getInternationalizedMessages().conditionIsEmpty()
	// };
	private static final String[] DATE_NUMBER_CONDITIONS = {
			// AppCurrContext.getInstance().getBundleMap().get("conditionEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionListOfValues"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"list of values"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionGreater"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"is greater than"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionLess"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"is less than"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionGreaterEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"greater than or equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionLessEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"less than or equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionNotEqual"),
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"does not equal"),
			// AppCurrContext.getInstance().getBundleMap().get("conditionIsEmpty")
			CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
					"is empty") };

	/**
	 * Класс ячейки условия фильтра в списке.
	 */
	private final class FilterCell extends AbstractCell<Filter> {
		@Override
		public void render(final com.google.gwt.cell.client.Cell.Context context,
				final Filter filter, final SafeHtmlBuilder sb) {
			renderFilterCell(context, filter, sb);
		}
	}

	public JSFilter(final JSLiveGridPluginPanel aJSLiveGridPluginPanel) {
		// setText(AppCurrContext.getInstance().getInternationalizedMessages().grid_caption_filter());
		setText(
				// AppCurrContext.getInstance().getBundleMap().get("grid_caption_filter"));
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Filter"));
		jsLiveGridPluginPanel = aJSLiveGridPluginPanel;

		fillCelllist();
		fillVPEdit();

		vpMain.setSize("100%", "100%");
		slpMain.setSize("600px", "550px");
		// Эксперименты для Opera
		// vpEdit.setHeight("550px");
		// vpEdit.setHeight("100%");
		// vpEdit.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		sp.setHeight("100%");
		final int size = 300;
		slpMain.addWest(sp, size);
		slpMain.add(vpEdit);
		hpFooter.setSize("100%", "100%");

		vpMain.add(slpMain);
		fillHPFooter();
		vpMain.add(hpFooter);
		setWidget(vpMain);

		center();

		initForm();

	}

	private void fillCelllist() {

		celllist = new CellList<Filter>(new FilterCell(), providesKey);
		celllist.setSize("100%", "100%");
		celllist.setKeyboardPagingPolicy(
				HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);

		selectionModel = new SingleSelectionModel<Filter>(providesKey);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(final SelectionChangeEvent event) {
				setEnableDisableVPEdit();
				setVPEdit(selectionModel.getSelectedObject());
			}
		});

		celllist.setSelectionModel(selectionModel);

		listDataProvider = new ListDataProvider<Filter>(providesKey);
		listDataProvider.addDataDisplay(celllist);

		sp = new ScrollPanel(celllist);

	}

	private void fillVPEdit() {
		final int spacing = 4;
		vpEdit.setSpacing(spacing);

		linkBox.addItem("OR");
		linkBox.addItem("AND");
		vpEdit.add(new HTML(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterLink")
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Mapping") + ":"));
		vpEdit.add(linkBox);

		columnBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				columnBoxChangeHandler();
			}
		});
		for (final GridColumnConfig egcc : ((GridMetadata) jsLiveGridPluginPanel.getElement())
				.getColumns()) {
			columnBox.addItem(egcc.getCaption());
		}
		vpEdit.add(new HTML(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterColumn")
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Column") + ":"));
		vpEdit.add(columnBox);

		conditionBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				conditionBoxChangeHandler();
			}
		});
		vpEdit.add(new HTML(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterCondition")
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Condition") + ":"));
		vpEdit.add(conditionBox);

		vpEdit.add(new HTML(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterValue")
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "Value")
						+ ":"));
		vpEdit.add(valueBox);

		dateBox = new DateBox(datePicker, new Date(),
				new DateBox.DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy HH:mm")));
		vpEdit.add(dateBox);
		vpEdit.add(datePicker);

		btnSelector = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterSelectValues"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Select values"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						// runSelector();
					}
				});
		vpEdit.add(btnSelector);

		HTML html = new HTML("");
		html.setHeight("10px");
		vpEdit.add(html);

		btnUpdate = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterUpdate"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Update filter condition"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						updateFilter();
					}
				});
		vpEdit.add(btnUpdate);

		conditionBox.setWidth("197px");
		valueBox.setWidth("190px");
		dateBox.setWidth("195px");
		datePicker.setWidth("200px");
		btnSelector.setWidth("200px");
		btnUpdate.setWidth("250px");

		setVisibleValueBoxes(true);

	}

	private void setEnableDisableVPEdit() {

		boolean enabled = (selectionModel.getSelectedObject() != null)
				&& (celllist.getVisibleItems().indexOf(selectionModel.getSelectedObject()) > -1);

		if (enabled && linkBox.isEnabled()) {
			return;
		}

		linkBox.setSelectedIndex(-1);
		columnBox.setSelectedIndex(-1);
		conditionBox.setSelectedIndex(-1);
		conditionBox.clear();
		valueBox.setText("");

		if (!enabled) {
			setVisibleValueBoxes(true);
		}

		linkBox.setEnabled(enabled);
		columnBox.setEnabled(enabled);
		conditionBox.setEnabled(enabled);
		valueBox.setEnabled(enabled);
		dateBox.setEnabled(enabled);
		btnUpdate.setEnabled(enabled);

	}

	private void fillHPFooter() {
		Button btnAdd = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterAdd"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(), "Add"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						Filter filter = new Filter();
						maxId++;
						filter.setId(String.valueOf(maxId));
						listDataProvider.getList().add(filter);
						adjustCelllistRowCount();
					}
				});
		btnAdd.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnAdd);

		HTML html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(html);

		Button btnDel = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterDel"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Delete"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						listDataProvider.getList().remove(selectionModel.getSelectedObject());
						adjustCelllistRowCount();
						setEnableDisableVPEdit();
					}
				});
		btnDel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnDel);

		html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(html);

		Button btnClear = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterClear"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Clear"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						maxId = 0;
						listDataProvider.getList().clear();
						adjustCelllistRowCount();
						setEnableDisableVPEdit();
					}
				});
		btnClear.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnClear);

		Button btnCancel = new Button(
				// AppCurrContext.getInstance().getBundleMap().get("jsFilterCancel"),
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"Cancel"),
				new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						hide();
					}
				});
		btnCancel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnCancel);

		html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(html);

		Button btnOK = new Button("OK", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();

				jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().setMaxId(maxId);
				jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getFilters().clear();
				for (final Filter filter : listDataProvider.getList()) {
					jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getFilters()
							.add(new Filter(filter));
				}

				jsLiveGridPluginPanel.refreshPanel();

			}
		});
		btnOK.setWidth("80px");
		btnOK.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnOK);

	}

	private void initForm() {
		maxId = jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getMaxId();
		listDataProvider.getList().clear();
		for (final Filter filter : jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo()
				.getFilters()) {
			listDataProvider.getList().add(new Filter(filter));
		}
		adjustCelllistRowCount();

		setEnableDisableVPEdit();

		btnSelector.setVisible(false);

	}

	private void adjustCelllistRowCount() {
		int count = listDataProvider.getList().size();
		celllist.setRowCount(count, true);
		celllist.setPageSize(count);
		celllist.setVisibleRange(0, count);
	}

	private void setListBoxSelectedIndex(final ListBox lst, final String str) {
		int index = -1;
		for (int i = 0; i < lst.getItemCount(); i++) {
			if (lst.getItemText(i).equals(str)) {
				index = i;
				break;
			}
		}
		lst.setSelectedIndex(index);
	}

	private void setVisibleValueBoxes(final boolean b) {
		valueBox.setVisible(b);
		dateBox.setVisible(!b);
		datePicker.setVisible(!b);
	}

	private void columnBoxChangeHandler() {
		conditionBox.clear();
		if (columnBox.getSelectedIndex() > -1) {
			GridValueType vt = ((GridMetadata) jsLiveGridPluginPanel.getElement()).getColumns()
					.get(columnBox.getSelectedIndex()).getValueType();

			if (vt.isGeneralizedString()) {
				setListBoxConditions(STRING_CONDITIONS);
			} else {
				setListBoxConditions(DATE_NUMBER_CONDITIONS);
			}
			conditionBox.setSelectedIndex(-1);

			if (vt.isDate()) {
				setVisibleValueBoxes(false);
			} else {
				setVisibleValueBoxes(true);
			}

			conditionBoxChangeHandler();
		} else {
			setVisibleValueBoxes(true);
		}
	}

	private void conditionBoxChangeHandler() {
		if ((conditionBox.getSelectedIndex() > -1)
				// AppCurrContext.getInstance().getBundleMap().get("conditionListOfValues")
				&& CourseClientLocalization
						.gettext(AppCurrContext.getInstance().getDomain(), "list of values")
						.equalsIgnoreCase(
								conditionBox.getItemText(conditionBox.getSelectedIndex()))) {
			btnSelector.setVisible(true);

			setVisibleValueBoxes(true);
		} else {
			btnSelector.setVisible(false);

			GridValueType vt = ((GridMetadata) jsLiveGridPluginPanel.getElement()).getColumns()
					.get(columnBox.getSelectedIndex()).getValueType();
			if (vt.isDate()) {
				setVisibleValueBoxes(false);
			} else {
				setVisibleValueBoxes(true);
			}
		}
	}

	private void setListBoxConditions(final String[] conditions) {
		for (int i = 0; i < conditions.length; i++) {
			if (
			// AppCurrContext.getInstance().getBundleMap().get("conditionListOfValues")
			CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "list of values")
					.equalsIgnoreCase(conditions[i])) {
				FilterMultiselector fms = ((GridMetadata) (jsLiveGridPluginPanel.getElement()))
						.getJSInfo().getFilterMultiselector();
				if (fms == null) {
					continue;
				}
			}

			conditionBox.addItem(conditions[i]);
		}
	}

	private void setVPEdit(final Filter filter) {

		setListBoxSelectedIndex(linkBox, filter.getLink());

		setListBoxSelectedIndex(columnBox, filter.getColumn());
		columnBoxChangeHandler();

		setListBoxSelectedIndex(conditionBox, filter.getCondition());
		conditionBoxChangeHandler();

		if (valueBox.isVisible()) {
			valueBox.setText(getStringValue(filter, false));
		} else {
			dateBox.setValue(filter.getDateValue());
		}

	}

	private void updateFilter() {
		Filter filter = selectionModel.getSelectedObject();

		int index = linkBox.getSelectedIndex();
		if (index > -1) {
			filter.setLink(linkBox.getItemText(index));
		} else {
			filter.setLink(null);
		}
		index = columnBox.getSelectedIndex();
		if (index > -1) {
			filter.setColumn(columnBox.getItemText(index));
		} else {
			filter.setColumn(null);
		}
		index = conditionBox.getSelectedIndex();
		if (index > -1) {
			filter.setCondition(conditionBox.getItemText(conditionBox.getSelectedIndex()));
		} else {
			filter.setCondition(null);
		}

		index = conditionBox.getSelectedIndex();
		if (index > -1) {
			if (!
			// AppCurrContext.getInstance().getBundleMap().get("conditionListOfValues")
			CourseClientLocalization
					.gettext(AppCurrContext.getInstance().getDomain(), "list of values")
					.equalsIgnoreCase(conditionBox.getItemText(index))) {
				filter.getListOfValues().clear();
				filter.getListOfValuesId().clear();
			}
		}

		if (dateBox.isVisible()) {
			filter.setDateValue(dateBox.getValue());
			filter.setValue(dateBox.getTextBox().getText());
		} else {
			filter.setValue(valueBox.getText());
		}

		listDataProvider.refresh();

	}

	private String getStringValue(final Filter filter, final boolean withQuotes) {
		String value = "";
		if (filter.getListOfValues().size() == 0) {
			if (withQuotes) {
				value = value + "'" + filter.getValue() + "'";
			} else {
				value = filter.getValue();
			}
		} else {
			for (int i = 0; i < filter.getListOfValues().size(); i++) {
				if (i > 0) {
					value = value + ", ";
				}
				value = value + "'" + filter.getListOfValues().get(i) + "'";
			}
		}
		return value;
	}

	private void renderFilterCell(final com.google.gwt.cell.client.Cell.Context context,
			final Filter filter, final SafeHtmlBuilder sb) {
		if (filter.getLink() == null) {
			sb.appendEscaped(
					// AppCurrContext.getInstance().getBundleMap().get("jsFilterCondition")
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"Condition") + " " + filter.getId());
		} else {
			String img;
			if ("OR".equalsIgnoreCase(filter.getLink())) {
				img = "resources/internal/or.png";
			} else {
				img = "resources/internal/and.png";
			}
			sb.appendHtmlConstant("<table>");
			sb.appendHtmlConstant("<td><img border=\"0\" src=" + img + "></td>");
			sb.appendHtmlConstant("<td style='vertical-align:middle;'>");
			sb.appendHtmlConstant("\"" + filter.getColumn() + "\" " + filter.getCondition() + " "
					+ getStringValue(filter, true));
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("</table>");
		}
	}

	private static native JavaScriptObject getInitSelection(final JsArrayString listOfValues,
			final JsArrayString listOfValuesId) /*-{
		var arrayNames = [ "id", "name" ];
		var arrayValues = [ listOfValuesId, listOfValues ];

		var selected = {};
		selected["columnCount"] = arrayNames.length;
		selected["recordCount"] = arrayValues[0].length;
		selected["names"] = arrayNames;
		selected["values"] = arrayValues;

		return selected;
	}-*/;

	/**
	 * ArrayJsDataRecord.
	 */
	@SuppressWarnings("unused")
	private static final class ArrayJsDataRecord extends JavaScriptObject {
		protected ArrayJsDataRecord() {
		};

		native int getRecordCount() /*-{
			return this.length;
		}-*/;

		native String getId(final int i) /*-{
			return this[i]["id"];
		}-*/;

		native String getName(final int i) /*-{
			return this[i]["name"];
		}-*/;

	}

}
