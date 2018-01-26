package ru.curs.showcase.core.frame;

import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Фабрика для создания фреймов главной формы. Отвечает за дополнительную
 * обработку полученного из шлюза текста.
 * 
 * @author den
 * 
 */
public final class MainPageFrameFactory {
	/**
	 * Исходный HTML текст.
	 */
	private String source;

	/**
	 * Результат работы фабрики.
	 */
	private String result;

	/**
	 * Признак того, что результат должен загружаться в IFrame.
	 */
	private final Boolean forIFrame;

	public MainPageFrameFactory(final Boolean aForIFrame) {
		super();
		forIFrame = aForIFrame;
	}

	/**
	 * Основной метод фабрики - обработка исходного HTML кода.
	 * 
	 * @param aSource
	 *            - исходный код.
	 */
	public String build(final String aSource) {
		if (aSource == null) {
			return null;
		}

		source = aSource;
		result = source;
		replaceTemplates();
		if (forIFrame) {
			convertToHTMLPage();
		}
		return result;
	}

	private void convertToHTMLPage() {
		result = "<html><head/><body>" + result + "</body></html>";
	}

	private void replaceTemplates() {
		result = UserDataUtils.replaceVariables(result);
	}

	public String getSource() {
		return source;
	}

	public String getResult() {
		return result;
	}

	public Boolean getForIFrame() {
		return forIFrame;
	}
}
