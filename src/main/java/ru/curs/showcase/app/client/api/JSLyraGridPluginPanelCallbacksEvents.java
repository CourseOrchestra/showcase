package ru.curs.showcase.app.client.api;

import com.google.gwt.json.client.JSONObject;

import ru.curs.showcase.app.client.JSLyraGridPluginPanel;

/**
 * Класс, реализующий функции обратного вызова из JSLyraGridPluginPanel.
 * 
 */
public final class JSLyraGridPluginPanelCallbacksEvents {

	private JSLyraGridPluginPanelCallbacksEvents() {
	}

	/**
	 * Возвращает текущую JSLyraGridPluginPanel.
	 * 
	 * @param pluginId
	 *            - Id элемента плагина.
	 * 
	 * @return PageGridPluginPanel
	 */
	private static JSLyraGridPluginPanel getCurrentPanel(final String pluginId) {
		return (JSLyraGridPluginPanel) ActionExecuter.getElementPanelById(pluginId);
	}

	public static JSONObject pluginGetHttpParams(final String pluginId, final String offset,
			final String limit, final String sortColId, final String sortColDir,
			final String refreshId) {
		return getCurrentPanel(pluginId).pluginGetHttpParams(Integer.parseInt(offset),
				Integer.parseInt(limit), sortColId, sortColDir, refreshId);
	}

	public static JSONObject pluginEditorGetHttpParams(final String pluginId, final String data,
			final String editorType) {
		return getCurrentPanel(pluginId).pluginEditorGetHttpParams(data, editorType);
	}

	public static void pluginAfterLoadData(final String pluginId, final String stringEvents,
			final String stringAddData, final String totalCount) {
		getCurrentPanel(pluginId).pluginAfterLoadData(stringEvents, stringAddData, totalCount);
	}

	public static void pluginAfterPartialUpdate(final String pluginId, final String stringEvents) {
		getCurrentPanel(pluginId).pluginAfterPartialUpdate(stringEvents);
	}

	public static void pluginAfterClick(final String pluginId, final String recId,
			final String colId, final String stringSelectedRecordIds) {
		getCurrentPanel(pluginId).pluginAfterClick(recId, colId, stringSelectedRecordIds);
	}

	public static void pluginAfterDoubleClick(final String pluginId, final String recId,
			final String colId, final String stringSelectedRecordIds) {
		getCurrentPanel(pluginId).pluginAfterDoubleClick(recId, colId, stringSelectedRecordIds);
	}

	public static void pluginProcessFileDownload(final String pluginId, final String recId,
			final String colId) {
		getCurrentPanel(pluginId).pluginProcessFileDownload(recId, colId);
	}

	public static void pluginShowMessage(final String pluginId, final String stringMessage,
			final String editorType) {
		getCurrentPanel(pluginId).pluginShowMessage(stringMessage, editorType);
	}

	public static void pluginShowErrorMessage(final String pluginId, final String stringMessage) {
		getCurrentPanel(pluginId).pluginShowErrorMessage(stringMessage);
	}

	public static void pluginSetOldPosition(final String pluginId, final String oldPosition) {
		getCurrentPanel(pluginId).pluginSetOldPosition(oldPosition);
	}

}
