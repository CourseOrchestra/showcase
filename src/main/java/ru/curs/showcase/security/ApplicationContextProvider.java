package ru.curs.showcase.security;

import org.springframework.beans.BeansException;
import org.springframework.context.*;

/**
 * Класс, представляющий собой контекст для доступа к бинам spring-security.
 * 
 * @author s.borodanev
 *
 */

public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		context = ac;
	}
}