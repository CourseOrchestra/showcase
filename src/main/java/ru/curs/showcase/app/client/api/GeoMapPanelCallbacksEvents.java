package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.google.gwt.regexp.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * @author anlug
 * 
 *         Класс реализующий функции обратного вызова из карты (Map).
 * 
 */
public final class GeoMapPanelCallbacksEvents {

	private GeoMapPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на карте (на Map).
	 * 
	 * @param mapDivId
	 *            - Id карты (ID тэга div для карты)
	 * @param featureId
	 *            - ID нажатого объекта (области или точки) карты
	 */

	// public static void mapPanelClick(final String mapDivId, final String
	// featureId) {
	//
	// // MessageBox.showSimpleMessage("Тест карты",
	// // "Сообщение вызвано при нажатии на карте "
	// // + mapDivId + " из gwt кода на объекте " + featureId);
	//
	// GeoMap gm = getPanel(mapDivId).getMap();
	//
	// List<GeoMapEvent> events =
	// gm.getEventManager().getEventForFeature(featureId);
	// for (GeoMapEvent gmev : events) {
	// AppCurrContext.getInstance().setCurrentActionFromElement(gmev.getAction(),
	// gm);
	// ActionExecuter.execAction();
	// }
	// }

	/**
	 * 
	 * Событие одинарного клика на карте (на Map).
	 * 
	 * @param mapDivId
	 *            - Id карты (ID тэга div для карты)
	 * @param featureId
	 *            - ID нажатого объекта (области или точки) карты
	 */

	public static void mapPanelClick(final String mapDivId, final String featureId,
			final String overridenValue, final String replaseWhat) {

		// MessageBox.showSimpleMessage("Тест карты",
		// "Сообщение вызвано при нажатии на карте "
		// + mapDivId + " из gwt кода на объекте " + featureId);

		GeoMap gm = getPanel(mapDivId).getMap();

		// =======
		ActionFieldType actionFieldType = ActionFieldType.ADD_CONTEXT;
		if (replaseWhat != null) {
			actionFieldType = ActionFieldType.valueOf(replaseWhat.toUpperCase());
		}
		// =====
		List<GeoMapEvent> events = gm.getEventManager().getEventForFeature(featureId);
		for (GeoMapEvent gmev : events) {

			// =====

			Action ac = gmev.getAction().gwtClone();
			if (overridenValue != null) {
				switch (actionFieldType) {
				case ADD_CONTEXT:
					ac.setAdditionalContext(overridenValue);
					break;
				case MAIN_CONTEXT:
					ac.setMainContext(overridenValue);
					break;
				case FILTER_CONTEXT:
					ac.filterBy(overridenValue);
					break;
				case ELEMENT_ID:
					// if (ac.getDataPanelActionType() !=
					// DataPanelActionType.DO_NOTHING) {
					// String elID = id;
					// DataPanelElementLink link = null;
					// if (elID != null) {
					// link = ac.getDataPanelLink().getElementLinkById(elID);
					// } else {
					// if (ac.getDataPanelLink().getElementLinks().size() > 0) {
					// link = ac.getDataPanelLink().getElementLinks().get(0);
					// }
					// }
					// if (link != null) {
					// link.setId(overridenValue);
					// } else {
					// MessageBox.showSimpleMessage("Ошибка",
					// "Элемент действия для замены ID неверно определен (ID = "
					// + elID + ")");
					// }
					// }
					break;
				default:
					break;
				}
			}
			// =====

			AppCurrContext.getInstance().setCurrentActionFromElement(ac, gm);
			ActionExecuter.execAction();
		}
	}

	public static GeoMapPanel getPanel(final String mapDivId) {
		String elementId = getElementIdByGeomapDivId(mapDivId);
		return (GeoMapPanel) ActionExecuter.getElementPanelById(elementId);
	}

	public static String getElementIdByGeomapDivId(final String mapDivId) {
		String elementId = null;
		RegExp pattern = RegExp.compile("dpe_.*__(.*)" + Constants.MAP_DIV_ID_SUFFIX, "i");
		MatchResult res = pattern.exec(mapDivId);
		if (res.getGroupCount() == 2) {
			elementId = res.getGroup(1);
		}
		return elementId;
	}

	/**
	 * Обязательно выставляем ENCODING_MULTIPART - иначе будут проблемы с
	 * большими картами - например, картой мира.
	 * 
	 */
	public static void exportToPNGSuccess(final String mapDivId, final String imageFormat,
			final String svg) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.clear();

		// dh.setErrorCaption(AppCurrContext.getInstance().getInternationalizedMessages()
		// .export_to_png_error());
		dh.setErrorCaption(
		// AppCurrContext.getInstance().getBundleMap().get("export_to_png_error"));
		CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
				"PNG file downloading error"));
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/geoMapExport"
				+ com.google.gwt.user.client.Window.Location.getQueryString());
		dh.setEncoding(FormPanel.ENCODING_MULTIPART);
		try {
			GeoMap map = getPanel(mapDivId).getMap();
			SerializationStreamFactory ssf = dh.getAddObjectSerializer();
			dh.addParam(map.getExportSettings().getClass().getName(), map.getExportSettings()
					.toParamForHttpPost(ssf));
			dh.addParam(ImageFormat.class.getName(), imageFormat);
			dh.addParam("svg", svg);
			dh.submit();
		} catch (SerializationException e) {
			// MessageBox.showSimpleMessage(AppCurrContext.getInstance()
			// .getInternationalizedMessages().export_to_png_error(),
			// e.getMessage());
			MessageBox.showSimpleMessage(
			// AppCurrContext.getInstance().getBundleMap().get("export_to_png_error"),
					CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
							"PNG file downloading error"), e.getMessage());
		}
	}

	public static void exportToPNGError(final String mapDivId, final String error) {
		// MessageBox.showSimpleMessage(AppCurrContext.getInstance().getInternationalizedMessages()
		// .export_to_png_error()
		// + "(djeo)", error);
		MessageBox.showSimpleMessage(
		// AppCurrContext.getInstance().getBundleMap().get("export_to_png_error")
				CourseClientLocalization.gettext(AppCurrContext.getInstance().getDomain(),
						"PNG file downloading error") + "(djeo)", error);
	}
}
