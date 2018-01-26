package ru.curs.showcase.app.api.html;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;

/**
 * Класс, содержащий данные для отрисовки элемента JsForm и необходимые
 * обработчики событий.
 * 
 * @author bogatov
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JsForm extends DataPanelElement {
	private static final long serialVersionUID = 1L;

	/**
	 * HTML данные для вставки на страницу.
	 */
	private String template;

	public JsForm() {
		super();
	}

	public JsForm(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	public JsForm(final String sTemplate) {
		super();
		this.template = sTemplate;
	}

	public JsForm(final String sTemplate, final DataPanelElementInfo aElInfo) {
		super(aElInfo);
		this.template = sTemplate;
	}

	@Override
	protected HTMLEventManager initEventManager() {
		return new HTMLEventManager();
	}

	@Override
	public HTMLEventManager getEventManager() {
		return (HTMLEventManager) super.getEventManager();
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(final String sTemplate) {
		this.template = sTemplate;
	}
}
