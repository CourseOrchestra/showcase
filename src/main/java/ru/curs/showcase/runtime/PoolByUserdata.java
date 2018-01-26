package ru.curs.showcase.runtime;

import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;

/**
 * Абстрактный класс для пула повторно используемых объектов, разделенных по
 * userdata.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип повторно используемого объекта.
 */
public abstract class PoolByUserdata<T> extends Pool<T> {

	public PoolByUserdata() {
		super();
	}

	protected abstract T createReusableItem();

	@Override
	public T acquire() {
		try {
			return super.acquire();
		} catch (TransformerConfigurationException | IOException e) {
			// это наследуемые исключения. в реальности их не будет. TODO убрать
			// вообще
			return null;
		}
	}

	@Override
	protected T createReusableItem(final String aKey) throws TransformerConfigurationException,
			IOException {
		return createReusableItem();
	}

	@Override
	protected String getThreadKey() {
		return AppInfoSingleton.getAppInfo().getCurUserDataId();
	}
}