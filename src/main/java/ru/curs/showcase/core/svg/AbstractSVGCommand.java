package ru.curs.showcase.core.svg;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.OutputStreamDataFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Базовый класс команды экспорта из SVG.
 * 
 * @author den
 * 
 */
public abstract class AbstractSVGCommand extends ServiceLayerCommand<OutputStreamDataFile> {

	private String input;

	private final GeoMapExportSettings settings;

	private final ImageFormat imageFormat;

	public AbstractSVGCommand(final CompositeContext aContext,
			final GeoMapExportSettings aSettings, final ImageFormat aImageFormat,
			final String aInput) {
		super(aContext);
		input = aInput;
		settings = aSettings;
		imageFormat = aImageFormat;

		setResult(new OutputStreamDataFile());
		getResult().setName(
				getSettings().getFileName() + "." + getImageFormat().toString().toLowerCase());
	}

	@InputParam
	public String getInput() {
		return input;
	}

	@InputParam
	public GeoMapExportSettings getSettings() {
		return settings;
	}

	@InputParam
	public ImageFormat getImageFormat() {
		return imageFormat;
	}

	private String checkSVGEncoding(final String aSvg) {
		String preambula =
			aSvg.substring(0, XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8.length() * 2).toLowerCase();
		if (preambula.contains(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8)) {
			return aSvg;
		} else {
			return XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8 + "\n" + aSvg;
		}
	}

	@Override
	protected void mainProc() throws Exception {
		input = checkSVGEncoding(input);
	}

	@Override
	protected void logOutput() {
		super.logOutput();
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format("Размер скачиваемого файла: %d байт", getResult().getData()
					.size()));
		}
	}
}
