/**
 * 
 */
package ru.curs.showcase.core.html.webtext;

import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.core.html.*;

/**
 * Фабрика для создания объектов WebText.
 * 
 * @author den
 * 
 */
public class WebTextFactory extends HTMLBasedElementFactory {
	/**
	 * Результат работы фабрики.
	 */
	private WebText result;

	public WebTextFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public WebText build() throws Exception {
		return (WebText) super.build();
	}

	@Override
	public DataPanelElement getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new WebText(getElementInfo());
	}

	@Override
	protected void transformData() {
		StandartXMLTransformer transformer = new StandartXMLTransformer(getSource());
		String data = transformer.transform();
		data = replaceVariables(data);
		result.setData(data);
	}

	@Override
	protected String replaceVariables(final String in) {
		String out = in.replace("_figurnayaskobka_", "{");
		out = super.replaceVariables(out);

		return out;
	}

	@Override
	protected void correctSettingsAndData() {
		// не используется
	}
}
