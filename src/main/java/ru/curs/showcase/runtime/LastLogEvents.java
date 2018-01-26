package ru.curs.showcase.runtime;

import java.util.*;

/**
 * Очередь для последних событий, записанных в лог. Длина очереди
 * устанавливается в файле общих настроек приложения.
 * 
 * @author den
 * 
 */
public class LastLogEvents extends TreeSet<LoggingEventDecorator> {

	public static final String OUTPUT = "output";

	public static final String INTERNAL_LOG_SIZE = "web.console.size";
	public static final int DEF_MAX_RECORDS = 50;

	private static final long serialVersionUID = 9039619678848110139L;

	public static int getMaxRecords() {
		String res = UserDataUtils.getGeneralOptionalProp(INTERNAL_LOG_SIZE);
		return res == null ? DEF_MAX_RECORDS : Integer.parseInt(res);
	}

	public LastLogEvents() {
		this(new Comparator<LoggingEventDecorator>() {

			@Override
			public int compare(final LoggingEventDecorator event1,
					final LoggingEventDecorator event2) {
				int res = (int) (event2.getTimeStamp() - event1.getTimeStamp());
				if (res == 0) {
					res = 1;
					// нельзя, чтобы события перезаписывали друг друга
				}
				return res;
			}
		});
	}

	private LastLogEvents(final Comparator<? super LoggingEventDecorator> aComparator) {
		super(aComparator);
	}

	@Override
	public boolean add(final LoggingEventDecorator event) {
		boolean res = super.add(event);
		if (size() > getMaxRecords()) {
			pollLast();
		}
		return res;
	}

}
