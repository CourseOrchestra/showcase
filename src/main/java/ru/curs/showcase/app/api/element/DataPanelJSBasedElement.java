package ru.curs.showcase.app.api.element;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.JSONObject;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;

/**
 * Класс элемента панели управления с легендой. Легенда содержит информацию об
 * отображаемых в элементе графических данных. Класс используется для типов
 * элементов CHART и MAP.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class DataPanelJSBasedElement extends DataPanelCompBasedElement implements
		JSONObject {

	private static final long serialVersionUID = 652329350772736896L;

	/**
	 * Позиция легенды относительно графика.
	 */
	private ChildPosition legendPosition;

	/**
	 * Шаблон элемента. В текущей реализации представляет собой строку с JSON
	 * объектом. Передается в JS компоненту, отрисовывающую элемент.
	 */
	private String template;

	/**
	 * JSON объект с динамическими данными для использования в JS коде.
	 */
	private String jsDynamicData;

	public final String getJsDynamicData() {
		return jsDynamicData;
	}

	@Override
	public final void setJsDynamicData(final String aJsDynamicData) {
		jsDynamicData = aJsDynamicData;
	}

	public final ChildPosition getLegendPosition() {
		return legendPosition;
	}

	public final void setLegendPosition(final ChildPosition aLegendPosition) {
		legendPosition = aLegendPosition;
	}

	public final String getTemplate() {
		return template;
	}

	public final void setTemplate(final String aTemplate) {
		template = aTemplate;
	}

	public DataPanelJSBasedElement() {
		super();
	}

	public DataPanelJSBasedElement(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

}
