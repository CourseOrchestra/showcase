package ru.curs.showcase.app.api.chart;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Настройки для серии графика. Сейчас содержат только цвет заливки, а в будущем
 * возможно добавление других настроек: stroke: {width: 2, color: "#f80"},
 * legend: “Some legend”.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class ChartSeriesOptions implements SerializableElement {

	private static final long serialVersionUID = -7841286885481015996L;

	/**
	 * Явно заданный цвет серии. Если NULL - используется автоматическая
	 * раскраска из шаблона.
	 */
	private String fill;

	public String getFill() {
		return fill;
	}

	public void setFill(final String aFill) {
		fill = aFill;
	}

}
