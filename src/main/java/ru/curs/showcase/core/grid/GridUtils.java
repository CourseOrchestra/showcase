package ru.curs.showcase.core.grid;

import org.w3c.dom.*;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

import ru.curs.lyra.LyraFieldType;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Утилиты серверной части грида.
 * 
 */
public final class GridUtils {

	public static final String GRID_DIR = "js/ui/grids";

	private static final String FILTER_TAG = "filter";

	private GridUtils() {
		throw new UnsupportedOperationException();
	}

	public static void fillFilterContextByFilterInfo(final GridContext gridContext)
			throws Exception {
		fillFilterContextByFilterOrListOfValuesInfo(gridContext, false);
	}

	public static void fillFilterContextByListOfValuesInfo(final GridContext gridContext)
			throws Exception {
		fillFilterContextByFilterOrListOfValuesInfo(gridContext, true);
	}

	private static void fillFilterContextByFilterOrListOfValuesInfo(final GridContext gridContext,
			final boolean isListOfValues) throws Exception {
		if (gridContext.getGridFilterInfo().getFilters().size() > 0) {
			String filterContext = gridContext.getFilter();
			if ((filterContext == null) || filterContext.isEmpty()) {
				filterContext = "<" + FILTER_TAG + "></" + FILTER_TAG + ">";
			}
			Document doc = XMLUtils.stringToDocument(filterContext);

			Document docFilterInfo;
			if (isListOfValues) {
				docFilterInfo = XMLUtils.objectToXML(gridContext.getGridListOfValuesInfo());
			} else {
				docFilterInfo = XMLUtils.objectToXML(gridContext.getGridFilterInfo());
			}
			Element inserted = docFilterInfo.getDocumentElement();
			Element child = (Element) doc.importNode(inserted, true);
			doc.getElementsByTagName(FILTER_TAG).item(0).appendChild(child);

			String result = XMLUtils.documentToString(doc);
			gridContext.setFilter(result);
		}
	}

	public static GridValueType getGridValueTypeByLyraFieldType(final LyraFieldType lft,
			final String subtype) {
		switch (lft) {
		case BLOB:
			return GridValueType.STRING;
		case BIT:
			return GridValueType.STRING;
		case DATETIME:
			return GridValueType.DATETIME;
		case REAL:
			return GridValueType.FLOAT;
		case INT:
			return GridValueType.INT;
		case VARCHAR:
			if (subtype != null) {
				switch (subtype.toUpperCase()) {
				case "IMAGE":
					return GridValueType.IMAGE;
				case "LINK":
					return GridValueType.LINK;
				case "DOWNLOAD":
					return GridValueType.DOWNLOAD;
				default:
					break;
				}
			}
			return GridValueType.STRING;
		default:
			return GridValueType.STRING;
		}
	}

	public static String getSerializeUserMessage(final UserMessage um) {
		String message = null;
		try {
			message = RPC.encodeResponseForSuccess(
					FakeService.class.getMethod("serializeUserMessage"), um);
			message = replaceServiceSymbols(message);
		} catch (SerializationException | NoSuchMethodException | SecurityException e) {
			throw GeneralExceptionFactory.build(e);
		}
		return message;
	}

	private static String replaceServiceSymbols(final String mess) {
		String ret = mess;
		ret = ret.replace("\\x", ExchangeConstants.OK_MESSAGE_X);
		ret = ret.replace("\\\"", ExchangeConstants.OK_MESSAGE_QUOT);
		return ret;
	}

}
