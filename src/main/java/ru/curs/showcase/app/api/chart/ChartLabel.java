package ru.curs.showcase.app.api.chart;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Класс подписи для оси графика.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartLabel implements SerializableElement {

	private static final long serialVersionUID = 2659147474532450840L;

	/**
	 * Значение на оси графика, к которому привязана подпись. Для оси X как
	 * правило - порядковый номер подписи. Для оси Y - может быть дробным.
	 */
	private Double value;

	/**
	 * Текст подписи.
	 */
	private String text;

	public final Double getValue() {
		return value;
	}

	public final void setValue(final double aD) {
		value = aD;
	}

	public final String getText() {
		return text;
	}

	public ChartLabel() {
		super();
	}

	public ChartLabel(final double aValue, final String aText) {
		super();
		value = aValue;
		text = aText;
	}

	public final void setText(final String aText) {
		text = aText;
	}

}
