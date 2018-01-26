package ru.curs.showcase.runtime;

import java.io.IOException;
import java.util.*;

import javax.xml.transform.TransformerConfigurationException;

/**
 * Абстрактный класс для пула повторно используемых объектов, разделенных на
 * группы по строковому ключу.
 * 
 * @author den
 * @param <T>
 *            - тип повторно используемого объекта.
 */
public abstract class Pool<T> {

	protected abstract Pool<T> getLock();

	public Pool() {
		super();
	}

	private final Map<String, List<T>> poolByKey = new HashMap<String, List<T>>();

	public T acquire(final String key) throws TransformerConfigurationException, IOException {
		synchronized (getLock()) {
			List<T> current = getReusables(key);
			if (current.size() > 0) {
				T result = current.remove(0);
				if (checkForValidity(result)) {
					return result;
				}
				return createReusableItem(key);
			}
			return createReusableItem(key);
		}
	}

	protected boolean checkForValidity(final T aResult) {
		return true;
	}

	protected abstract T createReusableItem(final String key)
			throws TransformerConfigurationException, IOException;

	protected List<T> getReusables(final String key) {
		List<T> current = poolByKey.get(key);
		if (current == null) {
			current = new ArrayList<T>();
			poolByKey.put(key, current);
		}
		return current;
	}

	public void release(final T reusable, final String key) {
		synchronized (getLock()) {
			List<T> current = getReusables(key);
			cleanReusable(reusable);
			current.add(reusable);
		}
	}

	protected void cleanReusable(final T aReusable) {
		// по умолчанию ничего не делаем
	}

	public T acquire() throws TransformerConfigurationException, IOException {
		return acquire(getThreadKey());
	}

	public void release(final T reusable) {
		release(reusable, getThreadKey());
	}

	/**
	 * Возвращает некий постоянный ключ, связанный с текущим потоком или
	 * сессией.
	 */
	protected String getThreadKey() {
		return null;
	}

	public void clear() {
		synchronized (getLock()) {
			poolByKey.clear();
		}
	}

	public int getAllCount() {
		int result = 0;
		for (List<T> list : poolByKey.values()) {
			result = result + list.size();
		}
		return result;
	}
}