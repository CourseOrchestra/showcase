package ru.curs.showcase.app.api.chart;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelJSBasedElement;

/**
 * Данные для построения графика.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Chart extends DataPanelJSBasedElement {

	private static final long serialVersionUID = 8945028162073890091L;

	/**
	 * Динамические данные для графика, на основе которых строится JSON объект
	 * для передачи в JS функцию построения графика.
	 */
	private ChartData javaDynamicData;

	@Override
	public final ChartData getJavaDynamicData() {
		return javaDynamicData;
	}

	public final void setJavaDynamicData(final ChartData aData) {
		javaDynamicData = aData;
	}

	@Override
	public final ChartEventManager getEventManager() {
		return (ChartEventManager) super.getEventManager();
	}

	@Override
	protected ChartEventManager initEventManager() {
		return new ChartEventManager();
	}

	public Chart() {
		super();
	}

	public Chart(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}
}
