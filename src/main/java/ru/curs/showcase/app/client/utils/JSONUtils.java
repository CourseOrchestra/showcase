package ru.curs.showcase.app.client.utils;

import com.google.gwt.json.client.*;

/**
 * Утилиты для работы с JSON.
 * 
 * @author bogatov
 * 
 */
public final class JSONUtils {

	private JSONUtils() {

	}

	public static String createXmlByJSONValue(final String name, final JSONValue jsonVal) {
		StringBuilder sb = new StringBuilder();
		if (jsonVal == null || jsonVal.isNull() != null) {
			sb.append("<").append(name).append(">").append("</").append(name).append(">");
		} else if (jsonVal.isObject() != null) {
			sb.append("<").append(name).append(">");
			JSONObject jsonObject = jsonVal.isObject();
			for (String key : jsonObject.keySet()) {
				sb.append(createXmlByJSONValue(key, jsonObject.get(key)));
			}
			sb.append("</").append(name).append(">");
		} else if (jsonVal.isArray() != null) {
			JSONArray jsonArray = jsonVal.isArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				sb.append(createXmlByJSONValue(name, jsonArray.get(i)));
			}
		} else {
			sb.append("<").append(name).append(">");
			String val;
			if (jsonVal.isString() != null) {
				JSONString jsonString = jsonVal.isString();
				val = jsonString.stringValue();
			} else {
				val = jsonVal.toString();
			}
			sb.append(val);
			sb.append("</").append(name).append(">");
		}
		return sb.toString();
	}

}
