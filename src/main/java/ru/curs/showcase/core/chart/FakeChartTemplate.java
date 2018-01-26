package ru.curs.showcase.core.chart;

/**
 * Fake класс для вытаскивания типа графика из шаблона.
 * 
 * @author den
 * 
 */
public class FakeChartTemplate {
	/**
	 * plot.
	 */

	private FakeChartTemplatePlot[] plot;

	public FakeChartTemplatePlot[] getPlot() {
		return plot;
	}

	public void setPlot(final FakeChartTemplatePlot[] aPlot) {
		plot = aPlot;
	}

}
