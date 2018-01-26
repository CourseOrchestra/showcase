package ru.curs.showcase.core.frame;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.util.Description;

/**
 * Шлюз для получения фреймов главной страницы из источника данных Celesta.
 * 
 * @author anlug
 * 
 */
@Description(process = "Загрузка данных для фрейма на главной странице из скрипта celesta")
public class MainPageFrameCelestaGateway implements MainPageFrameGateway {

	private String fileName;

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		setSourceName(frameSource);
		return getRawData(context);
	}

	@Override
	public String getRawData(final CompositeContext aContext) {

		CelestaHelper<String> helper = new CelestaHelper<String>(aContext, String.class);
		String result = helper.runPython(getSourceName());
		return result;

	}

	@Override
	public void setSourceName(final String aName) {
		fileName = aName;
	}

	public String getSourceName() {
		return fileName;
	}

}
