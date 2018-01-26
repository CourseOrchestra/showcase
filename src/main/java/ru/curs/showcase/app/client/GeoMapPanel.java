package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с картой и легендой.
 */
public class GeoMapPanel extends BasicElementPanelBasis {

	public GeoMapPanel(final CompositeContext context1, final DataPanelElementInfo element1) {
		this.setContext(context1);
		this.setElementInfo(element1);

		generalMapPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();

		this.getPanel().addStyleName("geomap-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalMapPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalMapPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalMapPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalMapPanel.add(new HTML(""));
		}

		// generalMapPanel.add(new
		// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading()));
		createChildPanels();
		dataService = GWT.create(DataService.class);

		setMapPanel();

	}

	private HorizontalPanel childLeftPanel;

	private HorizontalPanel childRightPanel;

	private VerticalPanel childTopPanel;

	private VerticalPanel childBottomPanel;

	private void createChildPanels() {
		childLeftPanel = new HorizontalPanel();
		DOM.setElementAttribute(childLeftPanel.getElement(), "id", "left" + getDivIdMap());
		childRightPanel = new HorizontalPanel();
		DOM.setElementAttribute(childLeftPanel.getElement(), "id", "right" + getDivIdMap());
		childTopPanel = new VerticalPanel();
		DOM.setElementAttribute(childLeftPanel.getElement(), "id", "top" + getDivIdMap());
		childBottomPanel = new VerticalPanel();
		DOM.setElementAttribute(childLeftPanel.getElement(), "id", "bottom" + getDivIdMap());
	}

	public GeoMapPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		this.setElementInfo(element1);
		generalHp = new HorizontalPanel();
		this.setContext(null);

		// я бы убрал этот код-конец
		createChildPanels();

		generalMapPanel = new VerticalPanel();

		this.getPanel().addStyleName("geomap-element");
		this.getPanel().addStyleName("id-" + element1.getId().getString());

		if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
			// generalMapPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalMapPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalMapPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		} else {
			generalMapPanel.add(new HTML(""));
		}

		// generalMapPanel.add(new
		// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading()));
	}

	private void setMapPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getGeoMap(getContext(), getElementInfo(), new
		// GWTServiceCallback<GeoMap>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_map_data_retrieving_from_server()) {
		dataService.getGeoMap(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<GeoMap>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_map_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving map data from server")) {

					@Override
					public void onSuccess(final GeoMap aGeoMap) {
						geoMap = aGeoMap;
						if (geoMap != null) {

							super.onSuccess(geoMap);

							fillMapPanel(aGeoMap);

							Scheduler.get().scheduleDeferred(new Command() {
								@Override
								public void execute() {
									for (DataPanelElementInfo el : AppCurrContext
											.getReadyStateMap().keySet()) {
										if (el.getType() == DataPanelElementType.GEOMAP
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
	 * Заполняет виджет карты содержимым. TODO: сделать установку ширины и
	 * высоты div всегда
	 * 
	 * @param aGeoMap
	 *            GeoMap
	 */
	protected void fillMapPanel(final GeoMap aGeoMap) {
		final String divIdLegend =
			getElementInfo().getFullId() + Constants.MAP_LEGEND_DIV_ID_SUFFIX;
		final String div = "<div id='";
		final String htmlForMap;
		final int n60 = 60;
		final int n80 = 80;

		if (aGeoMap.getAutoSize()) {
			// MessageBox.showSimpleMessage("cap",
			// //
			// String.valueOf(GeneralDataPanel.getTabPanel().getOffsetWidth()));
			// MessageBox.showSimpleMessage("cap",
			// String.valueOf(GeneralDataPanel.getTabPanel().getOffsetWidth()));
			final int width = GeneralDataPanel.getTabPanel().getOffsetWidth() - n60;
			final int height = GeneralDataPanel.getTabPanel().getOffsetHeight() - n80;

			htmlForMap =
				div + getDivIdMap() + "' style = 'width: " + String.valueOf(width)
						+ "px; height: " + String.valueOf(height) + "px'></div>";
			aGeoMap.applyAutoSizeValuesOnClient(width, height);

		} else {
			// htmlForMap = div + getDivIdMap() + "'></div>";

			htmlForMap =
				div + getDivIdMap() + "' style = 'width: " + String.valueOf(aGeoMap.getWidth())
						+ "px; height: " + String.valueOf(aGeoMap.getHeight()) + "px'></div>";
			// aGeoMap.applyAutoSizeValuesOnClient(width, height);

		}

		// final String htmlForMap =
		// "<div id='" + divIdMap +
		// "' style = 'width: 900px; height: 600px'></div>";

		final String htmlForLegend = div + divIdLegend + "'></div>";

		footerHTML = new HTML(aGeoMap.getFooter());

		headerHTML = new HTML(aGeoMap.getHeader());

		mapHTML = new HTML(htmlForMap);

		legendHTML = new HTML(htmlForLegend);
		generalMapPanel.clear();
		generalHp.clear();
		generalMapPanel.add(headerHTML);

		generalMapPanel.add(childTopPanel);
		generalHp.add(childLeftPanel);

		if (aGeoMap.getLegendPosition() == null) {
			generalMapPanel.add(generalHp);
			generalHp.add(mapHTML);
		} else {

			switch (aGeoMap.getLegendPosition()) {
			case LEFT:
				generalMapPanel.add(generalHp);
				generalHp.add(legendHTML);
				generalHp.add(mapHTML);
				break;

			case RIGHT:
				generalMapPanel.add(generalHp);
				generalHp.add(mapHTML);
				generalHp.add(legendHTML);
				break;
			case TOP:

				generalMapPanel.add(legendHTML);
				generalMapPanel.add(generalHp);
				generalHp.add(mapHTML);
				break;

			case BOTTOM:

				generalMapPanel.add(generalHp);
				generalHp.add(mapHTML);
				generalMapPanel.add(legendHTML);
				break;

			default:
				break;

			}
		}

		generalMapPanel.add(childBottomPanel);
		generalHp.add(childRightPanel);
		generalMapPanel.add(footerHTML);

		generateButtonsPanel();

		final String paramMap1 = aGeoMap.getJsDynamicData();

		final String paramMap2 = aGeoMap.getTemplate();

		try {
			drawMap(getDivIdMap(), divIdLegend, paramMap1, paramMap2);
		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				// MessageBox.showMessageWithDetails(AppCurrContext.getInstance()
				// .getInternationalizedMessages().error_of_map_painting(),
				// e.getMessage(),
				// GeneralException.generateDetailedInfo(e.getCause()),
				// GeneralException.getMessageType(e.getCause()),
				// GeneralException.needDetailedInfo(e.getCause()));
				MessageBox.showMessageWithDetails(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_map_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Map drawing error"), e.getMessage(), GeneralException
								.generateDetailedInfo(e.getCause()), GeneralException
								.getMessageType(e.getCause()), GeneralException.needDetailedInfo(e
								.getCause()), null);
			} else {
				// MessageBox.showSimpleMessage(AppCurrContext.getInstance()
				// .getInternationalizedMessages().error_of_map_painting(),
				// e.getMessage());
				MessageBox.showSimpleMessage(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_map_painting"),
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"Map drawing error"), e.getMessage());
			}
		}

		checkForDefaultAction();
		setupTimer();

	}

	private void generateButtonsPanel() {
		if (!geoMap.getUiSettings().getButtonsPanelVisible()) {
			return;
		}
		CellPanel buttonsPanel = null;
		switch (geoMap.getUiSettings().getButtonsPanelPosition()) {
		case TOP:
			buttonsPanel = new HorizontalPanel();
			childTopPanel.clear();
			childTopPanel.add(buttonsPanel);
			break;
		case BOTTOM:
			buttonsPanel = new HorizontalPanel();
			childBottomPanel.clear();
			childBottomPanel.add(buttonsPanel);
			break;
		case LEFT:
			buttonsPanel = new VerticalPanel();
			childLeftPanel.clear();
			childLeftPanel.add(buttonsPanel);
			break;
		case RIGHT:
			buttonsPanel = new VerticalPanel();
			childRightPanel.clear();
			childRightPanel.add(buttonsPanel);
			break;
		default:
			break;
		}
		DOM.setElementAttribute(buttonsPanel.getElement(), "id", "buttons" + getDivIdMap());

		if (geoMap.getUiSettings().getExportToPNGButtonVisible()) {
			createButton(buttonsPanel, ImageFormat.PNG);
		}
		if (geoMap.getUiSettings().getExportToJPGButtonVisible()) {
			createButton(buttonsPanel, ImageFormat.JPG);
		}
		if (geoMap.getUiSettings().getExportToSVGButtonVisible()) {
			createButton(buttonsPanel, ImageFormat.SVG);
		}
	}

	private void createButton(final CellPanel buttonsPanel, final ImageFormat imageFormat) {
		final String fileName = "ExportTo" + imageFormat.toString() + ".png";
		Button button =
			new Button("<nobr><img height=\"16\" width=\"16\" src=\"resources/internal/"
					+ fileName + "\"/>", new ClickHandler() {
				@Override
				public void onClick(final ClickEvent aEvent) {
					export(getDivIdMap(), imageFormat.toString());
				}

			});
		buttonsPanel.add(button);
	}

	// CHECKSTYLE:OFF
	private native void export(final String mapId, final String exportType) /*-{
																			$wnd.gwtGeoMapExportSuccess =                   
																			@ru.curs.showcase.app.client.api.GeoMapPanelCallbacksEvents::exportToPNGSuccess(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
																			$wnd.gwtGeoMapExportError =                   
																			@ru.curs.showcase.app.client.api.GeoMapPanelCallbacksEvents::exportToPNGError(Ljava/lang/String;Ljava/lang/String;);

																			//		$wnd.dojo.require("course.geo");		
																			$wnd.course.geo.toSvg(mapId, exportType, $wnd.gwtGeoMapExportSuccess, $wnd.gwtGeoMapExportError);
																			}-*/;

	// CHECKSTYLE:ON

	/**
	 * VerticalPanel на которой отображена карта и легенда.
	 */
	private final VerticalPanel generalMapPanel;

	/**
	 * HorizontalPanel на которой отображена карта и легенда.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * GeoMap geoMap.
	 */
	private GeoMap geoMap = null;

	/**
	 * @return Возвращает текущий объект типа Map - данные карты.
	 */
	public GeoMap getMap() {
		return geoMap;
	}

	/**
	 * Устанавливает текущий объект типа GeoMap - данные карты.
	 * 
	 * @param aGeoMap
	 *            - объект типа GeoMap
	 */
	public void setMap(final GeoMap aGeoMap) {
		this.geoMap = aGeoMap;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для подписи карты в нижней части.
	 */
	private HTML footerHTML = null;

	/**
	 * HTML виждет для подписи карты в заголовной части.
	 */
	private HTML headerHTML = null;

	/**
	 * HTML виждет для карты.
	 */
	private HTML mapHTML = null;

	/**
	 * HTML виждет для легенды карты.
	 */
	private HTML legendHTML = null;

	/**
	 * Ф-ция, возвращающая панель с картой и легендой, если она необходима.
	 * 
	 * @return - Панель с картой и легендой.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalMapPanel;
	}

	// CHECKSTYLE:OFF
	/**
	 * 
	 * Процедура прорисовки карты с помощью библиотеки dojo.
	 * 
	 * @param divIdMap
	 *            - ID для div карты
	 * @param divIdMap
	 *            - ID для div легенды карты
	 * 
	 * 
	 * @param jsonStr1
	 *            - JSON строка с данными карты
	 * @param jsonStr2
	 *            - JSON строка с настройками карты
	 */
	public native void drawMap(final String mapId, final String divIdLegend,
			final String jsonStr1, final String jsonStr2) /*-{
			                                               	$wnd.gwtMapFunc =  @ru.curs.showcase.app.client.api.GeoMapPanelCallbacksEvents::mapPanelClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);

															//$wnd.dojo.require("course.geo");
															// course.geo.makeMap("map", optionSet1, optionSet2, mapConvertorFunc);
															$wnd.course.geo.makeMap(mapId, divIdLegend, jsonStr1, jsonStr2);
															}-*/;

	// CHECKSTYLE:ON

	@Override
	public void reDrawPanel(final CompositeContext context1) {

		this.setContext(context1);
		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			generalMapPanel.clear();
			// generalMapPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalMapPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalMapPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getGeoMap(getContext(), getElementInfo(), new
		// GWTServiceCallback<GeoMap>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_map_data_retrieving_from_server()) {
		dataService.getGeoMap(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<GeoMap>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_map_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving map data from server")) {

					@Override
					public void onSuccess(final GeoMap aGeoMap) {
						geoMap = aGeoMap;
						if (geoMap != null) {

							super.onSuccess(geoMap);

							fillMapPanel(aGeoMap);
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
										.getGeoMapXformTrueStateForReadyStateMap()
										&& !AppCurrContext
												.getInstance()
												.getGridWithToolbarGeoMapTrueStateForReadyStateMap())
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
		generalMapPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalMapPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (geoMap.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(
					geoMap.getActionForDependentElements(), geoMap);
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return geoMap;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			generalMapPanel.clear();
			// generalMapPanel.add(new HTML(AppCurrContext.getInstance()
			// .getInternationalizedMessages().please_wait_data_are_loading()));
			// generalMapPanel.add(new
			// HTML(AppCurrContext.getInstance().getBundleMap()
			// .get("please_wait_data_are_loading")));
			generalMapPanel.add(new HTML("<div class=\"progress-bar\"></div>"));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getGeoMap(getContext(), getElementInfo(), new
		// GWTServiceCallback<GeoMap>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_map_data_retrieving_from_server()) {
		dataService.getGeoMap(
				getContext(),
				getElementInfo(),
				new GWTServiceCallback<GeoMap>(
				// AppCurrContext.getInstance().getBundleMap().get("error_of_map_data_retrieving_from_server"))
				// {
						CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
								"when retrieving map data from server")) {

					@Override
					public void onSuccess(final GeoMap aGeoMap) {
						geoMap = aGeoMap;
						if (geoMap != null) {

							super.onSuccess(geoMap);

							fillMapPanel(aGeoMap);
							getPanel().setHeight("100%");

						}
					}
				});

	}

	private String getDivIdMap() {
		return getElementInfo().getFullId() + Constants.MAP_DIV_ID_SUFFIX;
	}
}
