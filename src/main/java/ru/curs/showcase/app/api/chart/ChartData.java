package ru.curs.showcase.app.api.chart;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.element.Size;

/**
 * Динамические данные для графика, передаваемые непосредственно в функцию
 * отрисовки графика. Замечание по поводу подписей: может быть задан либо набор
 * подписей labelsY, либо функция labelYfunc. Приоритет имеет набор подписей.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartData extends Size implements SerializableElement {

	private static final long serialVersionUID = -2570277058263361565L;

	/**
	 * Набор серий с данными, подписями и некоторыми дополнительными
	 * настройками.
	 */
	private List<ChartSeries> series = new ArrayList<ChartSeries>();

	/**
	 * Набор подписей для оси X.
	 */
	private List<ChartLabel> labelsX = new ArrayList<ChartLabel>();

	/**
	 * Набор подписей для оси Y.
	 */
	private List<ChartLabel> labelsY = new ArrayList<ChartLabel>();

	public final void setLabelsY(final List<ChartLabel> aLabelsY) {
		labelsY = aLabelsY;
	}

	public final List<ChartSeries> getSeries() {
		return series;
	}

	public final void setSeries(final List<ChartSeries> aSeries) {
		series = aSeries;
	}

	public final List<ChartLabel> getLabelsX() {
		return labelsX;
	}

	public final void setLabelsX(final List<ChartLabel> aLabelsX) {
		labelsX = aLabelsX;
	}

	public final List<ChartLabel> getLabelsY() {
		return labelsY;
	}

	/**
	 * Возвращает объект подписи для оси Y по его значению.
	 * 
	 * @param aY
	 *            - значение Y.
	 * @return - подпись.
	 */
	public ChartLabel getLabelsYByValue(final double aY) {
		final double floatError = .0000001;
		for (ChartLabel cur : labelsY) {
			if (Math.abs(aY - cur.getValue().doubleValue()) < floatError) {
				return cur;
			}
		}
		return null;
	}

	/**
	 * Возвращает серию по ее ID.
	 * 
	 * @param seriesName
	 *            - ID серии.
	 * @return - серию или null.
	 */
	public ChartSeries getSeriesById(final String seriesName) {
		if (seriesName == null) {
			return null;
		}
		for (ChartSeries current : series) {
			if (seriesName.equals(current.getName())) {
				return current;
			}
		}
		return null;
	}
}
