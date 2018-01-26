package ru.curs.showcase.app.api.chart;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Класс серии графика.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartSeries implements SerializableElement {

	private static final long serialVersionUID = -3989798207026304906L;

	/**
	 * Название серии.
	 */
	private String name;

	/**
	 * Дополнительные настройки серии.
	 */
	private ChartSeriesOptions options = null;

	/**
	 * Массив с данными серии. Вместо отсутствующих данных должны быть null
	 * значения. Используем строки, а не числа, т.к. в JS дробное число всегда
	 * должно иметь "." несмотря на региональные настройки.
	 */
	private List<ChartSeriesValue> data = new ArrayList<ChartSeriesValue>();

	public final String getName() {
		return name;
	}

	public final void setName(final String aName) {
		name = aName;
	}

	/**
	 * Добавляет объект значения к серии и устанавливает у него значение Y.
	 * 
	 * @param aValue
	 *            - числовое значение.
	 */
	public void addValue(final Double aValue) {
		ChartSeriesValue value = new ChartSeriesValue(aValue);
		data.add(value);
	}

	public ChartSeriesOptions getOptions() {
		return options;
	}

	public void setOptions(final ChartSeriesOptions aOptions) {
		options = aOptions;
	}

	/**
	 * Устанавливает цвет серии, при необходимости создавая options.
	 * 
	 * @param color
	 *            - новый цвет.
	 */
	public void setColor(final String color) {
		if (options == null) {
			options = new ChartSeriesOptions();
		}
		options.setFill(color);
	}

	/**
	 * Возвращает цвет серии, если он задан.
	 * 
	 * @return - цвет серии.
	 */
	public String getColor() {
		if (options != null) {
			return options.getFill();
		}
		return null;
	}

	public final List<ChartSeriesValue> getData() {
		return data;
	}

	public final void setData(final List<ChartSeriesValue> aData) {
		data = aData;
	}
}
