package ru.curs.showcase.util;

import java.lang.reflect.Modifier;

import ru.curs.showcase.app.api.ExcludeFromSerialization;

import com.google.gson.*;

/**
 * Сериализатор в формате JSON.
 * 
 * @author den
 * 
 */
public class JSONObjectSerializer implements ObjectSerializer {

	private final ExclusionStrategy logExclusionStrategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(final Class<?> aClass) {
			return false;
		}

		@Override
		public boolean shouldSkipField(final FieldAttributes fa) {
			return fa.getAnnotation(ExcludeFromSerialization.class) != null;
		}
	};

	private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
			.setExclusionStrategies(logExclusionStrategy).serializeNulls()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC).create();

	@Override
	public String serialize(final Object aObj) {
		return gson.toJson(aObj);
	}

}
