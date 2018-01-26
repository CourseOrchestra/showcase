package ru.curs.showcase.util;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.*;

import ru.curs.showcase.util.exception.UTF8Exception;

/**
 * Класс, содержащий общие функции для работы с текстом.
 * 
 * @author den
 * 
 */
public final class TextUtils {

	/**
	 * Кодировка по умолчанию в приложении. Все выходные и входные документы по
	 * умолчанию должны имеют данную кодировку (если явно не указано другое).
	 */
	public static final String DEF_ENCODING = "UTF-8";

	/**
	 * Кодировка, используемая в JDBC.
	 */
	public static final String JDBC_ENCODING = "UTF-16";

	public static final String UTF8_BOM = "\uFEFF";

	private TextUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Преобразует InputStream в кодировке DEF_ENCODING в строку. Может
	 * использоваться при работе с Gateway классами и\или при отладке.
	 * 
	 * @param is
	 *            - InputStream
	 * @return - строка с содержимым InputStream.
	 * @throws IOException
	 */
	public static String streamToString(final InputStream is) throws IOException {
		return streamToString(is, DEF_ENCODING);
	}

	/**
	 * Преобразует InputStream в кодировке encoding в строку. Может
	 * использоваться при работе с Gateway классами и\или при отладке.
	 * 
	 * @param is
	 *            - InputStream
	 * @return - строка с содержимым InputStream.
	 * @throws IOException
	 */
	public static String streamToString(final InputStream is, final String encoding)
			throws IOException {
		if (is == null) {
			return "";
		}

		Writer writer = new StringWriter();
		final int bufMaxLen = 4096;
		char[] buffer = new char[bufMaxLen];
		try (Reader reader = new BufferedReader(new InputStreamReader(is, encoding))) {
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		}
		return writer.toString();
	}

	public static String removeUTF8BOM(final String in) {
		String s = in;
		if (s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}

	/**
	 * Преобразует строку в InputStream. Может использоваться при работе с
	 * Gateway классами и\или при отладке.
	 * 
	 * @param str
	 *            - строка.
	 * @return - InputStream.
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream stringToStream(final String str) {
		try {
			return new ByteArrayInputStream(str.getBytes(DEF_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new UTF8Exception();
		}

	}

	/**
	 * Преобразует строку в кодировке encoding в InputStream. Может
	 * использоваться при работе с Gateway классами и\или при отладке.
	 * 
	 * @param str
	 *            - строка.
	 * @param encoding
	 *            - кодировка.
	 * @return - InputStream.
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream stringToStream(final String str, final String encoding) {
		try {
			return new ByteArrayInputStream(str.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Неподдерживаемая кодировка");
		}

	}

	/**
	 * Возвращает числовое значение размера, извлеченное из переданной строки.
	 * 
	 * @param value
	 *            - строка с размером.
	 * @return - числовое значение.
	 */
	public static Integer getIntSizeValue(final String value) {
		Integer intValue = null;
		String strValue;
		if (value.indexOf("px") > -1) {
			strValue = value.substring(0, value.indexOf("px"));
			intValue = Integer.valueOf(strValue); // TODO проверить
		}
		return intValue;
	}

	/**
	 * Проверяет, является ли переданная строка целым числом.
	 * 
	 * @param value
	 *            - строка для проверки
	 * @return - true/false
	 */
	public static boolean isInteger(final String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Функция, возвращающая исходное слово, начинающееся с заглавной буквы. Все
	 * остальные буквы результата будут строчные.
	 * 
	 * @param source
	 *            - исходная строка.
	 * @return - преобразованная строка.
	 */
	public static String capitalizeWord(final String source) {
		return String.format("%s%s", source.substring(0, 1).toUpperCase(), source.substring(1));
	}

	/**
	 * Функция нечувствительной к реестру замены.
	 * 
	 * @param template
	 *            - шаблон для замены.
	 * @param source
	 *            - исходная строка.
	 * @param newValue
	 *            - значение для замены
	 * @return - результат после замены.
	 */
	public static String replaceCI(final String source, final String template,
			final String newValue) {
		Pattern pattern =
			Pattern.compile(template, Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(source);
		String result = matcher.replaceAll(newValue);
		return result;
	}

	/**
	 * Перекодирует строку из неправильно определенной кодировки в правильную.
	 * 
	 * @param source
	 *            - исходный текст.
	 * @param sourceCharset
	 *            - кодировка исходного текста.
	 * @param destCharset
	 *            - правильная кодировка.
	 * @return - строка в верной кодировке.
	 * @throws UnsupportedEncodingException
	 */
	public static String recode(final String source, final String sourceCharset,
			final String destCharset) throws UnsupportedEncodingException {
		return new String(source.getBytes(sourceCharset), destCharset);
	}

	/**
	 * Функция, возвращающая строку с текущей датой.
	 * 
	 * @return строка с датой.
	 */
	public static String getCurrentLocalDate() {
		DateFormat df = DateFormat.getDateTimeInstance();
		return df.format(new Date());
	}

	/**
	 * Возвращает имя файла без пути и расширения.
	 * 
	 * @param path
	 *            - полный путь к файлу.
	 * @return - имя файла.
	 */
	public static String extractFileName(final String path) {
		if (path == null) {
			return null;
		}

		int dotPos = path.lastIndexOf('.');
		int slashPos = path.lastIndexOf('\\');
		if (slashPos == -1) {
			slashPos = path.lastIndexOf('/');
		}

		int beginIndex = slashPos > 0 ? slashPos + 1 : 0;
		int endIndex = dotPos > slashPos ? dotPos : path.length();

		return path.substring(beginIndex, endIndex);
	}

	/**
	 * Возвращает имя файла с расширением из полного пути.
	 * 
	 * @param path
	 *            - путь к файлу.
	 * @return - имя с расширением.
	 */
	public static String extractFileNameWithExt(final String path) {
		if (path == null) {
			return null;
		}

		File file = new File(path);
		return file.getName();
	}

	/**
	 * Возвращает расширение файла из полного пути.
	 * 
	 * @param path
	 *            - путь к файлу.
	 * @return - расширение.
	 */
	public static String extractFileExt(final String path) {
		if (path == null) {
			return null;
		}

		int index = path.lastIndexOf('.');
		return index == -1 ? null : path.substring(index);
	}

	/**
	 * Правильное преобразование массива в строку. Arrays.toString не выставляет
	 * переводы строк.
	 */
	public static String arrayToString(final String[] array, final String separator) {
		if (array == null || separator == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		if (array.length > 0) {
			result.append(array[0]);
			for (int i = 1; i < array.length; i++) {
				result.append(separator);
				result.append(array[i]);
			}
		}
		return result.toString();
	}

	/**
	 * Расширенное преобразование строки в Boolean.
	 */
	public static boolean stringToBoolean(final String s) {
		boolean b = false;
		String str = s.trim();
		if ("true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str)
				|| "1".equalsIgnoreCase(str)) {
			b = true;
		}
		return b;
	}

}
