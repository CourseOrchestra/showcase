package ru.curs.showcase.app.api.geomap;

import ru.curs.showcase.app.api.SerializableElement;
import ru.curs.showcase.app.api.element.ChildPosition;

/**
 * Настройки внешнего вида грида.
 * 
 * @author den
 * 
 */
public final class GeoMapUISettings implements SerializableElement {

	static final long serialVersionUID = -6125271614968692225L;

	public GeoMapUISettings() {
		super();
	}

	private Boolean buttonsPanelVisible;

	private Boolean exportToSVGButtonVisible;

	private Boolean exportToJPGButtonVisible;

	private Boolean exportToPNGButtonVisible;

	private ChildPosition buttonsPanelPosition;

	public Boolean getButtonsPanelVisible() {
		return buttonsPanelVisible;
	}

	public void setButtonsPanelVisible(final Boolean aButtonsPanelVisible) {
		buttonsPanelVisible = aButtonsPanelVisible;
	}

	public Boolean getExportToSVGButtonVisible() {
		return exportToSVGButtonVisible;
	}

	public void setExportToSVGButtonVisible(final Boolean aExportToSVGButtonVisible) {
		exportToSVGButtonVisible = aExportToSVGButtonVisible;
	}

	public ChildPosition getButtonsPanelPosition() {
		return buttonsPanelPosition;
	}

	public void setButtonsPanelPosition(final ChildPosition aButtonsPanelPosition) {
		buttonsPanelPosition = aButtonsPanelPosition;
	}

	public Boolean getExportToJPGButtonVisible() {
		return exportToJPGButtonVisible;
	}

	public void setExportToJPGButtonVisible(final Boolean aExportToJPGButtonVisible) {
		exportToJPGButtonVisible = aExportToJPGButtonVisible;
	}

	public Boolean getExportToPNGButtonVisible() {
		return exportToPNGButtonVisible;
	}

	public void setExportToPNGButtonVisible(final Boolean aExportToPNGButtonVisible) {
		exportToPNGButtonVisible = aExportToPNGButtonVisible;
	}

}
