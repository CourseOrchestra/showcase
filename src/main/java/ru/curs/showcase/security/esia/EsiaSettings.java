package ru.curs.showcase.security.esia;

import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Настройки взаимодействия с ESIA.
 * 
 */
public final class EsiaSettings {

	private static boolean esiaEnable =
		Boolean.valueOf(UserDataUtils.getGeneralOptionalProp("esia.enable"));

	public static boolean isEsiaEnable() {
		return esiaEnable;
	}

	public static void setEsiaEnable(final boolean aEsiaEnable) {
		esiaEnable = aEsiaEnable;
	}

	public static final String CERT_FILE_NAME =
		UserDataUtils.getGeneralOptionalProp("esia.certificate");
	public static final String KEY_FILE_NAME =
		UserDataUtils.getGeneralOptionalProp("esia.privatekey");
	public static final String KEY_PASS =
		UserDataUtils.getGeneralOptionalProp("esia.privatekey.password");

	public static final String VALUE_CLIENT_ID =
		UserDataUtils.getGeneralOptionalProp("esia.param.clientid");
	public static final String VALUE_REDIRECT_URI =
		UserDataUtils.getGeneralOptionalProp("esia.param.redirecturi");
	public static final String VALUE_LOGOUT_REDIRECT_URI =
		UserDataUtils.getGeneralOptionalProp("esia.param.logoutredirecturi");
	public static final String VALUE_SCOPE =
		UserDataUtils.getGeneralOptionalProp("esia.param.scope");

	public static final String URL_BASE = UserDataUtils.getGeneralOptionalProp("esia.url");

	public static final boolean ALLOW_AUTHENTICATE_ONLY_TRUSTED_USER = Boolean.valueOf(
			UserDataUtils.getGeneralOptionalProp("esia.allow.authenticate.only.trusted.user"));

	private EsiaSettings() {
	}

}
