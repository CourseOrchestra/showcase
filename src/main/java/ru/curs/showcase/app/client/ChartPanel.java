package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с графиком и легендой.
 */
public class ChartPanel extends BasicElementPanelBasis {

	public ChartPanel(final CompositeContext context1, final DataPanelElementInfo element1) {

		this.setContext(context1);
		setElementInfo(element1);

		generalChartPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();

		this.getPanel().addStyleName("chart-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalChartPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalChartPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalChartPanel.add(new HTML(""));
		}

		// generalChartPanel.add(new
		// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading()));

		dataService = GWT.create(DataService.class);

		setChartPanel();

	}

	public ChartPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		setElementInfo(element1);
		generalHp = new HorizontalPanel();
		this.setContext(null);
		// я бы убрал этот код-конец

		generalChartPanel = new VerticalPanel();

		this.getPanel().addStyleName("chart-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalChartPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalChartPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalChartPanel.add(new HTML(""));
		}

		// generalChartPanel.add(new
		// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading()));
	}

	private void setChartPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getChart(getContext(), getElementInfo(), new
		// GWTServiceCallback<Chart>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_chart_data_retrieving_from_server()) {
		dataService.getChart(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<Chart>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_chart_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving graph data from server")) {
					@Override
					public void onSuccess(final Chart achart) {

						chart = achart;
						if (chart != null) {

							super.onSuccess(chart);

							fillChartPanel(achart);

							Scheduler.get().scheduleDeferred(new Command() {
								@Override
								public void execute() {
									for (DataPanelElementInfo el : AppCurrContext
											.getReadyStateMap().keySet()) {
										if (el.getType() == DataPanelElementType.CHART
												&& !AppCurrContext.getReadyStateMap().get(el)) {
											AppCurrContext.getReadyStateMap().put(el, true);
											break;
										}
									}

									if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
										RootPanel.getBodyElement().addClassName("ready");
									}
								}
							});

						}
					}
				});

	}

	/**
	 * 
	 * Заполняет виджет графика содержимым.
	 * 
	 * @param achart
	 *            Chart
	 */
	protected void fillChartPanel(final Chart achart) {

		final String divIdGraph = getElementInfo().getId() + Constants.CHART_DIV_ID_SUFFIX;
		final String divIdLegend = getElementInfo().getId() + Constants.CHART_LEGEND_DIV_ID_SUFFIX;

		final String htmlForChart = "<div id='" + divIdGraph + "' class='cursChart'></div>";

		final String htmlForLegend = "<div id='" + divIdLegend + "'></div>";

		footerHTML = new HTML(achart.getFooter());

		headerHTML = new HTML(achart.getHeader());

		chartHTML = new HTML(htmlForChart);

		legendHTML = new HTML(htmlForLegend);
		generalChartPanel.clear();
		generalHp.clear();

		generalChartPanel.add(headerHTML);

		switch (achart.getLegendPosition()) {
		case LEFT:

			generalChartPanel.add(generalHp);
			generalHp.add(legendHTML);
			generalHp.add(chartHTML);
			break;

		case RIGHT:

			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			generalHp.add(legendHTML);
			break;

		case TOP:

			generalChartPanel.add(legendHTML);
			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			break;

		case BOTTOM:

			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			generalChartPanel.add(legendHTML);
			break;

		default:
			break;

		}
		generalChartPanel.add(footerHTML);

		final String paramChart1 = achart.getJsDynamicData();

		final String paramChart2 = achart.getTemplate();

		try {

			drawChart(divIdGraph, divIdLegend, paramChart1, paramChart2);

		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				// MessageBox.showMessageWithDetails(AppCurrContext.getInstance()
				// .getInternationalizedMessages().error_of_chart_painting(),
				// e.getMessage(),
				// GeneralException.generateDetailedInfo(e.getCause()),
				// GeneralException.getMessageType(e.getCause()),
				// GeneralException.needDetailedInfo(e.getCause()));
				MessageBox.showMessageWithDetails(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_chart_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Graph construction error"), e.getMessage(), GeneralException
								.generateDetailedInfo(e.getCause()), GeneralException
								.getMessageType(e.getCause()), GeneralException.needDetailedInfo(e
								.getCause()), null);
			} else {
				// MessageBox.showSimpleMessage(AppCurrContext.getInstance()
				// .getInternationalizedMessages().error_of_chart_painting(),
				// e.getMessage());
				MessageBox.showSimpleMessage(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_chart_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Graph construction error"), e.getMessage());
			}
		}

		checkForDefaultAction();
		setupTimer();
	}

	/**
	 * VerticalPanel на которой отображен график и легенда.
	 */
	private final VerticalPanel generalChartPanel;

	/**
	 * HorizontalPanel на которой отображен график и легенда.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * DataGrid chart.
	 */
	private Chart chart = null;

	/**
	 * @return Возвращает текущий объект типа Сhart - данные графика.
	 */
	public Chart getChart() {
		return chart;
	}

	/**
	 * Устанавливает текущий объект типа Сhart - данные графика.
	 * 
	 * @param achart
	 *            - объект типа Сhart
	 */
	public void setChart(final Chart achart) {
		this.chart = achart;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для подписи графика в нижней части.
	 */
	private HTML footerHTML = null;

	/**
	 * HTML виждет для подписи графика в заголовной части.
	 */
	private HTML headerHTML = null;

	/**
	 * HTML виждет для графика.
	 */
	private HTML chartHTML = null;

	/**
	 * HTML виждет для легенды графика.
	 */
	private HTML legendHTML = null;

	/**
	 * Ф-ция, возвращающая панель с графиком и легендой, если она необходима.
	 * 
	 * @return - Панель с графиком и легендой.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalChartPanel;
	}

	/**
	 * 
	 * Процедура прорисовки графика с помощью библиотеки dojo.
	 * 
	 * @param divIdGraph
	 *            - ID для div графика
	 * @param divIdLegend
	 *            - ID для div легенды графика
	 * @param jsonStr1
	 *            - JSON строка с данными графика
	 * @param jsonStr2
	 *            - JSON строка с настройками графика
	 */
	public native void drawChart(final String divIdGraph, final String divIdLegend,
			final String jsonStr1, final String jsonStr2) /*-{
		$wnd.gwtChartFunc = 
		@ru.curs.showcase.app.client.api.ChartPanelCallbacksEvents::chartPanelClick(Ljava/lang/String;Ljava/lang/String;I);

		$wnd.dojo.require("course.charting");
		$wnd.course.charting.makeChart(divIdGraph, divIdLegend, jsonStr1, jsonStr2, $wnd.convertorFunc);
	}-*/;

	@Override
	public void reDrawPanel(final CompositeContext context1) {

		this.setContext(context1);
		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			generalChartPanel.clear();
			// generalChartPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalChartPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalChartPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getChart(getContext(), getElementInfo(), new
		// GWTServiceCallback<Chart>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_chart_data_retrieving_from_server()) {
		dataService.getChart(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<Chart>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_chart_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving graph data from server")) {

					@Override
					public void onSuccess(final Chart achart) {
						chart = achart;
						if (chart != null) {

							super.onSuccess(chart);

							fillChartPanel(achart);
							getPanel().setHeight("100%");
						}

						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								for (DataPanelElementInfo el : AppCurrContext.getReadyStateMap()
										.keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext.getReadyStateMap().get(el)) {
										AppCurrContext.getReadyStateMap().put(el, true);
										break;
									}
								}

								for (DataPanelElementInfo el : AppCurrContext
										.getFromActionElementsMap().keySet()) {
									if (getElementInfo().getId().getString()
											.equals(el.getId().getString())
											&& !AppCurrContext.getFromActionElementsMap().get(el)) {
										AppCurrContext.getFromActionElementsMap().put(el, true);
										break;
									}
								}

								if (!AppCurrContext.getInstance()
										.getChartXformTrueStateForReadyStateMap()
										&& !AppCurrContext
												.getInstance()
												.getGridWithToolbarChartTrueStateForReadyStateMap())
									if (!AppCurrContext.getReadyStateMap().containsValue(false)) {
										RootPanel.getBodyElement().addClassName("ready");
									}
							}
						});
					}
				});

	}

	@Override
	public void hidePanel() {
		generalChartPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalChartPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (chart.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(
					chart.getActionForDependentElements(), chart);
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return chart;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			generalChartPanel.clear();
			// generalChartPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalChartPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalChartPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getChart(getContext(), getElementInfo(), new
		// GWTServiceCallback<Chart>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_chart_data_retrieving_from_server()) {
		dataService.getChart(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<Chart>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_chart_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving graph data from server")) {

					@Override
					public void onSuccess(final Chart achart) {
						chart = achart;
						if (chart != null) {

							super.onSuccess(chart);

							fillChartPanel(achart);
							getPanel().setHeight("100%");

						}
					}
				});

	}
}
