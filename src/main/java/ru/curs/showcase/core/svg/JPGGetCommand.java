package ru.curs.showcase.core.svg;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.util.SVGConvertor;

/**
 * Команда получения JPG образа карты.
 * 
 * @author den
 * 
 */
public final class JPGGetCommand extends AbstractSVGCommand {

	public JPGGetCommand(final CompositeContext aContext, final GeoMapExportSettings aSettings,
			final String aInput) {
		super(aContext, aSettings, ImageFormat.JPG, aInput);
	}

	@Override
	protected void mainProc() throws Exception {
		super.mainProc();
		SVGConvertor convertor = new SVGConvertor(getSettings());
		getResult().setData(convertor.svgStringToJPG(getInput()));
	}
}
