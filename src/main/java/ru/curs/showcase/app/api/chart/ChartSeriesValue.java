package ru.curs.showcase.app.api.chart;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Значение серии в графике. Кроме собственно значения содержит также подпись и
 * текст для круговой диаграммы.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class ChartSeriesValue implements SerializableElement {

	private static final long serialVersionUID = -7483328757902171422L;

	/**
	 * Значение серии.
	 */
	private Double y;

	/**
	 * Подсказка к значению серии.
	 */
	private String tooltip;

	/**
	 * Подпись к легенде для значения на круговой диаграмме. Для других типов
	 * диаграмм также заполняется на сервере, но игнорируется компонентой.
	 */
	private String legend;

	public ChartSeriesValue(final Double aValue) {
		y = aValue;
	}

	public ChartSeriesValue() {
		super();
	}

	public Double getY() {
		return y;
	}

	public void setY(final Double aY) {
		y = aY;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(final String aToolTip) {
		tooltip = aToolTip;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(final String aText) {
		legend = aText;
	}

}
