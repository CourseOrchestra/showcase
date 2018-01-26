package ru.curs.showcase.app.api.services;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.plugin.*;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный "двойник" основного GWT-RPC интерфейса приложения - DataService.
 * Особенностью всех функций класса является возврат void и обязательное наличие
 * параметра AsyncCallback<YYY> callback. Данные функции генерируются
 * автоматически средствами Eclipse из DataService! Подробнее о назначении
 * класса можно прочитать в документации по GWT.
 */
public interface DataServiceAsync {

	void getNavigator(CompositeContext context, AsyncCallback<Navigator> callback);

	void getDataPanel(Action action, AsyncCallback<DataPanel> callback);

	void getWebText(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<WebText> callback);

	void getGridMetadata(GridContext context, DataPanelElementInfo element,
			AsyncCallback<GridMetadata> callback);

	void getGridData(GridContext context, DataPanelElementInfo element,
			AsyncCallback<GridData> callback);

	void getLyraGridMetadata(LyraGridContext context, DataPanelElementInfo element,
			AsyncCallback<LyraGridMetadata> callback);

	void getChart(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<Chart> callback);

	void getGeoMap(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<GeoMap> callback);

	void getXForms(XFormContext context, DataPanelElementInfo element,
			AsyncCallback<XForm> callback);

	void getMainXForms(AsyncCallback<List<String>> callback);

	void saveXForms(XFormContext context, DataPanelElementInfo element,
			AsyncCallback<VoidElement> callback);

	void getServerCurrentState(CompositeContext context, AsyncCallback<ServerState> callback);

	void execServerAction(Action action, AsyncCallback<VoidElement> callback);

	void getMainPage(CompositeContext context, AsyncCallback<MainPage> callback);

	void getPlugin(RequestData requestData, AsyncCallback<Plugin> callback);

	void writeToLog(CompositeContext aContext, String message, MessageType messageType,
			AsyncCallback<Void> callback);

	void getGridToolBar(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<GridToolBar> callback);

	void getPluginData(RequestData request, AsyncCallback<ResponceData> callback);

	void getBundle(CompositeContext context, AsyncCallback<Map<String, String>> callback);

	void getLocalizationBundleDomainName(CompositeContext context, AsyncCallback<String> callback);

	void copyToClipboard(String message, AsyncCallback<Void> callback);

	void fakeRPC(AsyncCallback<Void> callback);
}
