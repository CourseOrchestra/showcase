package ru.curs.showcase.security;

/**
 * Класс для изменения доступа к приложению (анонимный или поностью
 * аутентифицированный вход).
 * 
 * @author s.borodanev
 *
 */
public class CustomAccessProvider {

	private static String access = "fullyAuthenticated";

	public static String getAccess() {
		return access;
	}

	public static void setAccess(String anAccess) {
		access = anAccess;
	}

	public synchronized String getReturnStringMethod() {
		return getAccess();
	}

}
