package ru.curs.showcase.core;

/**
 * Интерфейс для выбора источника документа - БД или файла по его имени.
 * 
 * 
 * @param <T>
 *            - интерфейс шлюза.
 */
public abstract class SourceSelector<T> {
	private String sourceName;

	public String getSourceName() {
		return sourceName;
	}

	public SourceSelector(final String aSourceName) {
		super();
		sourceName = aSourceName;
	}

	public abstract T getGateway();

	public void setSourceName(final String aSourceName) {
		sourceName = aSourceName;
	}

	public SourceSelector() {
		super();
	}

	/**
	 * Возвращает зафиксированное расширение для файлов, содержащих документ
	 * данного типа.
	 */
	protected String getFileExt() {
		return "xml";
	}

	private boolean isEmpty() {
		return (sourceName == null) || sourceName.isEmpty();
	}

	/**
	 * На данный момент случай отсутствия источника обрабатывается в шлюзе для
	 * БД (выступающего в роли шлюза по умолчанию). Это сделано, чтобы не
	 * плодить отдельный класс.
	 */
	public SourceType sourceType() {
		if (isEmpty()) {
			return SourceType.SP;
		}
		if (sourceName.endsWith("." + getFileExt())) {
			return SourceType.FILE;
		} else if (sourceName.endsWith(".py")) {
			return SourceType.JYTHON;
		} else if (sourceName.endsWith(".sql")) {
			return SourceType.SQL;
		} else if (sourceName.endsWith(".celesta")
				|| sourceName.endsWith(".cl")) {
			return SourceType.CELESTA;
		}
		return SourceType.SP;
	}
}
