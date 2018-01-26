/**
 * 
 */
package ru.curs.showcase.core.html.jsForm;

import java.io.ByteArrayInputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.JsForm;
import ru.curs.showcase.core.html.*;

/**
 * Фабрика для создания объектов WebText.
 * 
 * @author den
 * 
 */
public class JsFormFactory {
	/**
	 * Результат работы фабрики.
	 */
	private final JsForm result;
	private final JsFormData data;
	private final HTMLBasedElementFactory htmlFactory;

	public JsFormFactory(final JsFormData jsFormData, final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		this.data = jsFormData;
		this.result = new JsForm(jsFormData.getData(), elementInfo);
		String setting = data.getSetting();
		HTMLBasedElementRawData rawdata = new HTMLBasedElementRawData(null,
				(setting != null && !setting.isEmpty())
						? new ByteArrayInputStream(setting.getBytes()) : null,
				elementInfo, context);
		this.htmlFactory = new HTMLBasedElementFactory(rawdata) {

			@Override
			protected void transformData() {
				// StandartXMLTransformer transformer = new
				// StandartXMLTransformer(getSource());
				// String data = transformer.transform();
				String template = replaceVariables(data.getData());
				result.setTemplate(template);
			}

			@Override
			public DataPanelElement getResult() {
				return result;
			}

			@Override
			protected void correctSettingsAndData() {
			}

			@Override
			protected void initResult() {
			}

		};

	}

	public JsForm build() throws Exception {
		return (JsForm) this.htmlFactory.build();
	}

}
