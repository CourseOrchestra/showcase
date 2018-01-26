package ru.curs.showcase.core.geomap;

import ru.curs.showcase.app.api.element.ChildPosition;
import ru.curs.showcase.app.api.geomap.GeoMapUISettings;
import ru.curs.showcase.core.ProfileBasedSettingsApplyStrategy;
import ru.curs.showcase.runtime.ProfileReader;
import ru.curs.showcase.util.exception.*;

/**
 * Стратегия применения настроек карты по умолчанию, основанная на считывании из
 * профайла.
 * 
 * @author den
 * 
 */
public class DefaultGeoMapSettingsApplyStrategy extends ProfileBasedSettingsApplyStrategy {

	private static final String BUTTONS_PANEL_POSITION_PROP = "buttons.panel.position";
	private static final String VISIBLE_EXPORTTOPNG_BUTTON_PROP = "exporttopng.button.visible";
	private static final String VISIBLE_EXPORTTOSVG_BUTTON_PROP = "exporttosvg.button.visible";
	private static final String VISIBLE_EXPORTTOJPG_BUTTON_PROP = "exporttojpg.button.visible";
	private static final String VISIBLE_BUTTONS_PANEL_PROP = "buttons.panel.visible";

	private final GeoMapUISettings uiSettings;
	private boolean profileExists = true;

	public DefaultGeoMapSettingsApplyStrategy(final GeoMapUISettings aUISettings) {
		super(new ProfileReader("default.properties", SettingsFileType.GEOMAP_PROPERTIES));
		try {
			reader().init();
		} catch (SettingsFileOpenException e) {
			profileExists = false;
		}
		uiSettings = aUISettings;
	}

	@Override
	protected void applyByDefault() {
		uiSettings.setButtonsPanelVisible(false);
		uiSettings.setExportToSVGButtonVisible(true);
		uiSettings.setButtonsPanelPosition(ChildPosition.TOP);
		uiSettings.setExportToJPGButtonVisible(true);
		uiSettings.setExportToPNGButtonVisible(true);
	}

	@Override
	protected void applyFromProfile() {
		if (!profileExists) {
			return;
		}
		Boolean boolValue = reader().getBoolValue(VISIBLE_BUTTONS_PANEL_PROP);
		if (boolValue != null) {
			uiSettings.setButtonsPanelVisible(boolValue);
		}
		boolValue = reader().getBoolValue(VISIBLE_EXPORTTOSVG_BUTTON_PROP);
		if (boolValue != null) {
			uiSettings.setExportToSVGButtonVisible(boolValue);
		}
		boolValue = reader().getBoolValue(VISIBLE_EXPORTTOPNG_BUTTON_PROP);
		if (boolValue != null) {
			uiSettings.setExportToPNGButtonVisible(boolValue);
		}
		boolValue = reader().getBoolValue(VISIBLE_EXPORTTOJPG_BUTTON_PROP);
		if (boolValue != null) {
			uiSettings.setExportToJPGButtonVisible(boolValue);
		}
		String value = reader().getStringValue(BUTTONS_PANEL_POSITION_PROP);
		if (value != null) {
			uiSettings.setButtonsPanelPosition(ChildPosition.valueOf(value));
		}
	}

}
