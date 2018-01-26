package ru.curs.showcase.core;

import java.lang.reflect.*;

import ru.curs.showcase.app.api.*;

import com.google.gson.*;

/**
 * Класс адаптера для использования объектов в JS коде.
 * 
 * @author den
 * 
 */
public class AdapterForJS {
	/**
	 * Специальный сериализатор для ID.
	 * 
	 * @author den
	 * 
	 */
	private class IDSerializer implements JsonSerializer<ID> {
		@Override
		public JsonElement serialize(final ID src, final Type typeOfSrc,
				final JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}

	/**
	 * Функция подготовки Java-объекта для использования в JS коде. Подготовка
	 * заключается в построении JSON объекта по Java объекту.
	 * 
	 * @param source
	 *            - Java-объект для обработки.
	 */
	public void adapt(final JSONObject source) {
		GsonBuilder builder =
			new GsonBuilder().serializeNulls()
					.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC)
					.registerTypeAdapter(ID.class, new IDSerializer());
		Gson gson = builder.create();

		String json = gson.toJson(source.getJavaDynamicData());
		source.setJsDynamicData(json);
	}
}
