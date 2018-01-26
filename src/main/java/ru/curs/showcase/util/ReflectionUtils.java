package ru.curs.showcase.util;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import ru.curs.showcase.app.api.ExcludeFromSerialization;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;

/**
 * Статический класс, содержащий общие функции для работы с Java Reflection.
 * 
 * @author den
 * 
 */
public final class ReflectionUtils {

	/**
	 * Возвращает значение свойства по имени поля, используя для доступа get
	 * метод.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static Object getPropValueForField(final Object obj, final Field field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return getPropValueByFieldName(obj, field.getName());
	}

	/**
	 * Метод не должен вызывать исключения, наследованные от BaseException по
	 * причине того, что используется в механизме вывода "веб-консоли". А запись
	 * нового события в лог во время процесса вывода, происходящая в том же
	 * потоке, приводит к ConcurrentModificationException.
	 */
	public static Object getPropValueByFieldName(final Object obj, final String fieldName)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String metName = getAccessMethodNameForField(fieldName);
		Method met = obj.getClass().getMethod(metName);
		return met.invoke(obj);
	}

	private static String getAccessMethodNameForField(final String fldName) {
		if (fldName.startsWith("is")) {
			return fldName;
		} else {
			return String.format("get%s%s", fldName.substring(0, 1).toUpperCase(),
					fldName.substring(1));
		}
	}

	private ReflectionUtils() {
		throw new UnsupportedOperationException();
	}

	public static String getProcessDescForClass(final Class<?> classLink) {
		if (classLink.isAnnotationPresent(Description.class)) {
			return classLink.getAnnotation(Description.class).process();
		} else if ((classLink.getEnclosingClass() != null)
				&& (classLink.getEnclosingClass().isAnnotationPresent(Description.class))) {
			return classLink.getEnclosingClass().getAnnotation(Description.class).process();
		}
		return "не задан";
	}

	private static boolean equals(final Object first, final Object second,
			final Class<?> comparedClass) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (hasClassRightEqualsMethod(comparedClass)) {
			return first.equals(second);
		}
		for (Field field : comparedClass.getDeclaredFields()) {
			if (field.getAnnotation(ExcludeFromSerialization.class) != null) {
				continue;
			}
			if (!isProperty(field.getModifiers())) {
				continue;
			}
			Object value1;
			Object value2;
			try {
				value1 = getPropValueByFieldName(first, field.getName());
				value2 = getPropValueByFieldName(second, field.getName());
			} catch (NoSuchMethodException e) {
				continue;
			}
			if (!equals(value1, value2)) {
				return false;
			}
		}

		if (comparedClass.getSuperclass() != Object.class) {
			return equals(first, second, comparedClass.getSuperclass());
		} else {
			return true;
		}
	}

	private static boolean hasClassRightEqualsMethod(final Class<?> comparedClass) {
		final Class<?> superclass = comparedClass.getSuperclass();
		boolean res =
			(superclass == null) || (superclass == Number.class) || comparedClass.isArray()
					|| Collection.class.isAssignableFrom(comparedClass)
					|| Map.class.isAssignableFrom(comparedClass);
		if (!res) {
			try {
				comparedClass.getDeclaredMethod("equals", Object.class);
				return true;
			} catch (NoSuchMethodException e) {
				return false;
			}
		}
		return res;
	}

	public static boolean equals(final Object first, final Object second)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (first == second) {
			return true;
		}
		if ((first == null) || (second == null)) {
			return false;
		}
		if (first.getClass() != second.getClass()) {
			return false;
		}
		return equals(first, second, first.getClass());
	}

	private static boolean isProperty(final int modifier) {
		return Modifier.isPrivate(modifier) && !Modifier.isStatic(modifier)
				&& !Modifier.isTransient(modifier) && !Modifier.isFinal(modifier);
	}

	public static int getObjectSizeBySerialize(final Serializable object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		return baos.size();
	}
}
