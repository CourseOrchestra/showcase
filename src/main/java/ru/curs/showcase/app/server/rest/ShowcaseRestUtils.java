package ru.curs.showcase.app.server.rest;

import ru.curs.showcase.runtime.UserDataUtils;

public class ShowcaseRestUtils {

	/**
	 * Получение типа аутентификации из свойства в файле generalapp.properties.
	 * 
	 * @return тип аутентификации в виде значения объекта Enum
	 */
	public static RestAuthenticationType getRestAuthenticationType() {
		RestAuthenticationType restAuthenticationTypeEnumValue = RestAuthenticationType.SIMPLE;

		String restAuthType = UserDataUtils.getGeneralOptionalProp("rest.authentication.type");
		if (restAuthType.trim().equalsIgnoreCase("celesta"))
			restAuthenticationTypeEnumValue = RestAuthenticationType.CELESTA;

		return restAuthenticationTypeEnumValue;
	}
}
