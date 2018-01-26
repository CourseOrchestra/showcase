package ru.curs.showcase.runtime;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.security.oauth.Oauth2Token;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Вспомогательные функции для получение информации о текущей сессии.
 * 
 * @author anlug
 * 
 */
public final class SessionUtils {

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	public static final String TEST_SESSION = "testSession";
	public static final String TEST_SID = "testSID";

	// Переменная, которая задействуется при анонимном входе в приложение.
	private static UserAndSessionDetails usd = null;

	private SessionUtils() {
		throw new UnsupportedOperationException();
	}

	public static void setAnonymousUserAndSessionDetails(
			UserAndSessionDetails userAndSessionDetails) {
		usd = userAndSessionDetails;
	}

	public static UserAndSessionDetails getAnonymousUserAndSessionDetails() {
		return usd;
	}

	private static UserAndSessionDetails getUserAndSessionDetails() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)
				return usd;
			return (UserAndSessionDetails) SecurityContextHolder.getContext().getAuthentication()
					.getDetails();
		} else {
			return null;
		}
	}

	/**
	 * Возвращает имя пользователя из текущей сессии приложения.
	 * 
	 * @return - имя пользователя.
	 */
	public static String getCurrentSessionUserName() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		} else {
			return "";
		}
	}

	/**
	 * Возвращает идентификатор текущей сессии приложения.
	 * 
	 * @return - идентификатор текущей сессии приложения.
	 */
	public static String getCurrentSessionId() {
		if (getUserAndSessionDetails() != null) {
			String sessionID = getUserAndSessionDetails().getSessionId();
			if (sessionID == null) {
				return AppInfoSingleton.getAppInfo().getSesid();
			}
			return getUserAndSessionDetails().getSessionId();
		} else {
			return TEST_SESSION;
		}
	}

	public static String getCurrentUserSID() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getSid();
		} else {
			return TEST_SID;
		}

	}

	public static String getCurrentUserEmail() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getEmail();
		} else {
			return null;
		}
	}

	public static String getCurrentUserFullName() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getFullName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserPhone() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getPhone();
		} else {
			return null;
		}
	}

	public static String getCurrentUserFirstName() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getFirstName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserLastName() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getLastName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserMiddleName() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getMiddleName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserTrusted() {
		if (getUserAndSessionDetails() != null) {
			return String.valueOf(getUserAndSessionDetails().getUserInfo().isTrusted());
		} else {
			return null;
		}
	}

	public static String getRemoteAddress() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getRemoteAddress();
		}
		return null;
	}

	public static Oauth2Token getOauth2Token() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getOauth2Token();
		}
		return null;
	}

	public static String getCurrentUserCaption() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getCaption();
		}
		return null;
	}

	// public static String getAdditionalParameter() {
	// if (getUserAndSessionDetails() != null) {
	// return getUserAndSessionDetails().getUserInfo().getAdditionalParameter();
	// }
	// return null;
	// }

	public static String[] getAdditionalParameters() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getAdditionalParameters();
		}
		return null;
	}

	public static String getCurrentUserSnils() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getSnils();
		} else {
			return null;
		}
	}

	public static String getCurrentUserGender() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getGender();
		} else {
			return null;
		}
	}

	public static String getCurrentUserBirthDate() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getBirthDate();
		} else {
			return null;
		}
	}

	public static String getCurrentUserBirthPlace() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getBirthPlace();
		} else {
			return null;
		}
	}

}
